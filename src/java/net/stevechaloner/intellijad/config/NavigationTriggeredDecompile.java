package net.stevechaloner.intellijad.config;

import net.stevechaloner.intellijad.IntelliJadResourceBundle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Steve Chaloner
 */
public enum NavigationTriggeredDecompile {
    ALWAYS("Always",
            IntelliJadResourceBundle.message("option.always")),
    NEVER("Never",
            IntelliJadResourceBundle.message("option.never")),
    ASK("Ask",
            IntelliJadResourceBundle.message("option.ask"));

    private final static Map<String, NavigationTriggeredDecompile> MAP = new HashMap<String, NavigationTriggeredDecompile>() {
        {
            put(ALWAYS.getName(),
                    ALWAYS);
            put(NEVER.getName(),
                    NEVER);
            put(ASK.getName(),
                    ASK);
        }


        public NavigationTriggeredDecompile get(Object o) {
            NavigationTriggeredDecompile option = super.get(o);
            return option == null ? ASK : option;
        }
    };

    private final String name;

    private final String displayName;

    NavigationTriggeredDecompile(String name,
                                 String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    // javadoc inherited
    public String getName() {
        return name;
    }

    // javadoc inherited
    public String getDisplayName() {
        return displayName;
    }


    /**
     * Look up the enum by name.
     *
     * @param name the name of the type
     * @return the enum entry
     */
    public static NavigationTriggeredDecompile getByName(String name) {
        return MAP.get(name);
    }

    // javadoc inherited
    public String toString() {
        return displayName;
    }
}
