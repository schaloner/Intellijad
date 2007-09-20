package net.stevechaloner.intellijad.console;

/**
 *
 * @author Steve Chaloner
 */
public interface NodeHandler
{
    /**
     * Expands the given node.
     *
     * @param node the node to expand
     */
    void expand(ConsoleTreeNode node);

    /**
     * Collapses the given node.
     *
     * @param node the node to collapse
     */
    void collapse(ConsoleTreeNode node);

    /**
     *
     * @param node
     */
    void select(ConsoleTreeNode node);
}
