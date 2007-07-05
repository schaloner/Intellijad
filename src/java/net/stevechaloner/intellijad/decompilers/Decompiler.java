/*
 * Copyright 2007 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

/**
 * A decompiler is used to convert a class file into a Java file.
 */
public interface Decompiler
{
    /**
     * Decompiles the class detailed in the decompilation descriptor.
     *
     * @param decompilationDescriptor a description of the class to decompile
     * @param context the context of the decompilation
     * @return a file representing the decompiled class
     * @throws DecompilationException if something prevents the operation
     */
    @Nullable
    VirtualFile decompile(DecompilationDescriptor decompilationDescriptor,
                          DecompilationContext context) throws DecompilationException;
}
