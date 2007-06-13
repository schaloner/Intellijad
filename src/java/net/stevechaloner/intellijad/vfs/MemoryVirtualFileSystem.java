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
     *
     */
    private final Map<String, MemoryVirtualFile> files = new HashMap<String, MemoryVirtualFile>();

    /**
     * @param file
     */
    public void addFile(MemoryVirtualFile file)
    {
        files.put(file.getName(),
                  file);
    }

    // javadoc inherited
    public String getProtocol()
    {
        return "mem";
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
     * @param packageName
     * @return
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
     * @param names
     * @param parent
     * @return
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
