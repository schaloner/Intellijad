package net.stevechaloner.intellijad;

import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.Key;

import java.util.List;

/**
 * @author Steve Chaloner
 */
public class IntelliJadConstants
{
    public static final Key<List<Library>> GENERATED_SOURCE_LIBRARIES = new Key<List<Library>>("generated-source-libraries");

    public static final Key<NavigationDecompileListener> DECOMPILE_LISTENER = new Key<NavigationDecompileListener>("decompile-listener");
}
