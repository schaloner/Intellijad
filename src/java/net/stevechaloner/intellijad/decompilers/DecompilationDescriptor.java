package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;

/**
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
     * @param classFile
     * @param fqName
     * @param className
     * @param extension
     * @param packageName
     * @param packageNameAsPath
     * @param path
     * @param pathToFile
     * @param jarFile
     */
    DecompilationDescriptor(VirtualFile classFile,
                            String fqName,
                            String className,
                            String extension,
                            String packageName,
                            String packageNameAsPath,
                            String path,
                            String pathToFile,
                            VirtualFile jarFile)
    {
        this.classFile = classFile;
        this.fqName = fqName;
        this.className = className;
        this.extension = extension;
        this.packageName = packageName;
        this.packageNameAsPath = packageNameAsPath;
        this.path = path;
        this.pathToFile = pathToFile;
        this.jarFile = jarFile;
    }

    // javadoc unnecessary
    public VirtualFile getClassFile()
    {
        return classFile;
    }

    // javadoc unnecessary
    public String getFullyQualifiedName()
    {
        return fqName;
    }

    // javadoc unnecessary
    public String getClassName()
    {
        return className;
    }

    // javadoc unnecessary
    public String getExtension()
    {
        return extension;
    }

    // javadoc unnecessary
    public String getPath()
    {
        return path;
    }

    // javadoc unnecessary
    public String getPackageName()
    {
        return packageName;
    }

    // javadoc unnecessary
    public String getPackageNameAsPath()
    {
        return packageNameAsPath;
    }

    // javadoc unnecessary
    public VirtualFile getJarFile()
    {
        return jarFile;
    }

    // javadoc unnecessary
    public String getPathToFile()
    {
        return pathToFile;
    }

}
