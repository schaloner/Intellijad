package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Steve Chaloner
 */
public class DecompilationDescriptor
{
    private String className;
    private String extension;
    private String packageName;
    private String path;
    private String pathToFile;
    private VirtualFile jarFile;

    /**
     * @param className
     * @param extension
     * @param packageName
     * @param path
     * @param pathToFile
     * @param jarFile
     */
    private DecompilationDescriptor(String className,
                                    String extension,
                                    String packageName,
                                    String path,
                                    String pathToFile,
                                    VirtualFile jarFile)
    {
        this.className = className;
        this.extension = extension;
        this.packageName = packageName;
        this.path = path;
        this.pathToFile = pathToFile;
        this.jarFile = jarFile;
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
    public VirtualFile getJarFile()
    {
        return jarFile;
    }

    // javadoc unnecessary
    public String getPathToFile()
    {
        return pathToFile;
    }

    public static DecompilationDescriptor create(VirtualFile file)
    {
        String path = file.getPath();
        return new DecompilationDescriptor(file.getNameWithoutExtension(),
                                           file.getExtension(),
                                           getPackageName(file),
                                           path,
                                           getPathToFile(file),
                                           getJarFile(file));
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
}
