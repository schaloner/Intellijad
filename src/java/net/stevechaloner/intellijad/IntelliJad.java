package net.stevechaloner.intellijad;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.config.ConfigComponent;
import net.stevechaloner.intellijad.decompilers.DecompilationChoiceListener;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptor;
import net.stevechaloner.intellijad.decompilers.Decompiler;
import net.stevechaloner.intellijad.decompilers.DiskDecompiler;
import net.stevechaloner.intellijad.decompilers.MemoryDecompiler;
import net.stevechaloner.intellijad.util.PluginHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.io.File;

public class IntelliJad implements ProjectComponent,
                                   DecompilationChoiceListener
{

    public static final String COMPONENT_NAME = "net.stevechaloner.idea";

    private final FileEditorManagerListener navigationListener;

    private final Project project;

    public IntelliJad(Project project)
    {
        this.project = project;
        navigationListener = new NavigationDecompileListener(project,
                                                             this);
    }

    // javadoc inherited
    @NotNull
    public String getComponentName()
    {
        return COMPONENT_NAME;
    }

    // javadoc inherited
    public void projectOpened()
    {
        FileEditorManager.getInstance(project).addFileEditorManagerListener(navigationListener);
    }

    // javadoc inherited
    public void projectClosed()
    {
        FileEditorManager.getInstance(project).removeFileEditorManagerListener(navigationListener);
    }

    // javadoc inherited
    public void initComponent()
    {
    }

    // javadoc inherited
    public void disposeComponent()
    {
    }

    // javadoc inherited
    public void decompile(DecompilationDescriptor decompilationDescriptor)
    {
        ConfigComponent configComponent = PluginHelper.getComponent(project,
                                                                    ConfigComponent.class);
        Config config = configComponent.getConfig();
        StringBuilder sb = new StringBuilder();
        String jadPath = config.getJadPath();
        try
        {
            validateJadPath(jadPath);
            sb.append(jadPath).append(' ');
            sb.append(config.renderCommandLinePropertyDescriptors());
            if (config.isDecompileToMemory())
            {
                decompileToMemory(decompilationDescriptor);
            }
            else
            {
                decompileToDisk(decompilationDescriptor);
            }
        }
        catch (IllegalArgumentException e)
        {
            JOptionPane.showMessageDialog(new JLabel(),
                                          e.getMessage());
        }
    }

    /**
     * @param decompilationDescriptor
     */
    private void decompileToDisk(DecompilationDescriptor decompilationDescriptor)
    {
        Decompiler decompiler = new DiskDecompiler();
        decompiler.decompile(decompilationDescriptor,
                             null);
    }

    /**
     * @param decompilationDescriptor
     */
    private void decompileToMemory(DecompilationDescriptor decompilationDescriptor)
    {
        Decompiler decompiler = new MemoryDecompiler();
        decompiler.decompile(decompilationDescriptor,
                             null);
    }

    private void validateJadPath(String path) throws IllegalArgumentException
    {
        if (path == null || path.trim().length() == 0)
        {
            throw new IllegalArgumentException(IntelliJadResourceBundle.message("error.unspecified-jad-path"));
        }
        else
        {
            File f = new File(path);
            if (!f.exists())
            {
                throw new IllegalArgumentException(IntelliJadResourceBundle.message("error.non-existant-jad-path",
                                                                                    path));
            }
            else if (!f.isFile())
            {
                throw new IllegalArgumentException(IntelliJadResourceBundle.message("error.invalid-jad-path",
                                                                                    path));
            }
        }
    }
}
