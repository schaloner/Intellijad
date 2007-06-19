package net.stevechaloner.intellijad.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import net.stevechaloner.idea.util.properties.DOMable;
import net.stevechaloner.intellijad.IntelliJad;
import net.stevechaloner.intellijad.config.rules.RuleContext;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steve Chaloner
 */
abstract class ConfigComponent implements Configurable,
                                          JDOMExternalizable
{
    /**
     * The display logo.
     */
    private static final Icon LOGO = new ImageIcon(IntelliJad.class.getClassLoader().getResource("scn-idea-32.png"));

    /**
     * The rule execution context.
     */
    private final RuleContext ruleContext = new RuleContext();

    /**
     * The configuration.
     */
    private final Config config = new Config(ruleContext);

    /**
     * The persistable properties.
     */
    private final Map<String, DOMable> domables = new HashMap<String, DOMable>()
    {
        {
            put(config.getName(),
                config);
        }
    };

    /**
     * The configuration GUI.
     */
    protected ConfigForm form;

    // javadoc inherited
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

    // javadoc inherited
    public boolean isModified()
    {
        return form != null && form.isModified(config);
    }

    // javadoc inherited
    public void apply() throws ConfigurationException
    {
        if (form != null)
        {
            form.getData(config);
        }
    }

    // javadoc inherited
    public void reset()
    {
        if (form != null)
        {
            form.setData(config);
        }
    }

    // javadoc inherited
    public void disposeUIResources()
    {
        form = null;
    }

    // javadoc inherited
    public void readExternal(Element element) throws InvalidDataException
    {
        for (String key : domables.keySet())
        {
            DOMable domable = domables.get(key);
            Element child = element.getChild(key);
            if (child == null)
            {
                child = new Element(key);
                element.addContent(child);
            }
            domable.read(child);
        }
    }

    // javadoc inherited
    public void writeExternal(Element element) throws WriteExternalException
    {
        for (String key : domables.keySet())
        {
            DOMable domable = domables.get(key);
            element.addContent(domable.write());
        }
    }

    /**
     * Gets the configuration instance.
     *
     * @return the configuration
     */
    Config getConfig()
    {
        return config;
    }
}
