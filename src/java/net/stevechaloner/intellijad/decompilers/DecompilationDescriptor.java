package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines the target class and supporting data required to target a file for decompilation.
 *
 * @author Steve Chaloner
 */
public abstract class DecompilationDescriptor
{
    /**
     * The type of path the class exists on, jar file or file system.
     */
    public enum ClassPathType
    {
        JAR, FS
    }

    @NotNull
    private final VirtualFile classFile;
    @Nullable
    private String fqName;
    @Nullable
    private final String className;
    @Nullable
    private final String extension;
    @Nullable
    private String packageName;
    @Nullable
    private String packageNameAsPath;
    @NotNull
    private String path;

    /**
     * Initialises a new instance of this class.
     *
     * @param classFile         the file pointing to the target class
     * @param fqName            the fully-qualified name of the class
     * @param packageName       the package (e.g. net.stevechaloner.intellijad)
     * @param packageNameAsPath the package as a path (e.g. net/stevechaloner/intellijad/)
     */
    DecompilationDescriptor(@NotNull VirtualFile classFile,
                            @NotNull String fqName,
                            @NotNull String packageName,
                            @NotNull String packageNameAsPath)
    {
        this.classFile = classFile;
        this.fqName = fqName;
        this.className = classFile.getNameWithoutExtension();
        this.extension = classFile.getExtension();
        this.packageName = packageName;
        this.packageNameAsPath = packageNameAsPath;
        this.path = classFile.getPath();
    }

    /**
     * Initialises a new instance of this class.
     *
     * @param classFile the file pointing to the target class
     */
    DecompilationDescriptor(@NotNull VirtualFile classFile)
    {
        this.classFile = classFile;
        this.path = classFile.getPath();
        this.className = classFile.getNameWithoutExtension();
        this.extension = classFile.getExtension();
    }

    @Nullable
    public abstract ClassPathType getClassPathType();

    // javadoc unnecessary
    @NotNull
    public VirtualFile getClassFile()
    {
        return classFile;
    }

    // javadoc unnecessary
    @Nullable
    public String getFullyQualifiedName()
    {
        return fqName;
    }

    // javadoc unnecessary
    @Nullable
    public String getClassName()
    {
        return className;
    }

    // javadoc unnecessary
    @Nullable
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
    @Nullable
    public String getPackageName()
    {
        return packageName;
    }

    // javadoc unnecessary
    @Nullable
    public String getPackageNameAsPath()
    {
        return packageNameAsPath;
    }

    // javadoc unnecessary
    public void setFqName(String fqName)
    {
        this.fqName = fqName;
    }

    // javadoc unnecessary
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    // javadoc unnecessary
    public void setPackageNameAsPath(String packageNameAsPath)
    {
        this.packageNameAsPath = packageNameAsPath;
    }
}