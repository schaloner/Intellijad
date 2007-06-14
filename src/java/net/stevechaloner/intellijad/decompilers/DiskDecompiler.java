package net.stevechaloner.intellijad.decompilers;

/**
 * @author Steve Chaloner
 */
public class DiskDecompiler implements Decompiler
{
    // javadoc inherited
    public void decompile(DecompilationDescriptor decompilationDescriptor,
                          DecompilationContext context) throws DecompilationException
    {
        throw new DecompilationException(new UnsupportedOperationException());
    }
}
