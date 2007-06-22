package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Steve Chaloner
 */
public class JarDecompilationDescriptor extends DecompilationDescriptor
{
    @NotNull
    private String pathToJarFile;
    @NotNull
    private VirtualFile jarFile;

    JarDecompilationDescriptor(@NotNull VirtualFile classFile,
                               @NotNull String fqName,
                               @NotNull String packageName,
                               @NotNull String packageNameAsPath,
                               @NotNull VirtualFile jarFile)
    {
        super(classFile,
              fqName,
              packageName,
              packageNameAsPath);
        this.jarFile = jarFile;
        this.pathToJarFile = jarFile.getPath();
    }


    // javadoc unnecessary
    @Nullable
    public VirtualFile getJarFile()
    {
        return jarFile;
    }

    // javadoc unnecessary
    @Nullable
    public String getPathToJarFile()
    {
        return pathToJarFile;
    }

    @Nullable
    public ClassPathType getClassPathType()
    {
        return ClassPathType.JAR;
    }

    // javadoc inherited
    @NotNull
    public File getSourceFile(@NotNull File availableDirectory)
    {
        return new File(availableDirectory,
                        getClassName() + '.' + getExtension());
    }
}
