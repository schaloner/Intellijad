package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.util.StreamPumper;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * @author Steve Chaloner
 */
abstract class AbstractDecompiler implements Decompiler
{
    protected enum OperationStatus { CONTINUE, ABORT }

    /**
     * Perform pre-compilation operations.
     * 
     * @param descriptor the decompilation descriptor
     * @param context    the decompilation context
     * @return the last chance to abort the operation before decompilation
     * @throws DecompilationException if the operation fails
     */
    protected abstract OperationStatus setup(DecompilationDescriptor descriptor,
                                              DecompilationContext context) throws DecompilationException;

    /**
     * @param descriptor the decompilation descriptor
     * @param context    the decompilation context
     * @param content    the content of the decompiled file
     * @return a file representing the decompiled file
     * @throws DecompilationException if the processing fails
     */
    protected abstract VirtualFile processOutput(@NotNull final DecompilationDescriptor descriptor,
                                                 @NotNull final DecompilationContext context,
                                                 @NotNull final String content) throws DecompilationException;

    /**
     * Updates the command to insert any specific arguments.
     *
     * @param command the process execution string
     */
    protected abstract void updateCommand(StringBuilder command);

    // javadoc inherited
    public VirtualFile decompile(DecompilationDescriptor descriptor,
                                 DecompilationContext context) throws DecompilationException
    {
        VirtualFile decompiledFile = null;
        try
        {
            boolean goodToGo = false;
            switch (descriptor.getClassPathType())
            {
                case JAR:
                    JarDecompilationDescriptor jarDD = (JarDecompilationDescriptor) descriptor;
                    VirtualFile jarFile = jarDD.getJarFile();
                    if (jarFile != null)
                    {
                        extractClassFiles(jarFile,
                                          context,
                                          descriptor);
                        goodToGo = true;
                    }
                    break;
                default:
                    goodToGo = true;
            }
            if (goodToGo)
            {
                File targetClass = descriptor.getSourceFile(context.getTargetDirectory());

                StringBuilder command = new StringBuilder(context.getCommand());
                updateCommand(command);
                command.append(targetClass.getAbsolutePath());
                context.getConsole().appendToConsole(command.toString());

                try
                {
                    OperationStatus status = setup(descriptor,
                                                   context);
                    if (status == OperationStatus.CONTINUE)
                    {
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        ByteArrayOutputStream err = new ByteArrayOutputStream();
                        ResultType resultType = runExternalDecompiler(command.toString(),
                                                                      context,
                                                                      output,
                                                                      err);
                        switch (resultType)
                        {
                            case NON_FATAL_ERROR:
                                context.getConsole().appendToConsole(err.toString());
                            case SUCCESS:
                                String content = output.toString();
                                if (DecompilationDescriptor.ClassPathType.FS == descriptor.getClassPathType())
                                {
                                    DecompilationDescriptorFactory.getFactoryForFile(targetClass).update(descriptor,
                                                                                                         content);
                                }
                                decompiledFile = processOutput(descriptor,
                                                               context,
                                                               content);
                                // todo this doesn't belong here
                                if (context.getConfig().isClearAndCloseConsoleOnSuccess())
                                {
                                    context.getConsole().clearConsoleContent();
                                    context.getConsole().closeConsole();
                                }
                                break;
                            case FATAL_ERROR:
                            default:
                                context.getConsole().appendToConsole(err.toString());
                        }
                    }
                }
                catch (IOException e)
                {
                    throw new DecompilationException(e);
                }
                catch (InterruptedException e)
                {
                    throw new DecompilationException(e);
                }
            }
        }
        finally
        {
            context.getTargetDirectory().delete();
        }

        return decompiledFile;
    }

    /**
     * 
     * @param command
     * @param context
     * @param output
     * @param err
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private ResultType runExternalDecompiler(String command,
                                             DecompilationContext context,
                                             ByteArrayOutputStream output,
                                             ByteArrayOutputStream err) throws IOException,
                                                                               InterruptedException
    {
        Process process = Runtime.getRuntime().exec(command);
        StreamPumper outputPumper = new StreamPumper(context,
                                                     process.getInputStream(),
                                                     output);
        Thread outputThread = new Thread(outputPumper);
        outputThread.start();
        StreamPumper errPumper = new StreamPumper(context,
                                                  process.getErrorStream(),
                                                  err);
        Thread errThread = new Thread(errPumper);
        errThread.start();
        int exitCode = process.waitFor();
        outputPumper.stopPumping();
        errPumper.stopPumping();

        return checkDecompilationStatus(exitCode,
                                        err,
                                        output);
    }

    /**
     * Calcuates the success of the process execution.
     *
     * @param exitCode the exit code of the process
     * @param err      the error stream of the process
     * @param output   the output of the process
     * @return a result based on the execution of the process
     */
    protected abstract ResultType checkDecompilationStatus(int exitCode,
                                                           ByteArrayOutputStream err,
                                                           ByteArrayOutputStream output);

    /**
     * Extract the class files from the library to the target directory.
     *
     * @param jarFile                 the library containing the class files
     * @param context                 the context
     * @param decompilationDescriptor the decompilation descriptor
     * @throws DecompilationException if an error occurs extracting the class files
     */
    private void extractClassFiles(VirtualFile jarFile,
                                   DecompilationContext context,
                                   DecompilationDescriptor decompilationDescriptor) throws DecompilationException
    {
        try
        {
            ZipFile lib = JarFileSystem.getInstance().getJarFile(jarFile);
            context.getConsole().appendToConsole(IntelliJadResourceBundle.message("message.examining",
                                                                                  jarFile.getPath()));
            ZipExtractor zipExtractor = new ZipExtractor();
            zipExtractor.extract(context,
                                 lib,
                                 decompilationDescriptor.getPackageNameAsPath(),
                                 decompilationDescriptor.getClassName());
        }
        catch (IOException e)
        {
            throw new DecompilationException(e);
        }
    }
}
