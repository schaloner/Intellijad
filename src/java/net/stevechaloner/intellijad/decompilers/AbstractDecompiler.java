package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.util.PluginHelper;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipFile;

/**
 * @author Steve Chaloner
 */
abstract class AbstractDecompiler implements Decompiler
{
    /**
     * @param descriptor the decompilation descriptor
     * @param context    the decompilation context
     * @param content    the content of the decompiled file
     * @return a file representing the decompiled file
     * @throws DecompilationException if the processing fails
     */
    protected abstract VirtualFile processOutput(DecompilationDescriptor descriptor,
                                                 DecompilationContext context,
                                                 ByteArrayOutputStream content) throws DecompilationException;

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
        VirtualFile jarFile = descriptor.getJarFile();
        VirtualFile decompiledFile = null;
        if (jarFile != null)
        {
            extractClassFiles(jarFile,
                              context,
                              descriptor);
            File targetClass = new File(context.getTargetDirectory(),
                                        descriptor.getClassName() + '.' + descriptor.getExtension());

            StringBuilder command = new StringBuilder(context.getCommand());
            updateCommand(command);
            command.append(targetClass.getAbsolutePath());
            context.getConsole().appendToConsole(command.toString());

            try
            {
                Process process = Runtime.getRuntime().exec(command.toString());
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ByteArrayOutputStream err = new ByteArrayOutputStream();
                StreamMonitor outputMonitor = new StreamMonitor(context,
                                                                process.getInputStream(),
                                                                output);
                Thread outputThread = new Thread(outputMonitor);
                outputThread.start();
                StreamMonitor errMonitor = new StreamMonitor(context,
                                                             process.getErrorStream(),
                                                             err);
                Thread errThread = new Thread(errMonitor);
                errThread.start();
                int exitCode = process.waitFor();
                outputMonitor.stop();
                errMonitor.stop();

                ResultType resultType = checkDecompilationStatus(exitCode,
                                                                 err,
                                                                 output);
                switch (resultType)
                {
                    case NON_FATAL_ERROR:
                        context.getConsole().appendToConsole(new String(err.toByteArray()));
                    case SUCCESS:
                        decompiledFile = processOutput(descriptor,
                                                       context,
                                                       output);
                        // todo this doesn't belong here
                        if (PluginHelper.getConfig().isClearAndCloseConsoleOnSuccess())
                        {
                            context.getConsole().clearConsoleContent();
                            context.getConsole().closeConsole();
                        }
                        break;
                    case FATAL_ERROR:
                    default:
                        context.getConsole().appendToConsole(new String(err.toByteArray()));
                }
                context.getTargetDirectory().delete();
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

        return decompiledFile;
    }

    /**
     * Calcuates the success of the process execution.
     *
     * @param exitCode the exit code of the process
     * @param err      the error stream of the process
     * @param output   the output of the process
     * @return a result based on the execution of the process
     */
    private ResultType checkDecompilationStatus(int exitCode,
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

    /**
     * Monitors an input stream to prevent it blocking.
     */
    protected class StreamMonitor implements Runnable
    {
        /**
         * The input stream to monitor
         */
        private final InputStream inputStream;

        /**
         * The output stream to move content to
         */
        private final OutputStream outputStream;

        /**
         * The decompilation context.
         */
        private final DecompilationContext context;

        /**
         * Monitor flag.
         */
        private boolean monitor = true;

        /**
         * Initialises a new instance of this class.
         *
         * @param context      the decompilation context
         * @param inputStream  the input stream to monitor
         * @param outputStream the output stream to move content to
         */
        public StreamMonitor(@NotNull DecompilationContext context,
                             @NotNull InputStream inputStream,
                             @NotNull OutputStream outputStream)
        {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
            this.context = context;
        }

        // javadoc inherited
        public void run()
        {
            try
            {
                while (monitor)
                {
                    while (inputStream.available() > 0)
                    {
                        StreamUtil.copyStreamContent(inputStream,
                                                     outputStream);
                    }
                }
            }
            catch (IOException e)
            {
                context.getConsole().appendToConsole(e.getMessage());
            }
        }

        public void stop()
        {
            monitor = false;
        }
    }
}
