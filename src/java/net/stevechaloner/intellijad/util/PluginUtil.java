package net.stevechaloner.intellijad.util;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import net.stevechaloner.intellijad.config.ApplicationConfigComponent;
import net.stevechaloner.intellijad.config.Config;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class for plugin interactions.
 *
 * @author Steve Chaloner
 */
public class PluginUtil
{
    /**
     * Static access only.
     */
    private PluginUtil()
    {
    }

    /**
     * Gets the component from the project context.
     *
     * @param project the project
     * @param clazz   the class of the component
     * @return the component
     */
    public static <C> C getComponent(Project project,
                                     Class<C> clazz)
    {
        return project.getComponent(clazz);
    }

    /**
     * @return
     */
    @Nullable
    public static ToolWindowManager getToolWindowManager()
    {
        Project project = getProject();
        return project == null ? null : ToolWindowManager.getInstance(project);
    }

    /**
     * @return
     */
    public static Project getProject()
    {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        return (Project) dataContext.getData(DataConstants.PROJECT);
    }

    /**
     * Gets the plugin configuration.
     *
     * @return the project configuration
     */
    public static Config getConfig()
    {
        return getComponent(getProject(),
                            ApplicationConfigComponent.class).getConfig();
    }
}
