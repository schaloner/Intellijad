package net.stevechaloner.intellijad.console;

import net.stevechaloner.intellijad.IntelliJadResourceBundle;

/**
 * @author Steve Chaloner
 */
public enum ConsoleEntryType
{
    ROOT(""),
    INTELLIJAD("message.class"),
    DECOMPILATION_OPERATION("message.decompilation"),
    JAR_OPERATION("message.jar-extraction"),
    LIBRARY_OPERATION("message.library"),
    MESSAGE("");

    private final String messageKey;

    /**
     *
     * @param messageKey
     */
    ConsoleEntryType(String messageKey)
    {
        this.messageKey = messageKey;
    }

    /**
     * 
     * @param params
     * @return
     */
    String getMessage(Object... params)
    {
        return IntelliJadResourceBundle.message(messageKey,
                                                params);
    }
}
