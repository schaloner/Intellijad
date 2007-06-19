package net.stevechaloner.intellijad.console;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import net.stevechaloner.intellijad.IntelliJad;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.util.PluginUtil;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Steve Chaloner
 */
public class IntelliJadConsole
{
    /**
     * The display logo.
     */
    private static final Icon LOGO = new ImageIcon(IntelliJad.class.getClassLoader().getResource("scn-idea-12.png"));

    /**
     * The tool window ID.
     */
    private static final String TOOL_WINDOW_ID = "IntelliJad Console";

    /**
     * The system-dependent newline character.
     */
    private static final String NEWLINE = System.getProperty("line.separator");

    /**
     * An empty implementation of {@link Runnable} for use with tool windows.
     */
    private static final Runnable EMPTY_RUNNABLE = new Runnable()
    {
        public void run()
        {
            // no-op
        }
    };

    /**
     * An empty implementation of {@link ClipboardOwner} for use with the system clipboard.
     */
    private static final ClipboardOwner EMPTY_CLIPBOARD_OWNER = new ClipboardOwner()
    {
        public void lostOwnership(Clipboard clipboard,
                                  Transferable transferable)
        {
            // no-op
        }
    };

    private JTabbedPane tabbedPane1;
    private JPanel root;
    private JTextArea consoleTextArea;
    private JToggleButton clearAndCloseOnSuccess;
    private JButton closeButton;
    private JToolBar toolbar;

    private boolean initialised = false;

    private void jitInit()
    {
        if (!initialised)
        {
            toolbar.setFloatable(false);
            clearAndCloseOnSuccess.setSelected(PluginUtil.getConfig().isClearAndCloseConsoleOnSuccess());
            clearAndCloseOnSuccess.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    PluginUtil.getConfig().setClearAndCloseConsoleOnSuccess(clearAndCloseOnSuccess.isSelected());
                }
            });
            closeButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    closeConsole();
                }
            });

            final JPopupMenu menu = new JPopupMenu();
            JMenuItem item = menu.add(new AbstractAction()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    clearConsoleContent();
                }
            });
            item.setText(IntelliJadResourceBundle.message("action.clear"));
            item = menu.add(new AbstractAction()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(consoleTextArea.getText()),
                                          EMPTY_CLIPBOARD_OWNER);
                }
            });
            item.setText(IntelliJadResourceBundle.message("action.copy-content"));
            consoleTextArea.addMouseListener(new MouseAdapter()
            {
                public void mouseReleased(MouseEvent e)
                {
                    maybeShowPopup(e);
                }

                public void mousePressed(MouseEvent e)
                {
                    maybeShowPopup(e);
                }

                private void maybeShowPopup(MouseEvent e)
                {
                    if (e.isPopupTrigger())
                    {
                        menu.show(e.getComponent(),
                                  e.getX(),
                                  e.getY());
                    }
                }
            });
            consoleTextArea.setBackground(Color.white);
        }
    }

    /**
     * Opens the console.
     */
    public void openConsole()
    {
        jitInit();
        ToolWindowManager toolWindowManager = PluginUtil.getToolWindowManager();
        ToolWindow window = toolWindowManager.getToolWindow(IntelliJadConsole.TOOL_WINDOW_ID);
        if (window == null)
        {
            window = toolWindowManager.registerToolWindow(IntelliJadConsole.TOOL_WINDOW_ID,
                                                          getRoot(),
                                                          ToolWindowAnchor.BOTTOM);
        }
        window.setIcon(LOGO);
        window.show(EMPTY_RUNNABLE);
    }

    /**
     * Closes the console.
     */
    public void closeConsole()
    {
        ToolWindowManager toolWindowManager = PluginUtil.getToolWindowManager();
        ToolWindow window = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
        if (window != null)
        {
            window.hide(EMPTY_RUNNABLE);
        }
    }

    /**
     * Sets the content of the console.
     *
     * @param content the content
     */
    public void setConsoleContent(String content)
    {
        consoleTextArea.setText(content);
    }

    /**
     * Gets the content of the console.
     *
     * @return the content
     */
    public String getConsoleContent()
    {
        return consoleTextArea.getText();
    }

    /**
     * Clears the console.
     */
    public void clearConsoleContent()
    {
        setConsoleContent("");
    }

    /**
     * Appends the given content to the console.
     *
     * @param content the content
     */
    public void appendToConsole(String content)
    {
        Document document = consoleTextArea.getDocument();
        try
        {
            document.insertString(document.getLength(),
                                  content + NEWLINE,
                                  null);
        }
        catch (BadLocationException e)
        {
            Logger.getInstance("IntelliJad").error(e);
        }
    }

    /**
     * Get the root component of the view.
     *
     * @return the root component
     */
    public JComponent getRoot()
    {
        return root;
    }

    public void disposeConsole()
    {
        ToolWindowManager toolWindowManager = PluginUtil.getToolWindowManager();
        if (toolWindowManager != null)
        {
            try
            {
                toolWindowManager.unregisterToolWindow(TOOL_WINDOW_ID);
            }
            catch (IllegalArgumentException e)
            {
                // ignore - this can occur due to lazy initialization
            }
        }
    }
}
