package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.util.PluginUtil;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory for creating {@link DecompilationDescriptor}s based on a virtual file representing the target class.
 *
 * @author Steve Chaloner
 */
public class DecompilationDescriptorFactory
{
    private static final Pattern JARRED_CLASS_PATTERN = Pattern.compile("[.[^!]]*!(.*)");
    private static final Pattern PACKAGE_AND_CLASS_PATTERN = Pattern.compile("!(.*)");
    private static final Pattern CLASS_PATTERN = Pattern.compile("/\\w*\\.class");
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package [\\w|\\.]*;");

    /**
     * Creates a {@link DecompilationDescriptor} for the target.
     *
     * @param target the class to decompile
     * @return a decompilation descriptor for the target
     */
    public static DecompilationDescriptor create(@NotNull VirtualFile target)
    {
        String path = target.getPath();
        Matcher isJarFile = JARRED_CLASS_PATTERN.matcher(path);
        DecompilationDescriptor dd;
        if (isJarFile.matches())
        {
            dd = new JarDecompilationDescriptor(target,
                                                getFullyQualifiedName(path),
                                                getPackageName(path),
                                                getPackageNameAsPath(path),
                                                getJarFile(target));
        }
        else
        {
            dd = new FileSystemDecompilationDescriptor(target);
        }
        return dd;
    }

    /**
     * @param dd
     * @param classContent
     */
    public static void update(DecompilationDescriptor dd,
                              String classContent)
    {
        Matcher packageNameMatcher = PACKAGE_PATTERN.matcher(classContent);
        if (packageNameMatcher.find())
        {
            String packageName = classContent.substring("package ".length() + packageNameMatcher.start(),
                                                        packageNameMatcher.end() - 1);
            dd.setPackageName(packageName);
            String asPath = packageName.replaceAll("\\.", "/");
            if (!asPath.endsWith("/"))
            {
                asPath = asPath + "/";
            }
            dd.setPackageNameAsPath(asPath);

            PsiElementFactory elementFactory = PsiManager.getInstance(PluginUtil.getProject()).getElementFactory();
            PsiElement[] psiElements = elementFactory.createFileFromText("X.java", classContent).getChildren();
        }
    }

    /**
     * Gets the jar file containing the target class.
     *
     * @param file the file representing the target class
     * @return the jar file
     */
    @NotNull
    private static VirtualFile getJarFile(@NotNull VirtualFile file)
    {
        VirtualFile jarFile;
        if (file.getFileType() == StdFileTypes.ARCHIVE)
        {
            jarFile = file;
        }
        else
        {
            VirtualFile parent = file.getParent();
            if (parent != null)
            {
                jarFile = getJarFile(parent);
            }
            else
            {
                throw new IllegalArgumentException(IntelliJadResourceBundle.message("error.no-jar-in-path",
                                                                                    file.getPath()));
            }
        }
        return jarFile;
    }

    /**
     * Gets the package name of the target class.
     *
     * @param path the path to the target class
     * @return the package name
     */
    @NotNull
    private static String getPackageName(@NotNull String path)
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
        return packageName == null ? "" : packageName;
    }

    /**
     * Gets the fully qualified name of the class, e.g. net.stevechaloner.intellijad.IntelliJad .
     *
     * @param path the path to extract the FQ name from
     * @return the FQ name
     */
    @NotNull
    private static String getFullyQualifiedName(@NotNull String path)
    {
        Matcher packageAndClassMatcher = PACKAGE_AND_CLASS_PATTERN.matcher(path);
        String fqName = null;
        if (packageAndClassMatcher.find())
        {
            fqName = path.substring(packageAndClassMatcher.start() + 2);
            fqName = fqName.substring(0, fqName.length() - ".class".length());
            fqName = fqName.replaceAll("/", ".");
        }
        return fqName == null ? "" : fqName;
    }

    /**
     * Gets the package name as a path, e.g. net/stevechaloner/intellijad.  Note this always ends in /.
     *
     * @param path the path of the target class
     * @return the package name as a string
     */
    @NotNull
    private static String getPackageNameAsPath(@NotNull String path)
    {
        String packageName = getPackageName(path);
        packageName = packageName.replaceAll("\\.", "/");
        if (!packageName.endsWith("/"))
        {
            packageName = packageName + "/";
        }
        return packageName == null ? "" : packageName;
    }
}
