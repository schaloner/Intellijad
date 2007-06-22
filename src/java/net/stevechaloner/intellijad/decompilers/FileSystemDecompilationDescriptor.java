package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Steve Chaloner
 */
public class FileSystemDecompilationDescriptor extends DecompilationDescriptor
{
    @NotNull
    private String pathToFile;
    @NotNull
    private VirtualFile jarFile;

    FileSystemDecompilationDescriptor(@NotNull VirtualFile classFile)
    {
        super(classFile);
    }

    // javadoc unnecessary
    @Nullable
    public VirtualFile getJarFile()
    {
        return jarFile;
    }

    // javadoc unnecessary
    @Nullable
    public String getPathToFile()
    {
        return pathToFile;
    }

    // javadoc inherited
    @Nullable
    public ClassPathType getClassPathType()
    {
        return ClassPathType.FS;
    }

    // javadoc inherited
    @NotNull
    public File getSourceFile(@NotNull File availableDirectory)
    {
        return new File(getClassFile().getPath());
    }
}
