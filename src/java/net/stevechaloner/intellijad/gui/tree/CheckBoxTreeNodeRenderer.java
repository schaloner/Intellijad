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

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * Tree node renderer for checkbox trees.
 */
class CheckBoxTreeNodeRenderer extends JCheckBox implements TreeCellRenderer
{
    private static final Color SELECTION_FOREGROUND = UIManager.getColor("Tree.selectionForeground");
    private static final Color SELECTION_BACKGROUND = UIManager.getColor("Tree.selectionBackground");
    private static final Color TEXT_FOREGROUND = UIManager.getColor("Tree.textForeground");
    private static final Color TEXT_BACKGROUND = UIManager.getColor("Tree.textBackground");

    /** {@inheritDoc} */
    public Component getTreeCellRendererComponent(JTree jTree,
                                                  Object o,
                                                  boolean selected,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
        CheckBoxTreeNode cbtn = (CheckBoxTreeNode)node.getUserObject();

        this.setText(cbtn.getText());
        this.setSelected(cbtn.isSelected());

        setOpaque(selected);
        setForeground(selected ? SELECTION_FOREGROUND : TEXT_FOREGROUND);
        setBackground(selected ? SELECTION_BACKGROUND : TEXT_BACKGROUND);

        return this;
    }
}
