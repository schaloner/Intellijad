package net.stevechaloner.intellijad.gui.tree;

import net.stevechaloner.intellijad.gui.IntelliJadIcons;

import javax.swing.Icon;
import javax.swing.JTree;

/**
 * Utils for tree node icons.
 */
class NodeIconUtil
{
    static Icon getIconFor(JTree jTree,
                           Object value,
                           boolean expanded,
                           boolean leaf)
    {
        Icon icon = IntelliJadIcons.JAVA;
        if (value.equals(jTree.getModel().getRoot()))
        {
            icon = IntelliJadIcons.INTELLIJAD_LOGO_16X16;
        }
        else
        {
            if (!leaf)
            {
                icon = expanded ? IntelliJadIcons.PACKAGE_OPEN : IntelliJadIcons.PACKAGE_CLOSED;
            }
        }
        return icon;
    }
}
