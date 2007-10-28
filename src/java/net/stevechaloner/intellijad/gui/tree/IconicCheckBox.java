package net.stevechaloner.intellijad.gui.tree;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A component that renders, in order, a checkbox, an icon and some text.
 */
public class IconicCheckBox {

    /**
     * The checkbox.
     */
    private JCheckBox checkBox;

    /**
     * The label.
     */
    private JLabel label;

    /**
     * The container for the checkbox and label.
     */
    private JPanel contentPane;

    /**
     * Gets the checkbox.
     *
     * @return the checkbox
     */
    public JCheckBox getCheckBox()
    {
        return checkBox;
    }

    /**
     * Gets the label.
     *
     * @return the label
     */
    public JLabel getLabel()
    {
        return label;
    }

    /**
     * Gets the content pane.
     *
     * @return the content pane
     */
    public JPanel getContentPane()
    {
        return contentPane;
    }
}
