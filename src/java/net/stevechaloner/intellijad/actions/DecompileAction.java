package net.stevechaloner.intellijad.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.vfs.VirtualFile;
import net.stevechaloner.idea.util.events.DataContextUtil;
import net.stevechaloner.intellijad.EnvironmentContext;
import net.stevechaloner.intellijad.IntelliJad;
import net.stevechaloner.intellijad.console.IntelliJadConsole;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptor;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptorFactory;
import net.stevechaloner.intellijad.util.PluginUtil;

/***
 *
 */
public class DecompileAction extends AnAction
{
    // javadoc inherited
    public void update(AnActionEvent e)
    {
        super.update(e);

        String extension = DataContextUtil.getFileExtension(e.getDataContext());
        this.getTemplatePresentation().setEnabled(extension != null && "class".equals(extension));
    }

    // javadoc inherited
    public void actionPerformed(AnActionEvent e)
    {
        DataContext dataContext = e.getDataContext();
        if ("class".equals(DataContextUtil.getFileExtension(dataContext)))
        {
            IntelliJad intelliJad = PluginUtil.getComponent(IntelliJad.class);
            VirtualFile file = DataContextUtil.getFile(e.getDataContext());
            if (file != null)
            {
                DecompilationDescriptor descriptor = DecompilationDescriptorFactory.getFactoryForFile(file).create(file);
                intelliJad.decompile(new EnvironmentContext(PluginUtil.getProject(e.getDataContext())),
                                     descriptor);
            }
            else
            {
                IntelliJadConsole console = PluginUtil.getComponent(IntelliJadConsole.class);
                // todo i18n here
                console.appendToConsole("file is null");
            }
        }
    }
}
