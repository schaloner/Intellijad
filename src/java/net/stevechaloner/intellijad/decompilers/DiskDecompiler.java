package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.ByteArrayOutputStream;

/**
 * @author Steve Chaloner
 */
public class DiskDecompiler extends AbstractDecompiler
{
    // javadoc inherited
    protected VirtualFile processOutput(DecompilationDescriptor descriptor,
                                        DecompilationContext context,
                                        ByteArrayOutputStream content) throws DecompilationException
    {
        throw new DecompilationException(new UnsupportedOperationException());
    }

    // javadoc inherited
    protected void updateCommand(StringBuilder builder)
    {
        builder.append(" -o ");
    }
}
