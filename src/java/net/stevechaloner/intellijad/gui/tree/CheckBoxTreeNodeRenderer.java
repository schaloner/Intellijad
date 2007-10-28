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

import net.stevechaloner.intellijad.gui.IntelliJadIcons;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * Tree node renderer for checkbox trees.
 */
class CheckBoxTreeNodeRenderer implements TreeCellRenderer
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
        IconicCheckBox cb = new IconicCheckBox();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)o;
        CheckBoxTreeNode cbtn = (CheckBoxTreeNode)node.getUserObject();

        JLabel label = cb.getLabel();
        label.setText(cbtn.getText());
        JCheckBox checkBox = cb.getCheckBox();
        checkBox.setSelected(cbtn.isSelected());

        if (leaf)
        {
            label.setIcon(IntelliJadIcons.JAVA);
        }
        else
        {
            if (o.equals(jTree.getModel().getRoot()))
            {
                label.setIcon(IntelliJadIcons.INTELLIJAD_LOGO_12X12);
            }
            else
            {
                label.setIcon(expanded ? IntelliJadIcons.PACKAGE_OPEN : IntelliJadIcons.PACKAGE_CLOSED);
            }
        }

        prepare(label,
                selected);
        prepare(checkBox,
                selected);

        return cb.getContentPane();
    }

    private void prepare(JComponent component,
                         boolean selected)
    {
        component.setOpaque(selected);
        component.setForeground(selected ? SELECTION_FOREGROUND : TEXT_FOREGROUND);
        component.setBackground(selected ? SELECTION_BACKGROUND : TEXT_BACKGROUND);
    }
}
