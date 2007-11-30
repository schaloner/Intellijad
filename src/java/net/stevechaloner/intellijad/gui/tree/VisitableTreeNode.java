package net.stevechaloner.intellijad.gui.tree;

import net.stevechaloner.intellijad.gui.Visitable;
import net.stevechaloner.intellijad.gui.Visitor;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jetbrains.annotations.NotNull;

/**
 * Visitable tree node, used for convenient tree walking.
 */
public class VisitableTreeNode extends DefaultMutableTreeNode implements Visitable
{
    /**
     * Initialises a new instance of this class.
     *
     * @param userObject the object contained in this node
     */
    public VisitableTreeNode(Object userObject)
    {
        super(userObject);
    }

    /**
     * Initialises a new instance of this class.
     *
     * @param userObject the object contained in this node
     * @param allowsChildren true iff children are allowed
     */
    public VisitableTreeNode(Object userObject,
                             boolean allowsChildren)
    {
        super(userObject,
              allowsChildren);
    }

    /** {@inheritDoc} */
    public void accept(@NotNull Visitor visitor)
    {
        visitor.visit(this);
    }
}
