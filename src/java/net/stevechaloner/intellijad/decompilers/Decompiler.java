package net.stevechaloner.intellijad.decompilers;

/**
 */
public interface Decompiler
{
    void decompile(DecompilationDescriptor decompilationDescriptor,
                   DecompilationContext context) throws DecompilationException;
}
