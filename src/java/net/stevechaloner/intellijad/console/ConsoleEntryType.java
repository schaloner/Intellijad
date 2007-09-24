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
    MESSAGE(""),
    INFO(""),
    ERROR("");

    private final String messageKey;

    /**
     * Initialises a new instance of this class.
     *
     * @param messageKey the key for the standard message of this entry type
     */
    ConsoleEntryType(String messageKey)
    {
        this.messageKey = messageKey;
    }

    /**
     * Gets the message, parameterised with the given parameters.
     *
     * @param params the parameters
     * @return the parameterised message
     */
    String getMessage(Object... params)
    {
        return IntelliJadResourceBundle.message(messageKey,
                                                params);
    }
}
