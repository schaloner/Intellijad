package net.stevechaloner.intellijad.console;

/**
 * Console tree controller interface.
 *
 * @author Steve Chaloner
 */
public interface NodeHandler
{
    /**
     * Expand the given node.
     *
     * @param node the node to expand
     */
    void expand(ConsoleTreeNode node);

    /**
     * Collapse the given node.
     *
     * @param node the node to collape
     */
    void collapse(ConsoleTreeNode node);

    /**
     * Select the given node.
     *
     * @param node the node to select
     */
    void select(ConsoleTreeNode node);
}
