package net.stevechaloner.intellijad.console;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import net.stevechaloner.intellijad.util.PluginHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Steve Chaloner
 */
public class IntelliJadConsole
{
    private static final String TOOL_WINDOW_ID = "IntelliJad Console";
    private static final String NEWLINE = System.getProperty("line.separator");

    /**
     * An empty implementation of {@link Runnable} for use with tool windows.
     */
    private static final Runnable EMPTY_RUNNABLE = new Runnable()
    {
        public void run()
        {
        }
    };

    private JTabbedPane tabbedPane1;
    private JPanel root;
    private JTextArea consoleTextArea;
    private JToggleButton clearAndCloseOnSuccess;
    private JButton button1;
    private JToolBar toolbar;

    private final Project project;

    public IntelliJadConsole(@NotNull final Project project)
    {
        this.project = project;

        toolbar.setFloatable(false);
        clearAndCloseOnSuccess.setSelected(PluginHelper.getConfig(project).isClearAndCloseConsoleOnSuccess());
        clearAndCloseOnSuccess.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                PluginHelper.getConfig(project).setClearAndCloseConsoleOnSuccess(clearAndCloseOnSuccess.isSelected());
            }
        });
        button1.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                clearConsoleContent();
                closeConsole();
            }
        });
    }

    /**
     * Opens the console.
     */
    public void openConsole()
    {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow window = toolWindowManager.getToolWindow(IntelliJadConsole.TOOL_WINDOW_ID);
        if (window == null)
        {
            window = toolWindowManager.registerToolWindow(IntelliJadConsole.TOOL_WINDOW_ID,
                                                          getRoot(),
                                                          ToolWindowAnchor.BOTTOM);
        }
        window.show(EMPTY_RUNNABLE);
    }

    /**
     * Closes the console.
     */
    public void closeConsole()
    {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
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
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
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
