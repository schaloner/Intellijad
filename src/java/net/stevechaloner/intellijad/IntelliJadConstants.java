package net.stevechaloner.intellijad;

import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.Key;
import net.stevechaloner.intellijad.actions.NavigationDecompileListener;

import java.util.List;

/**
 * Constants used by IntelliJad.
 *
 * @author Steve Chaloner
 */
public class IntelliJadConstants
{
    /**
     * The extension used by Java files.
     */
    public static final String JAVA_EXTENSION = "java";

    /**
     * The extension used by Java files, with a handy dot to save future appends.
     */
    public static final String DOT_JAVA_EXTENSION = ".java";

    /**
     * The protocol name used by in-memory decompiled classes.
     */
    public static final String INTELLIJAD_PROTOCOL = "intellijad";

    /**
     * The schema used by in-memory decompiled classes.
     */
    public static final String INTELLIJAD_SCHEMA = "intellijad://";

    /**
     * The root URL of all in-memory decompiled classes.
     */
    public static final String INTELLIJAD_ROOT = "intellijad://root";

    /**
     *
     */
    public static final Key<List<Library>> GENERATED_SOURCE_LIBRARIES = new Key<List<Library>>("generated-source-libraries");

    /**
     *
     */
    public static final Key<NavigationDecompileListener> DECOMPILE_LISTENER = new Key<NavigationDecompileListener>("decompile-listener");
}
