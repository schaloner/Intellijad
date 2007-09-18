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

package net.stevechaloner.intellijad.decompilers.fs;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import net.stevechaloner.intellijad.IntelliJadConstants;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.config.Config;
import net.stevechaloner.intellijad.console.ConsoleContext;
import net.stevechaloner.intellijad.console.ConsoleEntryType;
import net.stevechaloner.intellijad.decompilers.*;
import net.stevechaloner.intellijad.util.LibraryUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A decompiler that outputs the result to the file system and builds
 * a class based on the file generated by Jad.
 * 
 * @author Steve Chaloner
 */
public class FileSystemDecompiler extends AbstractDecompiler
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
                        context.getConsoleContext().addMessage(ConsoleEntryType.DECOMPILATION_OPERATION,
                                                               "error",
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
                        consoleContext.addMessage(ConsoleEntryType.DECOMPILATION_OPERATION,
                                                  "error",
                                                   err.toString());
                        consoleContext.setWorthDisplaying(true);

                        return null;
                    }
                });
            put(ResultType.SUCCESS,
                new DecompilationAftermathHandler()
                {
                    private VirtualFile getFile(DecompilationContext context,
                                                DecompilationDescriptor descriptor)
                    {
                        final LocalFileSystem vfs = getLocalFileSystem();
                        final Config config = context.getConfig();
                        final File td = new File(config.getOutputDirectory() + '/' + descriptor.getPackageNameAsPath() + descriptor.getClassName() + IntelliJadConstants.DOT_JAVA_EXTENSION);
                        final VirtualFile[] files = new VirtualFile[1];
                        ApplicationManager.getApplication().runWriteAction(new Runnable()
                        {
                            public void run()
                            {
                                files[0] = vfs.refreshAndFindFileByIoFile(td);
                            }
                        });
                        return files[0];
                    }

                    @Nullable
                    public VirtualFile execute(@NotNull DecompilationContext context,
                                               @NotNull DecompilationDescriptor descriptor,
                                               @NotNull File targetClass,
                                               @NotNull ByteArrayOutputStream output,
                                               @NotNull ByteArrayOutputStream err) throws DecompilationException
                    {
                        // todo this sucks - rewrite
                        VirtualFile file = getFile(context,
                                                   descriptor);
                        String content = null;
                        try
                        {
                            content = StreamUtil.readText(file.getInputStream());
                        }
                        catch (IOException e)
                        {
                            throw new DecompilationException(e);
                        }

                        if (DecompilationDescriptor.ClassPathType.FS == descriptor.getClassPathType())
                        {
                            DecompilationDescriptorFactory.getFactoryForFile(targetClass).update(descriptor,
                                                                                                 content);
                        }

                        return processOutput(descriptor,
                                             context,
                                             file);
                    }
                });
        }
    };

    /** {@javadocInherited} */
    protected OperationStatus setup(DecompilationDescriptor descriptor,
                                    DecompilationContext context) throws DecompilationException
    {
        final Config config = context.getConfig();
        final File outputDirectory = new File(config.getOutputDirectory());
        final boolean outputDirExists = outputDirectory.exists();
        OperationStatus status = OperationStatus.CONTINUE;
        if (!outputDirExists && config.isCreateOutputDirectory())
        {
            if (!outputDirectory.mkdirs())
            {
                status = OperationStatus.ABORT;
                context.getConsoleContext().addMessage(ConsoleEntryType.DECOMPILATION_OPERATION,
                                                       "error.could-not-create-output-directory",
                                                       config.getOutputDirectory());
            }
        }
        else if (!outputDirExists)
        {
            status = OperationStatus.ABORT;
            context.getConsoleContext().addMessage(ConsoleEntryType.DECOMPILATION_OPERATION,
                                                   "error.non-existant-output-directory",
                                                   config.getOutputDirectory());
        }
        return status;
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
                                      @NotNull final VirtualFile file) throws DecompilationException
    {
        final Project project = context.getProject();
        final LocalFileSystem vfs = getLocalFileSystem();
        final Config config = context.getConfig();
        final File td = new File(config.getOutputDirectory());
        final VirtualFile targetDirectory = vfs.findFileByIoFile(td);

        ApplicationManager.getApplication().runWriteAction(new Runnable()
        {
            public void run()
            {
                final List<Library> libraries = LibraryUtil.findLibrariesByClass(descriptor.getFullyQualifiedName(),
                                                                                 project);
                if (!libraries.isEmpty())
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
                                    found = targetDirectory.getUrl().equals(urls[i]);
                                }
                                if (!found)
                                {
                                    model.addRoot(targetDirectory,
                                                  OrderRootType.SOURCES);
                                    model.commit();
                                }

                                project.getUserData(IntelliJadConstants.GENERATED_SOURCE_LIBRARIES).add(library);
                                consoleContext.addMessage(ConsoleEntryType.LIBRARY_OPERATION,
                                                          "message.associating-source-with-library",
                                                          descriptor.getClassName(),
                                                          library.getName() == null ? IntelliJadResourceBundle.message("message.unnamed-library") : library.getName());
                            }
                        }
                    });
                    FileEditorManager.getInstance(project).openFile(file,
                                                                    true);
                }
                else
                {
                    context.getConsoleContext().addMessage(ConsoleEntryType.LIBRARY_OPERATION,
                                                           "message.library-not-found-for-class",
                                                           descriptor.getClassName());
                }
            }
        });


        return file;
    }

    /** {@javadocInherited} */
    protected void updateCommand(StringBuilder builder)
    {
        builder.append(" -o -r ");
    }

    /**
     * For some reason, Jad outputs info messages on the error stream
     * when decompiling to disk!
     *
     * @param exitCode the exit code of the process
     * @param err      the error stream of the process
     * @param output   the output of the process
     * @return a result based on the execution of the process
     */
    protected ResultType checkDecompilationStatus(int exitCode,
                                                  @NotNull ByteArrayOutputStream err,
                                                  @NotNull ByteArrayOutputStream output)
    {
        ResultType resultType = ResultType.SUCCESS;
        switch (exitCode)
        {
            case 0:
                if (err.size() > 0)
                {
                    resultType = ResultType.NON_FATAL_ERROR;
                }
                break;
            default:
                resultType = ResultType.FATAL_ERROR;

        }
        return resultType;
    }

    private LocalFileSystem getLocalFileSystem()
    {
        return (LocalFileSystem)VirtualFileManager.getInstance().getFileSystem(LocalFileSystem.PROTOCOL);
    }

    @NotNull
    protected DecompilationAftermathHandler getDecompilationAftermathHandler(@NotNull ResultType resultType)
    {
        return decompilationAftermathHandlers.get(resultType);
    }

    /** {@javadocInherited} */
    public VirtualFile getVirtualFile(DecompilationDescriptor descriptor,
                                      DecompilationContext context)
    {
        final LocalFileSystem vfs = (LocalFileSystem)VirtualFileManager.getInstance().getFileSystem(LocalFileSystem.PROTOCOL);
        VirtualFile file = vfs.findFileByPath(descriptor.getPath());
        // todo implement this!
//        return file;
        return null;
    }
}
