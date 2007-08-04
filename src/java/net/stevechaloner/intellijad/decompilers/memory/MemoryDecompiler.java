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

package net.stevechaloner.intellijad.decompilers.memory;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import net.stevechaloner.intellijad.IntelliJadConstants;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.console.ConsoleContext;
import net.stevechaloner.intellijad.console.ConsoleEntryType;
import net.stevechaloner.intellijad.decompilers.AbstractDecompiler;
import net.stevechaloner.intellijad.decompilers.DecompilationContext;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptor;
import net.stevechaloner.intellijad.decompilers.DecompilationDescriptorFactory;
import net.stevechaloner.intellijad.decompilers.DecompilationException;
import net.stevechaloner.intellijad.decompilers.ResultType;
import net.stevechaloner.intellijad.util.LibraryUtil;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFile;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory decompiler that catches the piped output of Jad and
 * builds a class from it.
 *
 * @author Steve Chaloner
 */
public class MemoryDecompiler extends AbstractDecompiler
{
    /**
     *
     */
    private final Map<ResultType, DecompilationAftermathHandler> decompilationAftermathHandlers = new HashMap<ResultType, DecompilationAftermathHandler>()
    {
        {
            put(ResultType.NON_FATAL_ERROR,
                new DecompilationAftermathHandler()
                {
                    @Nullable
                    public VirtualFile execute(@NotNull DecompilationContext context,
                                               @NotNull DecompilationDescriptor descriptor,
                                               @NotNull File targetClass,
                                               @NotNull ByteArrayOutputStream output,
                                               @NotNull ByteArrayOutputStream err) throws DecompilationException
                    {
                        VirtualFile file = get(ResultType.SUCCESS).execute(context,
                                                                           descriptor,
                                                                           targetClass,
                                                                           output,
                                                                           err);
                        context.getConsoleContext().addMessage("error",
                                                               err.toString());
                        return file;
                    }
                });
            put(ResultType.FATAL_ERROR,
                new DecompilationAftermathHandler()
                {
                    @Nullable
                    public VirtualFile execute(@NotNull DecompilationContext context,
                                               @NotNull DecompilationDescriptor descriptor,
                                               @NotNull File targetClass,
                                               @NotNull ByteArrayOutputStream output,
                                               @NotNull ByteArrayOutputStream err) throws DecompilationException
                    {
                        ConsoleContext consoleContext = context.getConsoleContext();
                        consoleContext.addMessage("error",
                                                  err.toString());
                        consoleContext.setWorthDisplaying(true);
                        return null;
                    }
                });
            put(ResultType.SUCCESS,
                new DecompilationAftermathHandler()
                {
                    @Nullable
                    public VirtualFile execute(@NotNull DecompilationContext context,
                                               @NotNull DecompilationDescriptor descriptor,
                                               @NotNull File targetClass,
                                               @NotNull ByteArrayOutputStream output,
                                               @NotNull ByteArrayOutputStream err) throws DecompilationException
                    {
                        String content = output.toString();
                        if (DecompilationDescriptor.ClassPathType.FS == descriptor.getClassPathType())
                        {
                            DecompilationDescriptorFactory.getFactoryForFile(targetClass).update(descriptor,
                                                                                                 content);
                        }
                        return processOutput(descriptor,
                                             context,
                                             content);
                    }
                });
        }
    };
    
    /** {@javadocInherited} */
    protected OperationStatus setup(DecompilationDescriptor descriptor,
                                    DecompilationContext context) throws DecompilationException
    {
        return OperationStatus.CONTINUE;
    }

    /**
     * 
     * @param descriptor the decompilation descriptor
     * @param context    the decompilation context
     * @param content    the content of the decompiled file
     * @return a file representing the decompiled file
     * @throws DecompilationException if the processing fails
     */
    private VirtualFile processOutput(@NotNull final DecompilationDescriptor descriptor,
                                      @NotNull final DecompilationContext context,
                                      @NotNull final String content) throws DecompilationException
    {
        final MemoryVirtualFileSystem vfs = (MemoryVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(IntelliJadConstants.INTELLIJAD_PROTOCOL);
        MemoryVirtualFile file = new MemoryVirtualFile(descriptor.getClassName() + IntelliJadConstants.DOT_JAVA_EXTENSION,
                                                       content);
        vfs.addFile(file);

        final Project project = context.getProject();
        MemoryVirtualFile showdom = vfs.getFileForPackage(descriptor.getPackageName());
        showdom.addChild(file);

        final List<Library> libraries = LibraryUtil.findLibrariesByClass(descriptor.getFullyQualifiedName(),
                                                                         project);

        if (!libraries.isEmpty())
        {
            ApplicationManager.getApplication().runWriteAction(new Runnable()
            {
                public void run()
                {
                    ConsoleContext consoleContext = context.getConsoleContext();
                    consoleContext.addSubsection(ConsoleEntryType.LIBRARY_OPERATION);
                    for (Library library : libraries)
                    {
                        Library.ModifiableModel model = library.getModifiableModel();
                        String[] urls = model.getUrls(OrderRootType.SOURCES);
                        boolean found = false;
                        for (int i = 0; !found && i < urls.length; i++)
                        {
                            found = IntelliJadConstants.ROOT_URI.equals(urls[i]);
                        }
                        if (!found)
                        {
                            model.addRoot(vfs.findFileByPath(IntelliJadConstants.INTELLIJAD_ROOT),
                                          OrderRootType.SOURCES);
                            model.commit();
                        }
                        consoleContext.addMessage("message.associating-source-with-library",
                                                  descriptor.getClassName(),
                                                  library.getName() == null ? IntelliJadResourceBundle.message("message.unnamed-library") : library.getName());
                        project.getUserData(IntelliJadConstants.GENERATED_SOURCE_LIBRARIES).add(library);
                    }
                }
            });
            FileEditorManager.getInstance(project).openFile(file,
                                                            true);

        }
        else
        {
            context.getConsoleContext().addMessage("message.library-not-found-for-class",
                                                   descriptor.getClassName());
        }

        return file;
    }

    /** {@javadocInherited} */
    protected void updateCommand(StringBuilder command)
    {
        command.append(" -p ");
    }

    /**
     * Calcuates the success of the process execution.
     *
     * @param exitCode the exit code of the process
     * @param err      the error stream of the process
     * @param output   the output of the process
     * @return a result based on the execution of the process
     */
    protected ResultType checkDecompilationStatus(int exitCode,
                                                  ByteArrayOutputStream err,
                                                  ByteArrayOutputStream output)
    {
        ResultType resultType = ResultType.SUCCESS;
        switch (exitCode)
        {
            case 0:
                if (err.size() > 0 && output.size() > 0)
                {
                    resultType = ResultType.NON_FATAL_ERROR;
                }
                else if (err.size() > 0)
                {
                    resultType = ResultType.FATAL_ERROR;
                }
                break;
            default:
                resultType = ResultType.FATAL_ERROR;

        }
        return resultType;
    }

    /** {@javadocInherited} */
    @NotNull
    protected DecompilationAftermathHandler getDecompilationAftermathHandler(@NotNull ResultType resultType)
    {
        return decompilationAftermathHandlers.get(resultType);
    }

    /** {@javadocInherited} */
    public VirtualFile getVirtualFile(DecompilationDescriptor descriptor,
                                      DecompilationContext context)
    {
        final MemoryVirtualFileSystem vfs = (MemoryVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(IntelliJadConstants.INTELLIJAD_PROTOCOL);
        return vfs.findFileByPath(descriptor.getFullyQualifiedNameAsPath());
    }
}
