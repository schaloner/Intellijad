package net.stevechaloner.intellijad.console;

import org.jetbrains.annotations.NotNull;

/**
 * @author Steve Chaloner
 */
public interface ConsoleContext
{
    boolean isWorthDisplaying();

    void setWorthDisplaying(boolean worthDisplaying);

    void addSubsection(ConsoleEntryType type,
                       Object... parameters);

    void addMessage(String message,
                    Object... parameters);

    void addSectionMessage(String message,
                           Object... parameters);

    void close();

    @NotNull
    ConsoleTreeNode getContextNode();
}
