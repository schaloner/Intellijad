package net.stevechaloner.intellijad.vfs;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Steve Chaloner
 */
public class MemoryVirtualFileSystem extends VirtualFileSystem implements ApplicationComponent
{
    /**
     * The protocol name.
     */
    public static final String PROTOCOL = "intellijad";

    /**
     * The files.
     */
    private final Map<String, MemoryVirtualFile> files = new HashMap<String, MemoryVirtualFile>();

    /**
     * Add a file to the file system.
     *
     * @param file the file to add
     */
    public void addFile(MemoryVirtualFile file)
    {
        files.put(file.getName(),
                  file);
    }

    // javadoc inherited
    public String getProtocol()
    {
        return PROTOCOL;
    }

    // javadoc inherited
    @Nullable
    public VirtualFile findFileByPath(String string)
    {
        String s = VirtualFileManager.extractPath(string);
        return files.get(s);
    }

    // javadoc inherited
    public void refresh(boolean b)
    {
    }

    // javadoc inherited
    @Nullable
    public VirtualFile refreshAndFindFileByPath(String string)
    {
        return files.get(string);
    }

    // javadoc inherited
    public void forceRefreshFiles(boolean b,
                                  @NotNull VirtualFile... virtualFiles)
    {
    }

    // javadoc inherited
    protected void deleteFile(Object object,
                              VirtualFile virtualFile) throws IOException
    {
        files.remove(virtualFile.getName());
    }

    // javadoc inherited
    protected void moveFile(Object object,
                            VirtualFile virtualFile,
                            VirtualFile virtualFile1) throws IOException
    {
        files.remove(virtualFile.getName());
        files.put(virtualFile1.getName(),
                  (MemoryVirtualFile) virtualFile1);
    }

    // javadoc inherited
    protected void renameFile(Object object,
                              VirtualFile virtualFile,
                              String string) throws IOException
    {
        files.remove(virtualFile.getName());
        files.put(string,
                  (MemoryVirtualFile) virtualFile);
    }

    // javadoc inherited
    protected MemoryVirtualFile createChildFile(Object object,
                                                VirtualFile virtualFile,
                                                String string) throws IOException
    {
        MemoryVirtualFile file = new MemoryVirtualFile(string, null);
        file.setParent(virtualFile);
        return file;
    }

    // javadoc inherited
    protected MemoryVirtualFile createChildDirectory(Object object,
                                                     VirtualFile virtualFile,
                                                     String string) throws IOException
    {
        MemoryVirtualFile file = new MemoryVirtualFile(string);
        file.setParent(virtualFile);
        return file;
    }

    // javadoc inherited
    @NonNls
    @NotNull
    public String getComponentName()
    {
        return "MemoryFileSystem";
    }

    // javadoc inherited
    public void initComponent()
    {
        MemoryVirtualFile root = new MemoryVirtualFile("root");
        addFile(root);
    }

    // javadoc inherited
    public void disposeComponent()
    {
        files.clear();
    }

    /**
     * For a given package, e.g. net.stevechaloner.intellijad, get the file corresponding
     * to the last element, e.g. intellijad.  If the file or any part of the directory tree
     * does not exist, it is created dynamically.
     *
     * @param packageName the name of the package
     * @return the file corresponding to the final location of the package
     */
    public MemoryVirtualFile getFileByPackage(String packageName)
    {
        StringTokenizer st = new StringTokenizer(packageName, ".");
        List<String> names = new ArrayList<String>();
        while (st.hasMoreTokens())
        {
            names.add(st.nextToken());
        }
        return getFileByPackage(names,
                                files.get("root"));
    }

    /**
     * Recursively search for, and if necessary create, the final file in the
     * name list.
     *
     * @param names  the name list
     * @param parent the parent file
     * @return a file corresponding to the last entry in the name list
     */
    private MemoryVirtualFile getFileByPackage(List<String> names,
                                               MemoryVirtualFile parent)
    {
        MemoryVirtualFile child = null;
        if (!names.isEmpty())
        {
            String name = names.remove(0);
            child = parent.getChild(name);
            if (child == null)
            {
                child = new MemoryVirtualFile(name);
                parent.addChild(child);
            }
        }

        if (child != null && !names.isEmpty())
        {
            child = getFileByPackage(names,
                                     child);
        }
        return child;
    }
}
