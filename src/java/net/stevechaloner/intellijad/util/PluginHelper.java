package net.stevechaloner.intellijad.util;

import com.intellij.openapi.project.Project;

/**
 * @author Steve Chaloner
 */
public class PluginHelper
{
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
}
