package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Defines the target class and supporting data required to target a file for decompilation.
 *
 * @author Steve Chaloner
 */
public class DecompilationDescriptor
{
    private final VirtualFile classFile;
    private final String fqName;
    private final String className;
    private final String extension;
    private final String packageName;
    private final String packageNameAsPath;
    private final String path;
    private final String pathToFile;
    private final VirtualFile jarFile;

    /**
     * Initialises a new instance of this class.
     *
     * @param classFile         the file pointing to the target class
     * @param fqName            the fully-qualified name of the class
     * @param packageName       the package (e.g. net.stevechaloner.intellijad)
     * @param packageNameAsPath the package as a path (e.g. net/stevechaloner/intellijad/)
     * @param path              the full path to the target class
     * @param jarFile           the jar file
     */
    DecompilationDescriptor(@NotNull VirtualFile classFile,
                            @NotNull String fqName,
                            @NotNull String packageName,
                            @NotNull String packageNameAsPath,
                            @NotNull String path,
                            @NotNull VirtualFile jarFile)
    {
        this.classFile = classFile;
        this.fqName = fqName;
        this.className = classFile.getNameWithoutExtension();
        this.extension = classFile.getExtension();
        this.packageName = packageName;
        this.packageNameAsPath = packageNameAsPath;
        this.path = path;
        this.pathToFile = jarFile.getPath();
        this.jarFile = jarFile;
    }

    // javadoc unnecessary
    @NotNull
    public VirtualFile getClassFile()
    {
        return classFile;
    }

    // javadoc unnecessary
    @NotNull
    public String getFullyQualifiedName()
    {
        return fqName;
    }

    // javadoc unnecessary
    @NotNull
    public String getClassName()
    {
        return className;
    }

    // javadoc unnecessary
    @NotNull
    public String getExtension()
    {
        return extension;
    }

    // javadoc unnecessary
    @NotNull
    public String getPath()
    {
        return path;
    }

    // javadoc unnecessary
    @NotNull
    public String getPackageName()
    {
        return packageName;
    }

    // javadoc unnecessary
    @NotNull
    public String getPackageNameAsPath()
    {
        return packageNameAsPath;
    }

    // javadoc unnecessary
    @NotNull
    public VirtualFile getJarFile()
    {
        return jarFile;
    }

    // javadoc unnecessary
    @NotNull
    public String getPathToFile()
    {
        return pathToFile;
    }

}
