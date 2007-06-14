package net.stevechaloner.intellijad.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import net.stevechaloner.idea.util.events.DataContextHelper;
import net.stevechaloner.intellijad.IntelliJad;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptor;
import net.stevechaloner.intellijad.util.PluginHelper;

/***
 *
 */
public class DecompileAction extends AnAction
{
    // javadoc inherited
    public void update(AnActionEvent e)
    {
        super.update(e);

        String extension = DataContextHelper.getFileExtension(e.getDataContext());
        this.getTemplatePresentation().setEnabled(extension != null && "class".equals(extension));
    }

    // javadoc inherited
    public void actionPerformed(AnActionEvent e)
    {
        DataContext dataContext = e.getDataContext();
        if ("class".equals(DataContextHelper.getFileExtension(dataContext)))
        {
            IntelliJad intelliJad = PluginHelper.getComponent(DataContextHelper.getProject(dataContext),
                                                              IntelliJad.class);
            DecompilationDescriptor descriptor = new DecompilationDescriptor(DataContextHelper.getFile(e.getDataContext()));
            intelliJad.decompile(descriptor);
        }
    }
}
