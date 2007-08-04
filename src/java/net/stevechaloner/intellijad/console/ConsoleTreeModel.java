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

import net.stevechaloner.intellijad.IntelliJadConstants;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

/**
 * @author Steve Chaloner
 */
class ConsoleTreeModel extends DefaultTreeModel
{
    private final NodeHandler nodeHandler;

    public ConsoleTreeModel(NodeHandler nodeHandler)
    {
        super(new ConsoleTreeNode(IntelliJadConstants.INTELLIJAD_ROOT,
                                  ConsoleEntryType.ROOT));
        this.nodeHandler = nodeHandler;
    }

    void insertNodeInto(ConsoleTreeNode child,
                               ConsoleTreeNode parent)
    {
        super.insertNodeInto(child,
                             parent,
                             parent.getChildCount());
    }

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
    ConsoleContext createConsoleContext(String name)
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

    void addSubsection(String message,
                       ConsoleContext consoleContext,
                       ConsoleEntryType type)
    {
        final ConsoleTreeNode section = consoleContext.getContextNode();
        this.insertNodeInto(new ConsoleTreeNode(message,
                                                type),
                            section);
        this.nodesWereInserted(section,
                               new int[]{section.getChildCount() - 1});
    }

    private ConsoleTreeNode getCurrentSubsection(ConsoleContext consoleContext)
    {
        ConsoleTreeNode section = consoleContext.getContextNode();
        ConsoleTreeNode subsection;
        if (section.getChildCount() > 0)
        {
            subsection = (ConsoleTreeNode)section.getLastChild();
        }
        else
        {
            throw new IllegalStateException("no subsection");
        }
        return subsection;
    }

    void addMessage(ConsoleContext consoleContext,
                    String message)
    {
        addMessage(getCurrentSubsection(consoleContext),
                   message);
    }

    void addSectionMessage(ConsoleContext consoleContext,
                           String message)
    {
        addMessage(consoleContext.getContextNode(),
                   message);
    }

    private void addMessage(ConsoleTreeNode parent,
                            String message)
    {
        this.insertNodeInto(new ConsoleTreeNode(message,
                                                ConsoleEntryType.MESSAGE),
                            parent);
        this.nodesWereInserted(parent,
                               new int[]{parent.getChildCount() - 1});
    }
}
