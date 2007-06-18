package net.stevechaloner.intellijad;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.console.IntelliJadConsole;
import net.stevechaloner.intellijad.decompilers.DecompilationChoiceListener;
import net.stevechaloner.intellijad.decompilers.DecompilationContext;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptor;
import net.stevechaloner.intellijad.decompilers.DecompilationException;
import net.stevechaloner.intellijad.decompilers.Decompiler;
import net.stevechaloner.intellijad.decompilers.DiskDecompiler;
import net.stevechaloner.intellijad.decompilers.MemoryDecompiler;
import net.stevechaloner.intellijad.util.PluginHelper;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class IntelliJad implements ApplicationComponent,
                                   DecompilationChoiceListener,
                                   ProjectManagerListener
{
    /**
     *
     */
    public static final String COMPONENT_NAME = "net.stevechaloner.intellijad.IntelliJad";

    /**
     *
     */
    public static final String INTELLIJAD = "IntelliJad";

    /**
     * The reporting console.
     */
    private IntelliJadConsole console;

    // javadoc inherited
    @NotNull
    public String getComponentName()
    {
        return COMPONENT_NAME;
    }

    public void projectOpened(Project project)
    {
        console = new IntelliJadConsole();
        project.putUserData(IntelliJadConstants.GENERATED_SOURCE_LIBRARIES,
                            new ArrayList<Library>());

        NavigationDecompileListener navigationListener = new NavigationDecompileListener(project,
                                                                                         this);
        FileEditorManager.getInstance(project).addFileEditorManagerListener(navigationListener);
        project.putUserData(IntelliJadConstants.DECOMPILE_LISTENER,
                            navigationListener);
    }

    public boolean canCloseProject(Project project)
    {
        // no-op
        return true;
    }

    public void projectClosed(Project project)
    {
        NavigationDecompileListener listener = project.getUserData(IntelliJadConstants.DECOMPILE_LISTENER);
        FileEditorManager.getInstance(project).removeFileEditorManagerListener(listener);
        console.disposeConsole();
    }

    public void projectClosing(final Project project)
    {
        ApplicationManager.getApplication().runWriteAction(new Runnable()
        {
            public void run()
            {
                List<Library> list = project.getUserData(IntelliJadConstants.GENERATED_SOURCE_LIBRARIES);
                for (Library library : list)
                {
                    Library.ModifiableModel model = library.getModifiableModel();
                    VirtualFile[] files = model.getFiles(OrderRootType.SOURCES);
                    for (VirtualFile file : files)
                    {
                        if (file instanceof MemoryVirtualFile && file.getParent() == null)
                        {
                            model.removeRoot(file.getUrl(),
                                             OrderRootType.SOURCES);
                        }
                    }
                    model.commit();
                }
            }
        });
    }

    // javadoc inherited
    public void initComponent()
    {
        ProjectManager.getInstance().addProjectManagerListener(this);
    }

    // javadoc inherited
    public void disposeComponent()
    {
        ProjectManager.getInstance().removeProjectManagerListener(this);
    }

    // javadoc inherited
    public void decompile(DecompilationDescriptor descriptor)
    {
        Config config = PluginHelper.getConfig();
        Project project = PluginHelper.getProject();

        String jadPath = config.getJadPath();
        console.openConsole();
        if (validateJadPath(jadPath))
        {
            StringBuilder sb = new StringBuilder();
            sb.append(jadPath).append(' ');
            sb.append(config.renderCommandLinePropertyDescriptors());
            DecompilationContext context = new DecompilationContext(project,
                                                                    console,
                                                                    sb.toString());
            Decompiler decompiler = (config.isDecompileToMemory()) ? new MemoryDecompiler() : new DiskDecompiler();
            try
            {
                VirtualFile decompiledFile = decompiler.decompile(descriptor,
                                                                  context);
                if (decompiledFile != null)
                {
                    FileEditorManager.getInstance(project).closeFile(descriptor.getClassFile());
                    FileEditorManager.getInstance(project).openFile(decompiledFile,
                                                                    true);
                }
            }
            catch (DecompilationException e)
            {
                console.appendToConsole(e.getMessage());
            }
        }
    }

    /**
     * Validate the path to Jad as valid.
     *
     * @param path the path to Jad
     * @return true iff the path is ok
     */
    private boolean validateJadPath(String path)
    {
        String message = null;
        if (path == null || path.trim().length() == 0)
        {
            message = IntelliJadResourceBundle.message("error.unspecified-jad-path");
        }
        else
        {
            File f = new File(path);
            if (!f.exists())
            {
                message = IntelliJadResourceBundle.message("error.non-existant-jad-path",
                                                           path);
            }
            else if (!f.isFile())
            {
                message = IntelliJadResourceBundle.message("error.invalid-jad-path",
                                                           path);
            }
        }
        if (message != null)
        {
            console.appendToConsole(message);
        }
        return message == null;
    }

    /**
     * Get the logger for this plugin.
     *
     * @return the logger
     */
    public static Logger getLogger()
    {
        return Logger.getInstance(INTELLIJAD);
    }
}