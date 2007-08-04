package net.stevechaloner.intellijad.console;

/**
 * @author Steve Chaloner
 */
public interface NodeHandler
{
    void expand(ConsoleTreeNode node);

    void collapse(ConsoleTreeNode node);

    void select(ConsoleTreeNode node);
}
