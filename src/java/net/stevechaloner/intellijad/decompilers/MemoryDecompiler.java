package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryUtil;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import net.stevechaloner.intellijad.IntelliJadConstants;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFile;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipFile;

/**
 * @author Steve Chaloner
 */
public class MemoryDecompiler implements Decompiler
{
    // javadoc inherited
    public void decompile(DecompilationDescriptor descriptor,
                          DecompilationContext context) throws DecompilationException
    {
        StringBuilder command = new StringBuilder(context.getCommand());
        command.append(" -p ");
        VirtualFile jarFile = descriptor.getJarFile();
        if (jarFile != null)
        {
            extractClassFiles(jarFile,
                              context,
                              descriptor);
            File targetClass = new File(context.getTargetDirectory(),
                                        descriptor.getClassName() + '.' + descriptor.getExtension());
            command.append(targetClass.getAbsolutePath());
            context.getConsole().appendToConsole(command.toString());

            try
            {
                Process process = Runtime.getRuntime().exec(command.toString());
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ByteArrayOutputStream err = new ByteArrayOutputStream();
                StreamMonitor outputMonitor = new StreamMonitor(process.getInputStream(),
                                                                output);
                Thread outputThread = new Thread(outputMonitor);
                outputThread.start();
                StreamMonitor errMonitor = new StreamMonitor(process.getErrorStream(),
                                                             err);
                Thread errThread = new Thread(errMonitor);
                errThread.start();
                int exitCode = process.waitFor();
                outputMonitor.stop();
                errMonitor.stop();
                if (exitCode == 0)
                {
                    final MemoryVirtualFileSystem vfs = (MemoryVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(MemoryVirtualFileSystem.PROTOCOL);
                    MemoryVirtualFile file = new MemoryVirtualFile(descriptor.getClassName() + ".java",
                                                                   new String(output.toByteArray()));
                    vfs.addFile(file);

                    Project project = context.getProject();
                    final Library lib = LibraryUtil.findLibraryByClass(descriptor.getFullyQualifiedName(),
                                                                       project);
                    MemoryVirtualFile showdom = vfs.getFileByPackage(descriptor.getPackageName());
                    showdom.addChild(file);

                    if (lib != null)
                    {
                        ApplicationManager.getApplication().runWriteAction(new Runnable()
                        {

                            public void run()
                            {
                                Library.ModifiableModel model = lib.getModifiableModel();
                                model.addRoot(vfs.findFileByPath("root"),
                                              OrderRootType.SOURCES);
                                model.commit();
                            }
                        });
                        FileEditorManager.getInstance(project).openFile(file,
                                                                        true);


                        project.getUserData(IntelliJadConstants.GENERATED_SOURCE_LIBRARIES).add(lib);
                    }
                }
                else
                {
                    if (err.size() > 0)
                    {
                        System.out.println("Error: " + new String(err.toByteArray()));
                    }
                }

                context.getTargetDirectory().delete();
            }
            catch (IOException e)
            {
                throw new DecompilationException(e);
            }
            catch (InterruptedException e)
            {
                throw new DecompilationException(e);
            }
        }
    }

    /**
     * Extract the class files from the library to the target directory.
     *
     * @param jarFile                 the library containing the class files
     * @param context                 the context
     * @param decompilationDescriptor the decompilation descriptor
     * @throws DecompilationException if an error occurs extracting the class files
     */
    private void extractClassFiles(VirtualFile jarFile,
                                   DecompilationContext context,
                                   DecompilationDescriptor decompilationDescriptor) throws DecompilationException
    {
        try
        {
            ZipFile lib = JarFileSystem.getInstance().getJarFile(jarFile);
            context.getConsole().appendToConsole(IntelliJadResourceBundle.message("message.examining",
                                                                                  jarFile.getPath()));
            ZipExtractor zipExtractor = new ZipExtractor();
            zipExtractor.extract(context,
                                 lib,
                                 decompilationDescriptor.getPackageNameAsPath(),
                                 decompilationDescriptor.getClassName());
        }
        catch (IOException e)
        {
            throw new DecompilationException(e);
        }
    }

    /**
     * Monitors input streams to prevent them blocking.
     */
    private class StreamMonitor implements Runnable
    {
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private boolean monitor = true;

        /**
         * @param inputStream
         * @param outputStream
         */
        public StreamMonitor(@NotNull InputStream inputStream,
                             @NotNull OutputStream outputStream)
        {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        // javadoc inherited
        public void run()
        {
            try
            {
                while (monitor)
                {
                    while (inputStream.available() > 0)
                    {
                        StreamUtil.copyStreamContent(inputStream,
                                                     outputStream);
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace(System.err);
            }
        }

        public void stop()
        {
            monitor = false;
        }
    }
}
