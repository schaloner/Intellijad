package net.stevechaloner.intellijad.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import net.stevechaloner.idea.util.events.DataContextHelper;

/***
 *
 */
public class DecompileAction extends AnAction {
    // javadoc inherited
    public void update(AnActionEvent e) {
        super.update(e);

        String extension = DataContextHelper.getFileExtension(e.getDataContext());
        this.getTemplatePresentation().setEnabled(extension != null && "class".equals(extension));
    }

    // javadoc inherited
    public void actionPerformed(AnActionEvent e) {
    }
}
