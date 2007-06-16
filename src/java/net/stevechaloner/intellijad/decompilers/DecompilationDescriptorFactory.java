package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Steve Chaloner
 */
public class DecompilationDescriptorFactory
{
    private static final Pattern JARRED_CLASS_PATTERN = Pattern.compile("[.[^!]]*!(.*)");
    private static final Pattern JAR_FILE_PATTERN = Pattern.compile("[.[^!]]*!");
    private static final Pattern PACKAGE_AND_CLASS_PATTERN = Pattern.compile("!(.*)");
    private static final Pattern CLASS_PATTERN = Pattern.compile("/\\w*\\.class");

    public static DecompilationDescriptor create(VirtualFile file)
    {
        String path = file.getPath();
        Matcher isJarFile = JARRED_CLASS_PATTERN.matcher(path);
        DecompilationDescriptor dd = null;
        if (isJarFile.matches())
        {
            dd = new DecompilationDescriptor(file,
                                             getFullyQualifiedName(path),
                                             file.getNameWithoutExtension(),
                                             file.getExtension(),
                                             getPackageName(path),
                                             getPackageNameAsPath(path),
                                             path,
                                             getPathToFile(path),
                                             getJarFile(file));
        }
        else
        {
            System.out.println("not a jar file");
        }
        return dd;
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
     * @param path
     * @return
     */
    private static String getPathToFile(String path)
    {
        Matcher jarFileMatcher = JAR_FILE_PATTERN.matcher(path);
        String pathToFile = null;
        if (jarFileMatcher.lookingAt())
        {
            pathToFile = path.substring(0, jarFileMatcher.toMatchResult().end() - 1);
        }
        return pathToFile;
    }

    /**
     * @param path
     * @return
     */
    private static String getPackageName(String path)
    {
        Matcher classMatcher = CLASS_PATTERN.matcher(path);
        String packageName = null;
        if (classMatcher.find())
        {
            Matcher packageAndClassMatcher = PACKAGE_AND_CLASS_PATTERN.matcher(path);
            if (packageAndClassMatcher.find())
            {
                int packageStart = packageAndClassMatcher.start() + 2;
                if (packageStart <= classMatcher.start())
                {
                    packageName = path.substring(packageStart,
                                                 classMatcher.start()).replaceAll("/", ".");
                }
            }
        }
        return packageName;
    }

    /**
     * Gets the fully qualified name of the class, e.g. net.stevechaloner.intellijad.IntelliJad .
     *
     * @param path the path to extract the FQ name from
     * @return the FQ name
     */
    private static String getFullyQualifiedName(String path)
    {
        Matcher packageAndClassMatcher = PACKAGE_AND_CLASS_PATTERN.matcher(path);
        String fqName = null;
        if (packageAndClassMatcher.find())
        {
            fqName = path.substring(packageAndClassMatcher.start() + 2);
            fqName = fqName.substring(0, fqName.length() - ".class".length());
            fqName = fqName.replaceAll("/", ".");
        }
        return fqName;
    }

    /**
     * @param path
     * @return
     */
    private static String getPackageNameAsPath(String path)
    {
        String packageName = getPackageName(path);
        packageName = packageName == null ? null : packageName.replaceAll("\\.", "/");
        if (!packageName.endsWith("/"))
        {
            packageName = packageName + "/";
        }
        return packageName;
    }
}
