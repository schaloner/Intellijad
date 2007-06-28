package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

/**
 */
public interface Decompiler
{
    /**
     *
     * @param decompilationDescriptor
     * @param context
     * @return
     * @throws DecompilationException
     */
    @Nullable
    VirtualFile decompile(DecompilationDescriptor decompilationDescriptor,
                          DecompilationContext context) throws DecompilationException;
}
