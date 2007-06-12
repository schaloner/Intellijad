package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Steve Chaloner
 */
public class DecompilationDescriptor
{
    private String className;
    private String extension;
    private String path;
    private String packageName;
    private boolean inJar;

    /**
     * @param className
     * @param extension
     * @param packageName
     * @param path
     * @param inJar
     */
    private DecompilationDescriptor(String className,
                                    String extension,
                                    String packageName,
                                    String path,
                                    boolean inJar)
    {
        this.className = className;
        this.extension = extension;
        this.packageName = packageName;
        this.path = path;
        this.inJar = inJar;
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
    public boolean isInJar()
    {
        return inJar;
    }

    // javadoc unnecessary
    public String getPackageName()
    {
        return packageName;
    }

    public static DecompilationDescriptor create(VirtualFile file)
    {
        return null;
    }
}
