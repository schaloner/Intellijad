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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Steve Chaloner
 */
public class NavigationDecompileListener implements FileEditorManagerListener
{
    /**
     * Handler classes for the result of navigation actions.
     */
    private final Map<NavigationTriggeredDecompile, NavigationOption> navigationOptions = new HashMap<NavigationTriggeredDecompile,NavigationOption>()
    {
        {
            put(NavigationTriggeredDecompile.ALWAYS,
                new NavigationOption()
                {
                    public void execute(@NotNull Config config,
                                        @NotNull DecompilationDescriptor descriptor)
                    {
                        boolean excluded = isExcluded(config,
                                                      descriptor);
                        if (!excluded)
                        {
                            decompilationListener.decompile(new EnvironmentContext(project),
                                                            descriptor);
                        }
                    }
                });
            put(NavigationTriggeredDecompile.ASK,
                new NavigationOption()
                {
                    public void execute(@NotNull Config config,
                                        @NotNull DecompilationDescriptor descriptor)
                    {
                        boolean excluded = isExcluded(config,
                                                      descriptor);
                        if (!excluded)
                        {
                            DecompileDialog dialog = new DecompileDialog(descriptor,
                                                                         project,
                                                                         decompilationListener);
                            dialog.pack();
                            SwingUtil.center(dialog);

                            dialog.setVisible(true);
                        }
                    }
                });
            put(NavigationTriggeredDecompile.NEVER,
                new NavigationOption()
                {
                    public void execute(@NotNull Config config,
                                        @NotNull DecompilationDescriptor descriptor)
                    {
                        // no-op
                    }
                });
        }
    };

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
            NavigationOption navigationOption = navigationOptions.get(NavigationTriggeredDecompile.getByName(config.getConfirmNavigationTriggeredDecompile()));
            navigationOption.execute(config,
                                     dd);
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
            if (ExclusionTableModel.ExclusionType.NOT_EXCLUDED == exclusionModel.containsPackage(packageName))
            {
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
            }
        }
        return exclude;
    }

    // javadoc inherited
    public void fileClosed(FileEditorManager fileEditorManager,
                           VirtualFile virtualFile)
    {
        // no-op
    }

    // javadoc inherited
    public void selectionChanged(FileEditorManagerEvent fileEditorManagerEvent)
    {
        // no-op
    }

    /**
     * Handles the result of a navigation-based action decision.
     */
    private interface NavigationOption
    {
        /**
         * Handle the choice.
         *
         * @param config the configutation
         * @param descriptor the descriptor of the class to decompile
         */
        void execute(@NotNull Config config,
                     @NotNull DecompilationDescriptor descriptor);
    }
}
