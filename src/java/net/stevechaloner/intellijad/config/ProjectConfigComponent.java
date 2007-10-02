/*
 * Copyright 2007 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package net.stevechaloner.intellijad.config;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;

import net.stevechaloner.intellijad.util.PluginUtil;

import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Component for the project-level configuation.
 *
 * @author Steve Chaloner
 */
public class ProjectConfigComponent implements ProjectComponent,
                                               ConfigAccessor,
                                               JDOMExternalizable
{
    /**
     * The name of the component.
     */
    @NonNls
    private static final String COMPONENT_NAME = "IntelliJadProjectConfigComponent";

    /**
     * The generic configuration component.
     */
    private final ConfigComponent configComponent = new ConfigComponent()
    {
        /** {@inheritDoc} */
        @Nls
        public String getDisplayName()
        {
            return "IntelliJad Project";
        }

        /** {@inheritDoc} */
        @NotNull
        public JComponent createComponent()
        {
            ConfigForm form = getForm();
            return form == null ? createForm(project).getRoot() : form.getRoot();
        }
    };

    /**
     * The project this configuration is associated with.
     */
    private final Project project;

    /**
     * Initialises a new instance of this class.
     *
     * @param project the project this configuration is associated with.
     */
    public ProjectConfigComponent(Project project)
    {
        this.project = project;
    }

    /** {@inheritDoc} */
    public void initComponent()
    {
    }

    /** {@inheritDoc} */
    public void disposeComponent()
    {
    }

    @NotNull
    public String getComponentName()
    {
        return COMPONENT_NAME;
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
        return configComponent.getDisplayName();
    }

    public Icon getIcon()
    {
        return configComponent.getIcon();
    }

    @Nullable
    @NonNls
    public String getHelpTopic()
    {
        return configComponent.getHelpTopic();
    }

    public JComponent createComponent()
    {
        return configComponent.createComponent();
    }

    public boolean isModified()
    {
        return configComponent.isModified();
    }

    public void apply() throws ConfigurationException
    {
        configComponent.apply();
    }

    public void reset()
    {
        configComponent.reset();
    }

    public void disposeUIResources()
    {
        configComponent.disposeUIResources();
    }

    public void readExternal(Element element) throws InvalidDataException
    {
        configComponent.readExternal(element);
    }

    public void writeExternal(Element element) throws WriteExternalException
    {
        configComponent.writeExternal(element);
    }

    /**
     * Get the configuration instance.  If the project-level config specifies the global
     * settings should be used, the global-level instance is returned.
     *
     * @return the configuration
     */
    public Config getConfig()
    {
        Config config = configComponent.getConfig();
        if (!config.isUseProjectSpecificSettings())
        {
            config = PluginUtil.getApplicationConfig();
        }
        return config;
    }
}
