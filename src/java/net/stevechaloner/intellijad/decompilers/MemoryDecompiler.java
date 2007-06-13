package net.stevechaloner.intellijad.decompilers;

import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * @author Steve Chaloner
 */
public class MemoryDecompiler implements Decompiler
{
    // javadoc inherited
    public void decompile(DecompilationDescriptor decompilationDescriptor,
                          DecompilerContext context)
    {
        VirtualFile jarFile = decompilationDescriptor.getJarFile();
        if (jarFile != null)
        {
            try
            {
                ZipFile lib = JarFileSystem.getInstance().getJarFile(jarFile);
                System.out.println(lib.getName());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
