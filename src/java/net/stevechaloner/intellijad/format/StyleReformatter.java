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

package net.stevechaloner.intellijad.format;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import net.stevechaloner.intellijad.console.ConsoleContext;
import net.stevechaloner.intellijad.console.ConsoleEntryType;
import net.stevechaloner.intellijad.decompilers.DecompilationContext;

/**
 * @author Steve Chaloner
 */
public class StyleReformatter
{
    /**
     * Reformats the content of the given file to match the IDE settings.
     *
     * @param context the context the decompilation is occurring in
     * @param file the file representing the source code
     * @return true if reformatted
     */
    public static boolean reformat(final DecompilationContext context,
                                   final VirtualFile file)
    {
        boolean reformatted = false;
        final FileDocumentManager fileDocManager = FileDocumentManager.getInstance();
        final Document document = fileDocManager.getDocument(file);
        if (document != null)
        {
            final Project project = context.getProject();
            final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            if (psiFile != null)
            {
                final boolean[] result = {false};
                final ConsoleContext consoleContext = context.getConsoleContext();
                ApplicationManager.getApplication().runWriteAction(new Runnable()
                {
                    public void run()
                    {
                        CodeStyleManager styleManager = CodeStyleManager.getInstance(project);
                        try
                        {
                            styleManager.optimizeImports(psiFile);
                            styleManager.reformat(psiFile);
                            fileDocManager.saveDocument(document);
                            result[0] = true;
                            consoleContext.addSectionMessage(ConsoleEntryType.INFO,
                                                             "message.reformatting",
                                                             file.getName());
                        }
                        catch (IncorrectOperationException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                reformatted = result[0];
            }
        }

        return reformatted;
    }
}
