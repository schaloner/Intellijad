package net.stevechaloner.intellijad;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import net.stevechaloner.intellijad.actions.DecompileDialog;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.config.ExclusionTableModel;
import net.stevechaloner.intellijad.config.NavigationTriggeredDecompile;
import net.stevechaloner.intellijad.decompilers.DecompilationChoiceListener;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptor;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptorFactory;
import net.stevechaloner.intellijad.util.PluginUtil;
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
            Config config = PluginUtil.getConfig();
            DecompilationDescriptor dd = DecompilationDescriptorFactory.getFactoryForFile(file).create(file);
            boolean excluded = isExcluded(config,
                                          dd);
            switch (NavigationTriggeredDecompile.getByName(config.getConfirmNavigationTriggeredDecompile()))
            {
                case ALWAYS:
                    if (!excluded)
                    {
                        decompilationListener.decompile(dd);
                    }
                    break;
                case ASK:
                    if (!excluded)
                    {
                        DecompileDialog dialog = new DecompileDialog(dd,
                                                                     project,
                                                                     decompilationListener);
                        dialog.pack();
                        SwingUtil.center(dialog);
                        dialog.setAlwaysOnTop(true);
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

    /**
     * Checks the exclusion settings to see if the class is eligable for decompilation.
     *
     * @param config                  the plugin configuration
     * @param decompilationDescriptor the descriptor of the target class
     * @return true if the class should not be decompiled
     */
    private boolean isExcluded(Config config,
                               DecompilationDescriptor decompilationDescriptor)
    {
        ExclusionTableModel exclusionModel = config.getExclusionTableModel();
        String packageName = decompilationDescriptor.getPackageName();
        boolean exclude = false;
        if (packageName != null)
        {
            switch (exclusionModel.containsPackage(packageName))
            {
                case NOT_EXCLUDED:
                    for (int i = 0; !exclude && i < exclusionModel.getRowCount(); i++)
                    {
                        String pn = (String) exclusionModel.getValueAt(i, 0);
                        if (pn != null)
                        {
                            exclude = packageName.startsWith(pn) &&
                                      (Boolean)exclusionModel.getValueAt(i, 1) &&
                                      (Boolean)exclusionModel.getValueAt(i, 2);
                        }
                    }
                    break;
                default:
                    // exclusion is either package-exact or disabled
            }
        }
        return exclude;
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
