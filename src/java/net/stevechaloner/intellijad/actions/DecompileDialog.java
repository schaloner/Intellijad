package net.stevechaloner.intellijad.actions;

import com.intellij.openapi.project.Project;
import net.stevechaloner.intellijad.DecompilationChoiceListener;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.config.ConfigComponent;
import net.stevechaloner.intellijad.config.NavigationTriggeredDecompile;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DecompileDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBox1;
    private JTabbedPane tabbedPane1;
    private JButton applyButton;
    private JComboBox labelComboBox;

    private final Project project;

    private final DecompilationChoiceListener listener;

    public DecompileDialog(@NotNull Project project,
                           @NotNull final DecompilationChoiceListener listener) {
        this.listener = listener;
        this.project = project;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        comboBox1.addItem(NavigationTriggeredDecompile.ALWAYS);
        comboBox1.addItem(NavigationTriggeredDecompile.ASK);
        comboBox1.addItem(NavigationTriggeredDecompile.NEVER);
        Config config = getComponent(ConfigComponent.class).getConfig();
        if (config != null) {
            comboBox1.setSelectedItem(NavigationTriggeredDecompile.getByName(config.getConfirmNavigationTriggeredDecompile()));
        }

        labelComboBox.addItem(NavigationTriggeredDecompile.ALWAYS);
        labelComboBox.addItem(NavigationTriggeredDecompile.ASK);
        labelComboBox.addItem(NavigationTriggeredDecompile.NEVER);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onApply();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                        0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onApply() {
        Config config = getComponent(ConfigComponent.class).getConfig();
        if (config != null) {
            NavigationTriggeredDecompile option = (NavigationTriggeredDecompile) comboBox1.getSelectedItem();
            config.setConfirmNavigationTriggeredDecompile(option.getName());
        }
    }

    private void onOK() {
        onApply();
        listener.decompile();
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public void setData(Config data) {
        System.out.println("DecompileDialog.setData");
        comboBox1.setSelectedItem(NavigationTriggeredDecompile.getByName(data.getConfirmNavigationTriggeredDecompile()));
    }

    public void getData(Config data) {
        data.setConfirmNavigationTriggeredDecompile(((NavigationTriggeredDecompile) comboBox1.getSelectedItem()).getName());
    }

    public boolean isModified(Config data) {
        return !((NavigationTriggeredDecompile) comboBox1.getSelectedItem()).getName().equals(data.getConfirmNavigationTriggeredDecompile());
    }

    /**
     * Get the required component.
     *
     * @param clazz the component class
     * @return the required component
     */
    private <C> C getComponent(Class<C> clazz) {
        return project.getComponent(clazz);
    }
}
