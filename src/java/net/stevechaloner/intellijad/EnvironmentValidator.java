package net.stevechaloner.intellijad;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MultiLineLabelUI;
import com.intellij.openapi.util.text.StringUtil;

import net.stevechaloner.intellijad.config.ApplicationConfigComponent;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.config.ConfigAccessor;
import net.stevechaloner.intellijad.config.ProjectConfigComponent;
import net.stevechaloner.intellijad.console.ConsoleContext;
import net.stevechaloner.intellijad.console.ConsoleEntryType;

import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;
import java.io.File;

/**
 * Validates the environment and configuration as suitable for decompilation.  If it's not,
 * the user is given the opportunity to rectify the issues.
 *
 * @author Steve Chaloner
 */
public class EnvironmentValidator
{
    /**
     * Validates the environment prior to decompilation.
     *
     * @param config the configuration
     * @param envContext the environment configuration
     * @param consoleContext the console's logging context for this operation
     * @return true if the decompilation should continue
     */
    public static boolean validateEnvironment(@NotNull Config config,
                                              @NotNull EnvironmentContext envContext,
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

        boolean valid = true;
        if (message != null)
        {
            // todo immediately work out what to do here
            valid = showErrorDialog(config,
                                    envContext,
                                    consoleContext,
                                    IntelliJadResourceBundle.message(message,
                                                                     params));
            consoleContext.addSectionMessage(ConsoleEntryType.ERROR,
                                             message,
                                             params);

        }
        return valid;
    }

    private static boolean showErrorDialog(@NotNull Config config,
                                           @NotNull EnvironmentContext envContext,
                                           @NotNull ConsoleContext consoleContext,
                                           @NotNull String message)
    {
        DialogBuilder builder = new DialogBuilder(envContext.getProject());
        builder.setTitle(IntelliJadResourceBundle.message("plugin.name"));
        builder.addOkAction().setText(IntelliJadResourceBundle.message("option.open-config"));
        builder.addCancelAction().setText(IntelliJadResourceBundle.message("option.cancel-decompilation"));
        JLabel label = new JLabel(message);
        label.setUI(new MultiLineLabelUI());
        builder.setCenterPanel(label);
        builder.setOkActionEnabled(true);

        boolean compile = false;
        switch (builder.show())
        {
            case DialogWrapper.OK_EXIT_CODE:
                // this will cause recursive correction unless cancel is selected
                Project project = envContext.getProject();
                ConfigAccessor configAccessor = config.isUseProjectSpecificSettings() ? new ProjectConfigComponent(project) : new ApplicationConfigComponent();
                boolean x = ShowSettingsUtil.getInstance().editConfigurable(project,
                                                                            configAccessor);
                // why isn't the damn dialog closing?
                compile = validateEnvironment(configAccessor.getConfig(),
                                              envContext,
                                              consoleContext);
                break;
            case DialogWrapper.CANCEL_EXIT_CODE:
                break;
        }

        return compile;
    }
}
