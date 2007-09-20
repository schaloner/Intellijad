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

import com.intellij.openapi.ui.MultiLineLabelUI;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Console tree node renderer to provide a more attractive view on what happened.
 * @author Steve Chaloner
 */
class ConsoleTreeCellRenderer extends DefaultTreeCellRenderer
{
    /**
     * Mapping for console entry types to icons.
     */
    private static final Map<ConsoleEntryType, Icon> ICONS = new HashMap<ConsoleEntryType, Icon>()

    {
        {
            put(ConsoleEntryType.ROOT,
                null);
            put(ConsoleEntryType.INTELLIJAD,
                IntelliJadConsole.LOGO);
            put(ConsoleEntryType.JAR_OPERATION,
                new ImageIcon(ConsoleTreeCellRenderer.class.getClassLoader().getResource("fileTypes/archive.png")));
            put(ConsoleEntryType.DECOMPILATION_OPERATION,
                new ImageIcon(ConsoleTreeCellRenderer.class.getClassLoader().getResource("fileTypes/java.png")));
            put(ConsoleEntryType.LIBRARY_OPERATION,
                new ImageIcon(ConsoleTreeCellRenderer.class.getClassLoader().getResource("modules/libraries.png")));
            final ImageIcon infoIcon = new ImageIcon(ConsoleTreeCellRenderer.class.getClassLoader().getResource(
                    "compiler/information.png"));
            put(ConsoleEntryType.MESSAGE,
                infoIcon);
            put(ConsoleEntryType.INFO,
                infoIcon);
            put(ConsoleEntryType.ERROR,
                new ImageIcon(ConsoleTreeCellRenderer.class.getClassLoader().getResource("compiler/error.png")));
        }
    };

    /**
     * Initialises a new instance of this class.
     */
    public ConsoleTreeCellRenderer() {
        super();
        setUI(new MultiLineLabelUI());
    }

    /** {@inheritDoc} */
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree,
                                           value,
                                           sel,
                                           expanded,
                                           leaf,
                                           row,
                                           hasFocus);
        ConsoleTreeNode node = (ConsoleTreeNode)value;
        setIcon(ICONS.get(node.getType()));
        return this;
    }
}
