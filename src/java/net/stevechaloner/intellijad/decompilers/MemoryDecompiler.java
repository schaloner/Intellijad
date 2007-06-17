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

import java.io.ByteArrayOutputStream;

/**
 * @author Steve Chaloner
 */
public class MemoryDecompiler extends AbstractDecompiler
{
    // javadoc inherited
    protected VirtualFile processOutput(DecompilationDescriptor descriptor,
                                        DecompilationContext context,
                                        ByteArrayOutputStream content) throws DecompilationException
    {
        final MemoryVirtualFileSystem vfs = (MemoryVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(MemoryVirtualFileSystem.PROTOCOL);
        MemoryVirtualFile file = new MemoryVirtualFile(descriptor.getClassName() + ".java",
                                                       new String(content.toByteArray()));
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
                        found = "intellijad://root".equals(urls[i]);
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
}
