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

package net.stevechaloner.intellijad.gui.tree;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventObject;

/**
 * Editor for checkbox-enabled trees.
 */
class CheckBoxTreeNodeEditor extends AbstractCellEditor implements TreeCellEditor
{
    /**
     * The tree the editor is used in.
     */
    private final JTree tree;

    /**
     * Initialises a new instance of this class.
     * @param tree
     */
    public CheckBoxTreeNodeEditor(JTree tree)
    {
        this.tree = tree;
    }

    /** {@inheritDoc} */
    public boolean isCellEditable(EventObject eventObject)
    {
        return true;
    }

    /** {@inheritDoc} */
    public Object getCellEditorValue()
    {
        TreePath path = tree.getEditingPath();
        return ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
    }

    /** {@inheritDoc} */
    public Component getTreeCellEditorComponent(JTree jTree,
                                                Object o,
                                                boolean b,
                                                boolean b1,
                                                boolean b2,
                                                int i)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
        final CheckBoxTreeNode cbtn = (CheckBoxTreeNode)node.getUserObject();
        final JCheckBox checkBox = new JCheckBox(cbtn.getText(),
                                           cbtn.isSelected());
        checkBox.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent event)
            {
                cbtn.setSelected(checkBox.isSelected());
                fireEditingStopped();
            }
        });
        return checkBox;
    }
}
