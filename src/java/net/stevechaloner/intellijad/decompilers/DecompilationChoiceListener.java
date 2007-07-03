package net.stevechaloner.intellijad.decompilers;

import net.stevechaloner.intellijad.EnvironmentContext;

/**
 * @author Steve Chaloner
 */
public interface DecompilationChoiceListener
{
    /**
     * 
     * @param environmentContext
     * @param decompilationDescriptor
     */
    void decompile(EnvironmentContext environmentContext,
                   DecompilationDescriptor decompilationDescriptor);
}
