package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import net.stevechaloner.intellijad.IntelliJadConstants;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.util.PluginUtil;

import java.io.File;
import java.io.ByteArrayOutputStream;

import org.jetbrains.annotations.NotNull;

/**
 * @author Steve Chaloner
 */
public class FileSystemDecompiler extends AbstractDecompiler
{
    // javadoc inherited
    protected OperationStatus setup(DecompilationDescriptor descriptor,
                                    DecompilationContext context) throws DecompilationException
    {
        final Config config = PluginUtil.getConfig();
        final File outputDirectory = new File(config.getOutputDirectory());
        final boolean outputDirExists = outputDirectory.exists();
        OperationStatus status = OperationStatus.CONTINUE;
        if (!outputDirExists && config.isCreateOutputDirectory())
        {
            if (!outputDirectory.mkdirs())
            {
                status = OperationStatus.ABORT;
                context.getConsole().appendToConsole(IntelliJadResourceBundle.message("error.could-not-create-output-directory",
                                                                                      config.getOutputDirectory()));
            }
        }
        else if (!outputDirExists)
        {
            status = OperationStatus.ABORT;
            context.getConsole().appendToConsole(IntelliJadResourceBundle.message("error.non-existant-output-directory",
                                                                                  config.getOutputDirectory()));
        }
        return status;
    }

    // javadoc inherited
    protected VirtualFile processOutput(@NotNull final DecompilationDescriptor descriptor,
                                        @NotNull final DecompilationContext context,
                                        @NotNull final String content) throws DecompilationException
    {
        final Project project = context.getProject();
        final LocalFileSystem vfs = (LocalFileSystem)VirtualFileManager.getInstance().getFileSystem(LocalFileSystem.PROTOCOL);
        final Config config = PluginUtil.getConfig();
        final File td = new File(config.getOutputDirectory());
        final VirtualFile targetDirectory = vfs.findFileByIoFile(td);
        final VirtualFile[] fileContainer = new VirtualFile[1];
        ApplicationManager.getApplication().runWriteAction(new Runnable()
        {
            public void run()
            {
                fileContainer[0] = vfs.refreshAndFindFileByIoFile(new File(td,
                                                                           descriptor.getPackageNameAsPath() +
                                                                           descriptor.getClassName() +
                                                                           IntelliJadConstants.DOT_JAVA_EXTENSION));

                final Library lib = LibraryUtil.findLibraryByClass(descriptor.getFullyQualifiedName(),
                                                                   project);
                if (lib != null)
                {
                    ApplicationManager.getApplication().runWriteAction(new Runnable()
                    {
                        public void run()
                        {
                            Library.ModifiableModel model = lib.getModifiableModel();
                            String[] urls = model.getUrls(OrderRootType.SOURCES);
                            boolean found = false;
                            for (int i = 0; !found && i < urls.length; i++)
                            {
                                found = targetDirectory.getUrl().equals(urls[i]);
                            }
                            if (!found)
                            {
                                model.addRoot(targetDirectory,
                                              OrderRootType.SOURCES);
                                model.commit();
                            }
                        }
                    });
                    FileEditorManager.getInstance(project).openFile(fileContainer[0],
                                                                    true);


                    project.getUserData(IntelliJadConstants.GENERATED_SOURCE_LIBRARIES).add(lib);
                    context.getConsole().appendToConsole(IntelliJadResourceBundle.message("message.associating-source-with-library",
                                                                                          descriptor.getClassName(),
                                                                                          lib.getName()));
                }
                else
                {
                    context.getConsole().appendToConsole(IntelliJadResourceBundle.message("message.library-not-found-for-class",
                                                                                          descriptor.getClassName()));
                }
            }
        });


        return fileContainer[0];
    }

    // javadoc inherited
    protected void updateCommand(StringBuilder builder)
    {
        builder.append(" -o -r ");
    }

    /**
     * For some reason, Jad outputs info messages on the error stream
     * when decompiling to disk!
     *
     * @param exitCode the exit code of the process
     * @param err      the error stream of the process
     * @param output   the output of the process
     * @return a result based on the execution of the process
     */
    protected ResultType checkDecompilationStatus(int exitCode,
                                                  ByteArrayOutputStream err,
                                                  ByteArrayOutputStream output)
    {
        ResultType resultType = ResultType.SUCCESS;
        switch (exitCode)
        {
            case 0:
                if (err.size() > 0)
                {
                    resultType = ResultType.NON_FATAL_ERROR;
                }
                break;
            default:
                resultType = ResultType.FATAL_ERROR;

        }
        return resultType;
    }
}
