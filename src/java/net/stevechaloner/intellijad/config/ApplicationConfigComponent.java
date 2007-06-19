package net.stevechaloner.intellijad.config;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * @author Steve Chaloner
 */
public class ApplicationConfigComponent implements ApplicationComponent,
                                                   Configurable,
                                                   JDOMExternalizable
{
    /**
     * The name of the component
     */
    @NonNls
    private static final String COMPONENT_NAME = "IntelliJadConfigComponent";

    /**
     * The generic configuration component.
     */
    private final ConfigComponent configComponent = new ConfigComponent()
    {
        @Nls
        public String getDisplayName()
        {
            return "IntelliJad";
        }

        public JComponent createComponent()
        {
            if (form == null)
            {
                form = new ConfigForm();
            }
            return form.getRoot();
        }
    };

    @NonNls
    @NotNull
    public String getComponentName()
    {
        return COMPONENT_NAME;
    }

    // javadoc inherited
    public void initComponent()
    {
    }

    // javadoc inherited
    public void disposeComponent()
    {
    }

    // javadoc inherited
    public Icon getIcon()
    {
        return configComponent.getIcon();
    }

    @Nls
    public String getDisplayName()
    {
        return configComponent.getDisplayName();
    }

    // javadoc inherited
    @Nullable
    @NonNls
    public String getHelpTopic()
    {
        return configComponent.getHelpTopic();
    }

    // javadoc inherited
    public JComponent createComponent()
    {
        return configComponent.createComponent();
    }

    // javadoc inherited
    public boolean isModified()
    {
        return configComponent.isModified();
    }

    // javadoc inherited
    public void apply() throws ConfigurationException
    {
        configComponent.apply();
    }

    // javadoc inherited
    public void reset()
    {
        configComponent.reset();
    }

    // javadoc inherited
    public void disposeUIResources()
    {
        configComponent.disposeUIResources();
    }

    // javadoc inherited
    public void readExternal(Element element) throws InvalidDataException
    {
        configComponent.readExternal(element);
    }

    // javadoc inherited
    public void writeExternal(Element element) throws WriteExternalException
    {
        configComponent.writeExternal(element);
    }

    // javadoc unnecessary
    public Config getConfig()
    {
        return configComponent.getConfig();
    }
}
