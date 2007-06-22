package net.stevechaloner.intellijad.actions;

import com.intellij.openapi.project.Project;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.config.ExclusionTableModel;
import net.stevechaloner.intellijad.config.NavigationTriggeredDecompile;
import net.stevechaloner.intellijad.decompilers.DecompilationChoiceListener;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptor;
import net.stevechaloner.intellijad.util.PluginUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DecompileDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBox1;
    private JTabbedPane tabbedPane1;
    private JButton applyButton;
    private JLabel confirmDecompileLabel;
    private JCheckBox excludePackageCheckBox;
    private JCheckBox excludeRecursivelyCheckBox;

    private final DecompilationChoiceListener listener;

    private final DecompilationDescriptor decompilationDescriptor;

    public DecompileDialog(@NotNull DecompilationDescriptor decompilationDescriptor,
                           @NotNull Project project,
                           @NotNull final DecompilationChoiceListener listener)
    {
        this.decompilationDescriptor = decompilationDescriptor;
        this.listener = listener;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        confirmDecompileLabel.setText(IntelliJadResourceBundle.message("message.confirm-decompile",
                                                                       decompilationDescriptor.getClassName()));
        excludePackageCheckBox.setText(IntelliJadResourceBundle.message("config.exclude-package",
                                                                        decompilationDescriptor.getPackageName()));
        excludeRecursivelyCheckBox.setText(IntelliJadResourceBundle.message("config.exclude-recursively"));
        excludePackageCheckBox.addChangeListener(new ChangeListener()
        {

            public void stateChanged(ChangeEvent e)
            {
                excludeRecursivelyCheckBox.setEnabled(excludePackageCheckBox.isSelected());
            }
        });

        Config config = PluginUtil.getConfig();
        excludeRecursivelyCheckBox.setSelected(config.isAlwaysExcludeRecursively());

        comboBox1.addItem(NavigationTriggeredDecompile.ALWAYS);
        comboBox1.addItem(NavigationTriggeredDecompile.ASK);
        comboBox1.addItem(NavigationTriggeredDecompile.NEVER);
        comboBox1.setSelectedItem(NavigationTriggeredDecompile.getByName(config.getConfirmNavigationTriggeredDecompile()));

        buttonOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });

        applyButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onApply();
            }
        });

        buttonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        },
                                           KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                                                                  0),
                                           JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onApply()
    {
        Config config = PluginUtil.getConfig();
        if (config != null)
        {
            NavigationTriggeredDecompile option = (NavigationTriggeredDecompile) comboBox1.getSelectedItem();
            config.setConfirmNavigationTriggeredDecompile(option.getName());

            String packageName = decompilationDescriptor.getPackageName();
            if (packageName != null && excludePackageCheckBox.isSelected())
            {
                ExclusionTableModel tableModel = config.getExclusionTableModel();
                tableModel.addExclusion(packageName,
                                        excludeRecursivelyCheckBox.isSelected());
            }
        }
    }

    private void onOK()
    {
        onApply();
        listener.decompile(decompilationDescriptor);
        dispose();
    }

    private void onCancel()
    {
        dispose();
    }

    public boolean isModified(Config data)
    {
        return !((NavigationTriggeredDecompile) comboBox1.getSelectedItem()).getName().equals(data.getConfirmNavigationTriggeredDecompile());
    }
}
