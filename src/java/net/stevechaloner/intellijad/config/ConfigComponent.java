package net.stevechaloner.intellijad.config;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import net.stevechaloner.idea.util.properties.DOMable;
import net.stevechaloner.intellijad.config.rules.RuleContext;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steve Chaloner
 */
public class ConfigComponent implements ProjectComponent,
        Configurable,
        JDOMExternalizable {
    /**
     * The name of the component
     */
    @NonNls
    private static final String COMPONENT_NAME = "IntelliJadConfigComponent";

    /**
     * The display name of the component
     */
    @NonNls
    private static final String DISPLAY_NAME = "IntelliJad";

    /**
     * The project.
     */
    private final Project project;

    private final RuleContext ruleContext = new RuleContext();

    /**
     * The configuration.
     */
    private final Config config = new Config(ruleContext);

    /**
     * The configuration GUI.
     */
    private ConfigForm form;

    /**
     * The persistable properties.
     */
    private final Map<String, DOMable> domables = new HashMap<String, DOMable>() {
        {
            put(config.getName(),
                    config);
        }
    };

    /**
     * Initialises a new instance of this class.
     *
     * @param project the project
     */
    public ConfigComponent(Project project) {
        this.project = project;
    }

    // javadoc inherited
    public void projectOpened() {
    }

    // javadoc inherited
    public void projectClosed() {
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    public Icon getIcon() {
        return null;
    }

    @Nls
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Nullable
    @NonNls
    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (form == null) {
            form = new ConfigForm(project);
        }
        return form.getRoot();
    }

    public boolean isModified() {
        return form != null && form.isModified(config);
    }

    public void apply() throws ConfigurationException {
        if (form != null) {
            form.getData(config);
        }
    }

    public void reset() {
        if (form != null) {
            form.setData(config);
        }
    }

    public void disposeUIResources() {
        form = null;
    }

    public void readExternal(Element element) throws InvalidDataException {
        for (String key : domables.keySet()) {
            DOMable domable = domables.get(key);
            Element child = element.getChild(key);
            if (child == null) {
                child = new Element(key);
                element.addContent(child);
            }
            domable.read(child);
        }
    }

    public void writeExternal(Element element) throws WriteExternalException {
        for (String key : domables.keySet()) {
            DOMable domable = domables.get(key);
            element.addContent(domable.write());
        }
    }

    // javadoc unnecessary
    public Config getConfig() {
        return config;
    }
}
