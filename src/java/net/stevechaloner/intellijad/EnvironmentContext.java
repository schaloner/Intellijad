package net.stevechaloner.intellijad;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author Steve Chaloner
 */
public class EnvironmentContext
{
    @NotNull private final Project project;

    /**
     * Initialises a new instance of this class.
     *
     * @param project the current project
     */
    public EnvironmentContext(@NotNull Project project)
    {
        this.project = project;
    }


    /**
     * Gets the environment's project.
     *
     * @return the project
     */
    @NotNull
    public Project getProject()
    {
        return project;
    }
}
