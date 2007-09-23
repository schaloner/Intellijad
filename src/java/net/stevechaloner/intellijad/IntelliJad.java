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

package net.stevechaloner.intellijad;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import net.stevechaloner.intellijad.actions.NavigationDecompileListener;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.console.ConsoleContext;
import net.stevechaloner.intellijad.console.ConsoleEntryType;
import net.stevechaloner.intellijad.console.ConsoleManager;
import net.stevechaloner.intellijad.console.IntelliJadConsole;
import net.stevechaloner.intellijad.decompilers.DecompilationChoiceListener;
import net.stevechaloner.intellijad.decompilers.DecompilationContext;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptor;
import net.stevechaloner.intellijad.decompilers.DecompilationException;
import net.stevechaloner.intellijad.decompilers.Decompiler;
import net.stevechaloner.intellijad.decompilers.fs.FileSystemDecompiler;
import net.stevechaloner.intellijad.decompilers.memory.MemoryDecompiler;
import net.stevechaloner.intellijad.util.PluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JOptionPane;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The central component of the plugin.
 */
public class IntelliJad implements ApplicationComponent,
                                   DecompilationChoiceListener,
                                   ProjectManagerListener
{
    /**
     * The name of the component.
     */
    public static final String COMPONENT_NAME = "net.stevechaloner.intellijad.IntelliJad";

    /**
     * The name of the plugin.
     */
    public static final String INTELLIJAD = "IntelliJad";

    /**
     * The manager for projects' consoles.
     */
    private final ConsoleManager consoleManager = new ConsoleManager();

    /** {@inheritDoc} */
    @NotNull
    public String getComponentName()
    {
        return COMPONENT_NAME;
    }

    /** {@inheritDoc} */
    public void projectOpened(Project project)
    {
        project.putUserData(IntelliJadConstants.GENERATED_SOURCE_LIBRARIES,
                            new ArrayList<Library>());

        NavigationDecompileListener navigationListener = new NavigationDecompileListener(project,
                                                                                         this);
        FileEditorManager.getInstance(project).addFileEditorManagerListener(navigationListener);
        project.putUserData(IntelliJadConstants.DECOMPILE_LISTENER,
                            navigationListener);
    }

    /** {@inheritDoc} */
    public boolean canCloseProject(Project project)
    {
        // no-op
        return true;
    }

    /** {@inheritDoc} */
    public void projectClosed(Project project)
    {
        NavigationDecompileListener listener = project.getUserData(IntelliJadConstants.DECOMPILE_LISTENER);
        FileEditorManager.getInstance(project).removeFileEditorManagerListener(listener);
        consoleManager.disposeConsole(project);
    }

    /** {@inheritDoc} */
    public void projectClosing(final Project project)
    {
        ApplicationManager.getApplication().runWriteAction(new Runnable()
        {
            public void run()
            {
                List<Library> list = project.getUserData(IntelliJadConstants.GENERATED_SOURCE_LIBRARIES);
                for (Library library : list)
                {
                    Library.ModifiableModel model = library.getModifiableModel();
                    VirtualFile[] files = model.getFiles(OrderRootType.SOURCES);
                    for (VirtualFile file : files)
                    {
                        if (file.getParent() == null && IntelliJadConstants.ROOT_URI.equals(file.getUrl()))
                        {
                            model.removeRoot(file.getUrl(),
                                             OrderRootType.SOURCES);
                        }
                    }
                    model.commit();
                }
            }
        });
    }

    /** {@inheritDoc} */
    public void initComponent()
    {
        ProjectManager.getInstance().addProjectManagerListener(this);
    }

    /** {@inheritDoc} */
    public void disposeComponent()
    {
        ProjectManager.getInstance().removeProjectManagerListener(this);
    }

    /** {@inheritDoc} */
    public void decompile(EnvironmentContext envContext,
                          DecompilationDescriptor descriptor)
    {
        long startTime = System.currentTimeMillis();
        Project project = envContext.getProject();
        IntelliJadConsole console = consoleManager.getConsole(project);
        console.openConsole();
        final ConsoleContext consoleContext = console.createConsoleContext("message.class",
                                                                           descriptor.getClassName());
        Config config = PluginUtil.getConfig(project);
        String validationMessage = validateOptions(config,
                                                   consoleContext);
        if (validationMessage == null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(config.getJadPath()).append(' ');
            sb.append(config.renderCommandLinePropertyDescriptors());
            DecompilationContext context = new DecompilationContext(project,
                                                                    consoleContext,
                                                                    sb.toString());
            Decompiler decompiler = (config.isDecompileToMemory()) ? new MemoryDecompiler() : new FileSystemDecompiler();
            try
            {
                VirtualFile file = decompiler.getVirtualFile(descriptor,
                                                             context);
                FileEditorManager editorManager = FileEditorManager.getInstance(project);
                if (file != null && editorManager.isFileOpen(file))
                {
                    console.closeConsole();
                    FileEditorManager.getInstance(project).closeFile(descriptor.getClassFile());
                    editorManager.openFile(file,
                                           true);
                }
                else
                {
                    file = decompiler.decompile(descriptor,
                                                context);
                    // todo check if file is already open in case of FS decomp
                    if (file != null)
                    {
                        editorManager.closeFile(descriptor.getClassFile());
                        editorManager.openFile(file,
                                               true);
                    }
                    consoleContext.addSectionMessage(ConsoleEntryType.INFO,
                                                     "message.operation-time",
                                                     System.currentTimeMillis() - startTime);
                }
            }
            catch (DecompilationException e)
            {
                consoleContext.addSectionMessage(ConsoleEntryType.ERROR,
                                                 "error",
                                                 e.getMessage());
            }
            consoleContext.close();
            checkConsole(config,
                         console,
                         consoleContext);
        }
        else
        {
            JOptionPane.showMessageDialog(WindowManager.getInstance().suggestParentWindow(project),
                                          validationMessage);
        }
    }

    /**
     * Check if the console can be closed.
     *
     * @param config the plugin configuration
     * @param console the console
     * @param consoleContext the console context
     */
    private void checkConsole(Config config,
                              IntelliJadConsole console,
                              ConsoleContext consoleContext)
    {
        if (config.isClearAndCloseConsoleOnSuccess() &&
            !consoleContext.isWorthDisplaying())
        {
            console.clearConsoleContent();
            console.closeConsole();
        }
    }

    /**
     * Validate the path to Jad as valid.
     *
     * @param config the config to check
     * @param consoleContext the console context to report issues to
     * @return the error message if necessary, or null if no error
     */
    @Nullable
    private String validateOptions(@NotNull Config config,
                                   @NotNull ConsoleContext consoleContext)
    {
        String message = null;
        Object[] params = {};
        String jadPath = config.getJadPath();
        if (StringUtil.isEmptyOrSpaces(jadPath))
        {
            message = "error.unspecified-jad-path";
        }
        else
        {
            File f = new File(jadPath);
            if (!f.exists())
            {
                message = "error.non-existant-jad-path";
                params = new String[]{jadPath};
            }
            else if (!f.isFile())
            {
                message = "error.invalid-jad-path";
                params = new String[]{jadPath};
            }
        }

        if (message == null &&
            !config.isDecompileToMemory() &&
            StringUtil.isEmptyOrSpaces(config.getOutputDirectory()))
        {
            message = IntelliJadResourceBundle.message("error.unspecified-output-directory");
        }

        String rtnVal = null;
        if (message != null)
        {
            rtnVal = IntelliJadResourceBundle.message(message,
                                                      params);
            consoleContext.addSectionMessage(ConsoleEntryType.ERROR,
                                             message,
                                             params);
        }
        return rtnVal;
    }

    /**
     * Get the logger for this plugin.
     *
     * @return the logger
     */
    public static Logger getLogger()
    {
        return Logger.getInstance(INTELLIJAD);
    }
}