package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Steve Chaloner
 */
public class DecompilationDescriptor
{
    private VirtualFile classFile;
    private String fqName;
    private String className;
    private String extension;
    private String packageName;
    private String packageNameAsPath;
    private String path;
    private String pathToFile;
    private VirtualFile jarFile;

    /**
     * @param file
     */
    public DecompilationDescriptor(VirtualFile file)
    {
        this.classFile = file;
        this.fqName = getFullyQualifiedName(file);
        this.className = file.getNameWithoutExtension();
        this.extension = file.getExtension();
        this.packageName = getPackageName(file);
        this.packageNameAsPath = getPackageNameAsPath(file);
        this.path = file.getPath();
        this.pathToFile = getPathToFile(file);
        this.jarFile = getJarFile(file);
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

    /**
     * @param file
     * @return
     */
    private static VirtualFile getJarFile(VirtualFile file)
    {
        VirtualFile jarFile = null;
        if (file != null)
        {
            if (file.getFileType() == StdFileTypes.ARCHIVE)
            {
                jarFile = file;
            }
            else
            {
                jarFile = getJarFile(file.getParent());
            }
        }
        return jarFile;
    }

    /**
     * @param file
     * @return
     */
    private static String getPathToFile(VirtualFile file)
    {
        String path = file.getPath();
        int index = path.indexOf("!");
        String p = null;
        if (index != -1)
        {
            p = path.substring(index + 2);  // todo make this a hell of a lot safer
        }
        return p;
    }

    /**
     * @param file
     * @return
     */
    private static String getPackageName(VirtualFile file)
    {
        String path = file.getPath();
        int index = path.indexOf("!");
        String packageName = null;
        if (index != -1)
        {
            String virtualPath = path.substring(index + 1);
            if (virtualPath != null && virtualPath.length() > 0)
            {
                if (virtualPath.charAt(0) == '/')
                {
                    virtualPath = virtualPath.substring(1);
                }
                int lastIndex = virtualPath.lastIndexOf("/");
                if (lastIndex != -1)
                {
                    virtualPath = virtualPath.substring(0, lastIndex);
                }
                packageName = virtualPath.replaceAll("/", ".");
            }
        }
        return packageName;
    }

    private static String getFullyQualifiedName(VirtualFile file)
    {
        String path = file.getPath();
        int index = path.indexOf("!");
        String packageName = null;
        if (index != -1)
        {
            String virtualPath = path.substring(index + 1);
            if (virtualPath != null && virtualPath.length() > 0)
            {
                if (virtualPath.charAt(0) == '/')
                {
                    virtualPath = virtualPath.substring(1);
                }
                if (virtualPath.endsWith(".class"))
                {
                    virtualPath = virtualPath.substring(0, virtualPath.length() - ".class".length());
                }
                packageName = virtualPath.replaceAll("/", ".");
            }
        }
        return packageName;
    }

    /**
     * @param file
     * @return
     */
    private static String getPackageNameAsPath(VirtualFile file)
    {
        String path = file.getPath();
        int index = path.indexOf("!");
        String packageName = null;
        if (index != -1)
        {
            String virtualPath = path.substring(index + 1);
            if (virtualPath != null && virtualPath.length() > 0)
            {
                if (virtualPath.charAt(0) == '/')
                {
                    virtualPath = virtualPath.substring(1);
                }
                int lastIndex = virtualPath.lastIndexOf("/");
                if (lastIndex != -1)
                {
                    virtualPath = virtualPath.substring(0, lastIndex + 1);
                }
                packageName = virtualPath;
            }
        }
        return packageName;
    }
}
