package net.stevechaloner.intellijad;

/*
 * Copyright 2007 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.Key;

import java.util.List;

import net.stevechaloner.intellijad.actions.NavigationListener;

/**
 * Constants used by IntelliJad.
 *
 * @author Steve Chaloner
 */
public class IntelliJadConstants
{
    /**
     * The name of the plugin.
     */
    public static final String INTELLIJAD = "IntelliJad";

    /**
     * The extension used by Java files.
     */
    public static final String JAVA_EXTENSION = "java";

    /**
     * The extension used by class files.
     */
    public static final String CLASS_EXTENSION = "class";

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
    public static final String INTELLIJAD_SCHEMA = INTELLIJAD_PROTOCOL + "://";

    /**
     * The name of the root.
     */
    public static final String INTELLIJAD_ROOT = "intellijad";

    /**
     * The root URI of all in-memory decompiled classes.
     */
    public static final String ROOT_URI = INTELLIJAD_SCHEMA + INTELLIJAD_ROOT;

    /**
     * The help key for configuration support.
     */
    public static final String CONFIGURATION_HELP_TOPIC = "intellijad";

    /**
     * The key for retrieving generated source libraries from the user data.
     */
    public static final Key<List<Library>> GENERATED_SOURCE_LIBRARIES = new Key<List<Library>>("generated-source-libraries");

    /**
     * The key for retrieving the decompilation listener from the user data.
     */
    public static final Key<NavigationListener> DECOMPILE_LISTENER = new Key<NavigationListener>("decompile-listener");
}
