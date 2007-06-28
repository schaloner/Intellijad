package net.stevechaloner.intellijad.util;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindow;
import net.stevechaloner.intellijad.config.ApplicationConfigComponent;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.config.ProjectConfigComponent;
import net.stevechaloner.intellijad.console.IntelliJadConsole;
import org.jetbrains.annotations.NotNull;
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
     * Gets the component from the application context.
     *
     * @param clazz the class of the component
     * @return the component
     */
    public static <C> C getComponent(Class<C> clazz)
    {
        return ApplicationManager.getApplication().getComponent(clazz);
    }

    /**
     * Gets the component from the project context.
     *
     * @param clazz   the class of the component
     * @return the component
     */
    public static <C> C getProjectComponent(@NotNull Class<C> clazz)
    {
        Project project = getProject();
        return project == null ? null : project.getComponent(clazz);
    }

    /**
     * Gets the tool window manager for the current project
     *
     * @return the tool window manager
     */
    @Nullable
    public static ToolWindowManager getToolWindowManager()
    {
        Project project = getProject();
        return project == null ? null : ToolWindowManager.getInstance(project);
    }

    /**
     * Gets the current project.
     *
     * @return the current project
     */
    public static Project getProject()
    {
        DataContext dataContext = DataManager.getInstance().getDataContext();
        return (Project) dataContext.getData(DataConstants.PROJECT);
    }

    /**
     * Gets the application-level configuration.
     *
     * @return the application-level configuration
     */
    public static Config getApplicationConfig()
    {
        return getComponent(ApplicationConfigComponent.class).getConfig();
    }

    /**
     * Gets the plugin configuration.
     *
     * @return the project configuration
     */
    public static Config getConfig()
    {
        Config config = getProjectComponent(ProjectConfigComponent.class).getConfig();
        if (!config.isUseProjectSpecificSettings())
        {
            config = getApplicationConfig();
        }
        return config;
    }
}
