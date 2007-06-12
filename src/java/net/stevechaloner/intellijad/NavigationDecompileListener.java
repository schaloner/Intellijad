package net.stevechaloner.intellijad;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import net.stevechaloner.intellijad.actions.DecompileDialog;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.config.ConfigComponent;
import net.stevechaloner.intellijad.config.ExclusionTableModel;
import net.stevechaloner.intellijad.config.NavigationTriggeredDecompile;
import net.stevechaloner.intellijad.util.PluginHelper;
import net.stevechaloner.intellijad.util.SwingUtil;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * @author Steve Chaloner
 */
public class NavigationDecompileListener implements FileEditorManagerListener
{
    /**
     * The current project.
     */
    private final Project project;

    /**
     * The decompilation listener.
     */
    private final DecompilationChoiceListener decompilationListener;

    /**
     * Initialises a new instance of this class.
     *
     * @param project               the current project
     * @param decompilationListener the decompilation listener
     */
    NavigationDecompileListener(Project project,
                                DecompilationChoiceListener decompilationListener)
    {
        this.project = project;
        this.decompilationListener = decompilationListener;
    }

    // javadoc inherited
    public void fileOpened(FileEditorManager fileEditorManager,
                           VirtualFile file)
    {
        if (file != null && "class".equals(file.getExtension()))
        {
            ConfigComponent configComponent = PluginHelper.getComponent(project,
                                                                        ConfigComponent.class);
            if (configComponent != null)
            {
                Config config = configComponent.getConfig();
                if (config != null)
                {
                    boolean excluded = isExcluded(config,
                                                  file);
                    switch (NavigationTriggeredDecompile.getByName(config.getConfirmNavigationTriggeredDecompile()))
                    {
                        case ALWAYS:
                            if (!excluded)
                            {
                                decompilationListener.decompile();
                            }
                            break;
                        case ASK:
                            if (!excluded)
                            {
                                DecompileDialog dialog = new DecompileDialog(file.getNameWithoutExtension(),
                                                                             getPackageName(file),
                                                                             project,
                                                                             decompilationListener);
                                dialog.pack();
                                SwingUtil.center(dialog);
                                dialog.setVisible(true);
                            }
                            break;
                        case NEVER:
                            JOptionPane.showMessageDialog(new JLabel(),
                                                          "NEVER");
                            break;
                    }
                }
            }
        }
    }

    private boolean isExcluded(Config config,
                               VirtualFile file)
    {
        ExclusionTableModel exclusionModel = config.getExclusionTableModel();
        String packageName = getPackageName(file);
        boolean exclude = false;
        for (int i = 0; !exclude && i < exclusionModel.getRowCount(); i++)
        {
            String pn = (String) exclusionModel.getValueAt(i, 0);
            if (pn != null)
            {
                exclude = packageName.equals(pn) || (packageName.startsWith(pn) && (Boolean) exclusionModel.getValueAt(i, 1));
            }
        }
        return exclude;
    }

    private String getPackageName(VirtualFile file)
    {
        String path = file.getPath();
        int index = path.indexOf("!");
        String packageName = null;
        if (index != -1)
        {
            String virtualPath = path.substring(index + 1);
            if (virtualPath != null && virtualPath.length() > 0)
            {
                if (virtualPath.charAt(0) == '/')
                {
                    virtualPath = virtualPath.substring(1);
                }
                int lastIndex = virtualPath.lastIndexOf("/");
                if (lastIndex != -1)
                {
                    virtualPath = virtualPath.substring(0, lastIndex);
                }
                packageName = virtualPath.replaceAll("/", ".");
            }
        }
        return packageName;
    }

    // javadoc inherited
    public void fileClosed(FileEditorManager fileEditorManager, VirtualFile virtualFile)
    {
        // no-op
    }

    // javadoc inherited
    public void selectionChanged(FileEditorManagerEvent fileEditorManagerEvent)
    {
        // no-op
    }
}
