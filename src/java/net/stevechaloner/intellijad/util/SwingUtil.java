package net.stevechaloner.intellijad.util;

import javax.swing.JDialog;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * @author Steve Chaloner
 */
public class SwingUtil
{
    private SwingUtil()
    {
    }

    public static void center(JDialog dialog)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation((int) (screenSize.getWidth() - dialog.getWidth()) / 2,
                           (int) (screenSize.getHeight() - dialog.getHeight()) / 2);
    }
}
