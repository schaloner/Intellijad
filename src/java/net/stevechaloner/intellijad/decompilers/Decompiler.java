package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;

/**
 */
public interface Decompiler
{
    VirtualFile decompile(DecompilationDescriptor decompilationDescriptor,
                          DecompilationContext context) throws DecompilationException;
}
