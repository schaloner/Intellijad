package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import net.stevechaloner.intellijad.IntelliJadConstants;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFile;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

/**
 * @author Steve Chaloner
 */
public class MemoryDecompiler extends AbstractDecompiler
{
    protected OperationStatus setup(DecompilationDescriptor descriptor,
                                    DecompilationContext context) throws DecompilationException
    {
        return OperationStatus.CONTINUE;
    }

    // javadoc inherited
    protected VirtualFile processOutput(@NotNull final DecompilationDescriptor descriptor,
                                        @NotNull final DecompilationContext context,
                                        @NotNull final String content) throws DecompilationException
    {
        final MemoryVirtualFileSystem vfs = (MemoryVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(MemoryVirtualFileSystem.PROTOCOL);
        MemoryVirtualFile file = new MemoryVirtualFile(descriptor.getClassName() + ".java",
                                                       content);
        vfs.addFile(file);

        Project project = context.getProject();
        MemoryVirtualFile showdom = vfs.getFileByPackage(descriptor.getPackageName());
        showdom.addChild(file);

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
                        found = IntelliJadConstants.INTELLIJAD_ROOT.equals(urls[i]);
                    }
                    if (!found)
                    {
                        model.addRoot(vfs.findFileByPath("root"),
                                      OrderRootType.SOURCES);
                        model.commit();
                    }
                }
            });
            FileEditorManager.getInstance(project).openFile(file,
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

        return file;
    }

    // javadoc inherited
    protected void updateCommand(StringBuilder command)
    {
        command.append(" -p ");
    }

    /**
     * Calcuates the success of the process execution.
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
                if (err.size() > 0 && output.size() > 0)
                {
                    resultType = ResultType.NON_FATAL_ERROR;
                }
                else if (err.size() > 0)
                {
                    resultType = ResultType.FATAL_ERROR;
                }
                break;
            default:
                resultType = ResultType.FATAL_ERROR;

        }
        return resultType;
    }
}
