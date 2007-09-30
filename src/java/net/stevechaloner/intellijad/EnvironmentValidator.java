package net.stevechaloner.intellijad;

import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MultiLineLabelUI;
import com.intellij.openapi.util.text.StringUtil;

import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.console.ConsoleContext;
import net.stevechaloner.intellijad.console.ConsoleEntryType;

import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;
import java.io.File;

/**
 * @author Steve Chaloner
 */
public class EnvironmentValidator
{
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
                                           @NotNull String message)
    {
        DialogBuilder builder = new DialogBuilder(envContext.getProject());
        builder.setTitle(IntelliJadResourceBundle.message("plugin.name"));
        builder.addCancelAction().setText(IntelliJadResourceBundle.message("option.open-config"));
        JLabel label = new JLabel(message);
        label.setUI(new MultiLineLabelUI());
        builder.setCenterPanel(label);
        builder.setOkActionEnabled(true);
        switch (builder.show())
        {
            case DialogWrapper.CANCEL_EXIT_CODE:
//                decompilePopup.persistConfig();
                break;
        }

        return true;
    }
}
