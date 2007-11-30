package net.stevechaloner.intellijad.gui;

import org.jetbrains.annotations.NotNull;

/**
 * Visitable objects can be visited by {@link Visitor}s.
 */
public interface Visitable
{
    /**
     * Accept the visitor.
     *
     * @param visitor the visitor
     */
    void accept(@NotNull Visitor visitor);
}
