/*
 * Copyright 2007 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package net.stevechaloner.intellijad.actions;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import net.stevechaloner.intellijad.EnvironmentContext;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.config.ExclusionTableModel;
import net.stevechaloner.intellijad.config.NavigationTriggeredDecompile;
import net.stevechaloner.intellijad.decompilers.DecompilationChoiceListener;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptor;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptorFactory;
import net.stevechaloner.intellijad.util.PluginUtil;
import net.stevechaloner.intellijad.util.SwingUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * @author Steve Chaloner
 */
public class NavigationDecompileListener implements FileEditorManagerListener
{
    /**
     * The decompilation listener.
     */
    @NotNull
    private final DecompilationChoiceListener decompilationListener;

    /**
     *
     */
    @NotNull
    private final Project project;

    /**
     * Initialises a new instance of this class.
     *
     * @param project the project this object is listening for
     * @param decompilationListener the decompilation listener
     */
    public NavigationDecompileListener(@NotNull Project project,
                                       @NotNull DecompilationChoiceListener decompilationListener)
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
            Config config = PluginUtil.getConfig(project);
            DecompilationDescriptor dd = DecompilationDescriptorFactory.getFactoryForFile(file).create(file);
            boolean excluded = isExcluded(config,
                                          dd);
            switch (NavigationTriggeredDecompile.getByName(config.getConfirmNavigationTriggeredDecompile()))
            {
                case ALWAYS:
                    if (!excluded)
                    {
                        decompilationListener.decompile(new EnvironmentContext(project),
                                                        dd);
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
    private boolean isExcluded(@NotNull Config config,
                               @NotNull DecompilationDescriptor decompilationDescriptor)
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
