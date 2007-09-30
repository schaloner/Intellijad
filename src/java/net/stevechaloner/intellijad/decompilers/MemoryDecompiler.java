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

package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import net.stevechaloner.intellijad.IntelliJadConstants;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.console.ConsoleContext;
import net.stevechaloner.intellijad.console.ConsoleEntryType;
import net.stevechaloner.intellijad.util.LibraryUtil;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFile;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFileSystem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

/**
 * An in-memory decompiler that catches the piped output of Jad and
 * builds a class from it.
 *
 * @author Steve Chaloner
 */
public class MemoryDecompiler extends AbstractDecompiler
{
    /**
     * Initialises a new instance of this class.
     */
    public MemoryDecompiler()
    {
        setSuccessfulDecompilationAftermathHandler(new DecompilationAftermathHandler()
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

    /** {@inheritDoc} */
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
    protected VirtualFile processOutput(@NotNull final DecompilationDescriptor descriptor,
                                        @NotNull final DecompilationContext context,
                                        @NotNull final String content) throws DecompilationException
    {
        final MemoryVirtualFileSystem vfs = (MemoryVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(IntelliJadConstants.INTELLIJAD_PROTOCOL);
        MemoryVirtualFile file = new MemoryVirtualFile(descriptor.getClassName() + IntelliJadConstants.DOT_JAVA_EXTENSION,
                                                       content);

        this.reformatToStyle(context,
                             file);


        insertIntoFileSystem(descriptor,
                             context,
                             vfs,
                             file);

        final Project project = context.getProject();
        final List<Library> libraries = LibraryUtil.findLibrariesByClass(descriptor.getFullyQualifiedName(),
                                                                         project);

        if (!libraries.isEmpty())
        {
            attachSourceToLibraries(descriptor,
                                    context,
                                    vfs,
                                    libraries);
        }
        else
        {
            context.getConsoleContext().addMessage(ConsoleEntryType.LIBRARY_OPERATION,
                                                   "message.library-not-found-for-class",
                                                   descriptor.getClassName());
        }

        file.setWritable(false);

        return file;
    }

    /**
     * Inserts the file into the file system.
     *
     * @param descriptor the decompilation descriptor
     * @param context the decompilation context
     * @param vfs the virtual file system
     * @param file the file to insert
     */
    protected void insertIntoFileSystem(@NotNull DecompilationDescriptor descriptor,
                                        @NotNull final DecompilationContext context,
                                        @NotNull MemoryVirtualFileSystem vfs, 
                                        @NotNull MemoryVirtualFile file)
    {
        vfs.addFile(file);
        MemoryVirtualFile packageFile = vfs.getFileForPackage(descriptor.getPackageName());
        packageFile.addChild(file);
    }

    /**
     * Attaches the decompiled source to the relevant libraries.
     *
     * @param descriptor the decompilation descriptor
     * @param context the decompilation context
     * @param vfs the memory virtual file system
     * @param libraries the libraries containing class files that match the decompiled source
     */
    protected void attachSourceToLibraries(@NotNull final DecompilationDescriptor descriptor,
                                           @NotNull final DecompilationContext context,
                                           @NotNull final MemoryVirtualFileSystem vfs,
                                           @NotNull final List<Library> libraries)
    {
        ApplicationManager.getApplication().runWriteAction(new Runnable()
        {
            public void run()
            {
                ConsoleContext consoleContext = context.getConsoleContext();
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
                    consoleContext.addMessage(ConsoleEntryType.LIBRARY_OPERATION,
                                              "message.associating-source-with-library",
                                              descriptor.getClassName(),
                                              library.getName() == null ? IntelliJadResourceBundle.message("message.unnamed-library") : library.getName());
                    context.getProject().getUserData(IntelliJadConstants.GENERATED_SOURCE_LIBRARIES).add(library);
                }
            }
        });
    }

    /** {@inheritDoc} */
    protected void updateCommand(StringBuilder command)
    {
        command.append(" -p ");
        if (command.indexOf(" -lnc ") == -1)
        {
            // technically it wouldn't hurt to have this present twice, but this is neater
            command.append(" -lnc ");
        }
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

    /** {@inheritDoc} */
    @Nullable
    public VirtualFile getVirtualFile(DecompilationDescriptor descriptor,
                                      DecompilationContext context)
    {
        final MemoryVirtualFileSystem vfs = (MemoryVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(IntelliJadConstants.INTELLIJAD_PROTOCOL);
        return vfs.findFileByPath(descriptor.getFullyQualifiedNameAsPath());
    }
}
