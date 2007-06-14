package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.project.Project;
import net.stevechaloner.intellijad.console.IntelliJadConsole;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Steve Chaloner
 */
public class DecompilationContext
{
    /**
     * The console to use for reporting.
     */
    private final IntelliJadConsole console;

    /**
     * The command to execute.
     */
    private final String command;

    private final File targetDirectory;

    private final Project project;

    /**
     * Initialises a new instance of this class.
     *
     * @param project the project
     * @param console the reporting console
     * @param command the command to execute
     */
    public DecompilationContext(@NotNull Project project,
                                @NotNull IntelliJadConsole console,
                                @NotNull String command)
    {
        this.project = project;
        this.console = console;
        this.command = command;
        this.targetDirectory = new File(new File(System.getProperty("java.io.tmpdir")),
                                        "ij" + System.currentTimeMillis());
        targetDirectory.mkdir();
        targetDirectory.deleteOnExit();
    }

    // javadoc unnecessary
    public IntelliJadConsole getConsole()
    {
        return console;
    }

    // javadoc unnecessary
    public String getCommand()
    {
        return command;
    }

    // javadoc unnecessary
    public File getTargetDirectory()
    {
        return targetDirectory;
    }

    public Project getProject()
    {
        return project;
    }
}
