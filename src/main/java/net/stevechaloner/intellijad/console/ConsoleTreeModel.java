/*
 * Copyright 2007 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package net.stevechaloner.intellijad.console;

import java.util.List;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import net.stevechaloner.intellijad.IntelliJadConstants;

import org.jetbrains.annotations.NotNull;

/**
 * @author Steve Chaloner
 */
class ConsoleTreeModel extends DefaultTreeModel
{
    @NotNull
    private final NodeHandler nodeHandler;

    public ConsoleTreeModel(@NotNull NodeHandler nodeHandler)
    {
        super(new ConsoleTreeNode(IntelliJadConstants.INTELLIJAD_ROOT,
                                  ConsoleEntryType.ROOT));
        this.nodeHandler = nodeHandler;
    }

    void insertNodeInto(@NotNull ConsoleTreeNode child,
                        @NotNull  ConsoleTreeNode parent)
    {
        super.insertNodeInto(child,
                             parent,
                             parent.getChildCount());
    }

    @NotNull
    ConsoleTreeNode getRootNode()
    {
        return (ConsoleTreeNode)getRoot();
    }

    void clear()
    {
        final ConsoleTreeNode root = getRootNode();
        while (root.getChildCount() > 0)
        {
            final MutableTreeNode child = (MutableTreeNode)root.getFirstChild();
            this.removeNodeFromParent(child);
        }
        this.nodeChanged(root);
    }

    /**
     *
     * @param name
     * @return
     */
    ConsoleContext createConsoleContext(@NotNull String name)
    {
        final ConsoleTreeNode root = getRootNode();
        ConsoleTreeNode contextNode = new ConsoleTreeNode(name,
                                                          ConsoleEntryType.INTELLIJAD);
        this.insertNodeInto(contextNode,
                            root);
        this.nodesWereInserted(root,
                               new int[]{root.getChildCount() - 1});

        return new ConsoleContextImpl(this,
                                      contextNode,
                                      nodeHandler);
    }

    @NotNull
    private ConsoleTreeNode addSubsection(@NotNull String message,
                                          @NotNull ConsoleContext consoleContext,
                                          @NotNull ConsoleEntryType type)
    {
        final ConsoleTreeNode section = consoleContext.getContextNode();
        ConsoleTreeNode subsection = new ConsoleTreeNode(message,
                                                         type);
        this.insertNodeInto(subsection,
                            section);
        this.nodesWereInserted(section,
                               new int[]{section.getChildCount() - 1});
        return subsection;
    }

    @NotNull
    private ConsoleTreeNode getSubsection(@NotNull ConsoleContext consoleContext,
                                          @NotNull ConsoleEntryType entryType)
    {
        ConsoleTreeNode section = consoleContext.getContextNode();
        List<ConsoleTreeNode> children = section.getChildren();
        ConsoleTreeNode subsection = null;
        for (int i = 0; subsection == null && i < children.size(); i++)
        {
            ConsoleTreeNode child = children.get(i);
            if (entryType.equals(child.getType()))
            {
                subsection = child;
            }
        }
        if (subsection == null)
        {
            subsection = addSubsection(entryType.getMessage(),
                                       consoleContext,
                                       entryType);
        }
        return subsection;
    }

    void addMessage(@NotNull ConsoleEntryType entryType,
                    @NotNull ConsoleContext consoleContext,
                    @NotNull String message)
    {
        addMessage(getSubsection(consoleContext,
                                 entryType),
                   message);
    }

    void addSectionMessage(@NotNull ConsoleContext consoleContext,
                           @NotNull String message)
    {
        addMessage(consoleContext.getContextNode(),
                   message);
    }

    private void addMessage(@NotNull ConsoleTreeNode parent,
                            @NotNull String message)
    {
        this.insertNodeInto(new ConsoleTreeNode(message,
                                                ConsoleEntryType.MESSAGE),
                            parent);
        this.nodesWereInserted(parent,
                               new int[]{parent.getChildCount() - 1});
    }
}
