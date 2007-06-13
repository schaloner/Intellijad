package net.stevechaloner.intellijad.decompilers;

/**
 * @author Steve Chaloner
 */
public class DiskDecompiler implements Decompiler
{
    // javadoc inherited
    public void decompile(DecompilationDescriptor decompilationDescriptor,
                          DecompilerContext context)
    {
        System.out.println("DiskDecompiler.decompile");
    }
}
