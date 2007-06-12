package net.stevechaloner.intellijad;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import net.stevechaloner.intellijad.actions.DecompileDialog;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.config.ConfigComponent;
import net.stevechaloner.intellijad.config.NavigationTriggeredDecompile;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class IntelliJad implements ProjectComponent,
        FileEditorManagerListener,
        DecompilationChoiceListener {

    public static final String COMPONENT_NAME = "net.stevechaloner.idea";

    private final Project project;

    public IntelliJad(Project project) {
        this.project = project;
    }

    public void fileOpened(FileEditorManager source,
                           VirtualFile file) {
        // no-op
    }

    public void fileClosed(FileEditorManager source,
                           VirtualFile file) {
        // no-op
    }

    public void selectionChanged(FileEditorManagerEvent event) {

        selectedFileChanged(event);
    }

    public void selectedFileChanged(final FileEditorManagerEvent e) {
        VirtualFile file = e.getNewFile();
        if (file != null && "class".equals(file.getExtension())) {
            ConfigComponent configComponent = getComponent(ConfigComponent.class);
            if (configComponent != null) {
                Config config = configComponent.getConfig();
                if (config != null) {
                    switch (NavigationTriggeredDecompile.getByName(config.getConfirmNavigationTriggeredDecompile())) {
                        case ALWAYS:
                            decompile();
                            break;
                        case ASK:
                            DecompileDialog dialog = new DecompileDialog(project,
                                    this);
                            dialog.pack();
                            dialog.setVisible(true);
                            break;
                        case NEVER:
                            JOptionPane.showMessageDialog(new JLabel(),
                                    "NEVER");
                            break;
                    }
                }
            }
        }
    }

    @NotNull
    public String getComponentName() {
        return COMPONENT_NAME;
    }


    public void projectOpened() {
        FileEditorManager.getInstance(project).addFileEditorManagerListener(this);
    }

    public void projectClosed() {
        FileEditorManager.getInstance(project).removeFileEditorManagerListener(this);
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    /**
     * Get the required component.
     * todo move this to a helper class
     *
     * @param clazz the component class
     * @return the required component
     */
    private <C> C getComponent(Class<C> clazz) {
        return project.getComponent(clazz);
    }


    public void decompile() {
        System.out.println("IntelliJad.decompile");
    }
}
