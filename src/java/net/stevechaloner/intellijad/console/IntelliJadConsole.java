package net.stevechaloner.intellijad.console;

import com.intellij.openapi.diagnostic.Logger;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * @author Steve Chaloner
 */
public class IntelliJadConsole
{
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTextArea consoleTextArea;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;

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
                                  content,
                                  null);
        }
        catch (BadLocationException e)
        {
            Logger.getInstance("IntelliJad").error(e);
        }
    }

    /**
     * Sets the content of the output view.
     *
     * @param content the content
     */
    public void setOutputContent(String content)
    {
        outputTextArea.setText(content);
    }

    /**
     * Gets the content of the output view.
     *
     * @return the content
     */
    public String getOutputContent()
    {
        return outputTextArea.getText();
    }

    /**
     * Clears the output view.
     */
    public void clearOutputContent()
    {
        setOutputContent("");
    }

    /**
     * Sets the content of the input view.
     *
     * @param content the content
     */
    public void setInputContent(String content)
    {
        inputTextArea.setText(content);
    }

    /**
     * Gets the content of the input view.
     *
     * @return the content
     */
    public String getInputContent()
    {
        return inputTextArea.getText();
    }

    /**
     * Clears the input view.
     */
    public void clearInputContent()
    {
        setInputContent("");
    }
}
