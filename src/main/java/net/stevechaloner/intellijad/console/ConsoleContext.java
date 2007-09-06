package net.stevechaloner.intellijad.console;

import org.jetbrains.annotations.NotNull;

/**
 * @author Steve Chaloner
 */
public interface ConsoleContext
{
    /**
     * Checks if the console contains any information that makes it interesting
     * to the user, e.g. errors.
     *
     * @return true iff worth displaying
     */
    boolean isWorthDisplaying();

    /**
     * Sets the worthDisplaying flag.
     *
     * @param worthDisplaying true iff worth displaying
     */
    void setWorthDisplaying(boolean worthDisplaying);

    /**
     * Adds a message to a subsection  within this operation's log.
     *
     * @param entryType the entry type
     * @param message resource bundle key key
     * @param parameters resource bundle parameters
     */
    void addMessage(ConsoleEntryType entryType,
                    String message,
                    Object... parameters);

    /**
     * Adds a context-level message to this operation's log.
     *
     * @param entryType the entry type
     * @param message resource bundle key key
     * @param parameters resource bundle parameters
     */
    void addSectionMessage(ConsoleEntryType entryType,
                           String message,
                           Object... parameters);

    /**
     * Close the console view.
     */
    void close();

    @NotNull
    ConsoleTreeNode getContextNode();
}
