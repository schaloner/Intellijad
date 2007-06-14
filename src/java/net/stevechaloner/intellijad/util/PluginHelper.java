package net.stevechaloner.intellijad.util;

import com.intellij.openapi.project.Project;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.config.ConfigComponent;

/**
 * Helper class for plugin interactions.
 *
 * @author Steve Chaloner
 */
public class PluginHelper
{
    /**
     * Static access only.
     */
    private PluginHelper()
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
     * Gets the plugin configuration.
     *
     * @param project the project
     * @return the project configuration
     */
    public static Config getConfig(Project project)
    {
        return getComponent(project,
                            ConfigComponent.class).getConfig();
    }
}
