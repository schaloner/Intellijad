package net.stevechaloner.intellijad.config;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import net.stevechaloner.intellijad.IntelliJad;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * @author Steve Chaloner
 */
public class ProjectConfigComponent implements ProjectComponent,
                                               Configurable,
                                               JDOMExternalizable
{
    /**
     * The display name of the component
     */
    @NonNls
    private static final String DISPLAY_NAME = "IntelliJad Project";

    /**
     * The display logo.
     */
    private static final Icon LOGO = new ImageIcon(IntelliJad.class.getClassLoader().getResource("scn-idea-32.png"));

    /**
     * The configuration GUI.
     */
    private ConfigForm form;

    /**
     *
     */
    private final Project project;

    /**
     * @param project
     */
    public ProjectConfigComponent(Project project)
    {
        this.project = project;
    }

    public void initComponent()
    {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent()
    {
        // TODO: insert component disposal logic here
    }

    public String getComponentName()
    {
        return "ProjectConfigComponent";
    }

    public void projectOpened()
    {
        // called when project is opened
    }

    public void projectClosed()
    {
        // called when project is being closed
    }

    @Nls
    public String getDisplayName()
    {
        return DISPLAY_NAME;
    }

    public Icon getIcon()
    {
        return LOGO;
    }

    @Nullable
    @NonNls
    public String getHelpTopic()
    {
        return null;
    }

    public JComponent createComponent()
    {
        if (form == null)
        {
            form = new ConfigForm(project);
        }
        return form.getRoot();
    }

    public boolean isModified()
    {
        return false;
    }

    public void apply() throws ConfigurationException
    {
    }

    public void reset()
    {
    }

    public void disposeUIResources()
    {
    }

    public void readExternal(Element element) throws InvalidDataException
    {
    }

    public void writeExternal(Element element) throws WriteExternalException
    {
    }
}
