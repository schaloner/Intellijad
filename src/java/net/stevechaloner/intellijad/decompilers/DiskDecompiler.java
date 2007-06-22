package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Steve Chaloner
 */
public class DiskDecompiler extends AbstractDecompiler
{
    // javadoc inherited
    protected VirtualFile processOutput(DecompilationDescriptor descriptor,
                                        DecompilationContext context,
                                        String content) throws DecompilationException
    {
        throw new DecompilationException(new UnsupportedOperationException());
    }

    // javadoc inherited
    protected void updateCommand(StringBuilder builder)
    {
        builder.append(" -o ");
    }
}
