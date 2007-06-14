package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.util.io.StreamUtil;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Steve Chaloner
 */
class ZipExtractor
{
    private static final String CLASS_PATTERN = "((\\$\\w*)?)*";

    /**
     * @param context
     * @param zipFile
     * @param packageName
     * @param className
     * @throws IOException
     */
    void extract(DecompilationContext context,
                 ZipFile zipFile,
                 String packageName,
                 String className) throws IOException
    {
        Pattern p = Pattern.compile(packageName + className + CLASS_PATTERN + ".class");

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements())
        {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            Matcher matcher = p.matcher(name);
            if (matcher.matches())
            {
                context.getConsole().appendToConsole(IntelliJadResourceBundle.message("message.extracting",
                                                                                      entry.getName()));
                InputStream inputStream = zipFile.getInputStream(entry);
                int lastIndex = name.lastIndexOf("/");
                File outputFile = new File(context.getTargetDirectory(),
                                           name.substring(lastIndex));
                outputFile.deleteOnExit();
                FileOutputStream fos = new FileOutputStream(outputFile);
                StreamUtil.copyStreamContent(inputStream,
                                             fos);
                inputStream.close();
                fos.close();
            }
        }
    }
}
