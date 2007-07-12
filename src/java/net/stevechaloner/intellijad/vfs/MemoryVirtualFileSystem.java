package net.stevechaloner.intellijad.vfs;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.util.text.StringUtil;
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
    public void addFile(final MemoryVirtualFile file)
    {
        files.put(file.getName(),
                  file);
        fireFileCreated(file);
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
        // todo rewrite this so it doesn't look like crap
        VirtualFile file = null;
        if (!StringUtil.isEmptyOrSpaces(string))
        {
            String path = VirtualFileManager.extractPath(string);
            StringTokenizer st = new StringTokenizer(path, "/");
            VirtualFile currentFile = files.get("root");
            boolean keepLooking = true;
            String targetName = null;
            while (keepLooking && st.hasMoreTokens())
            {
                String element = st.nextToken();
                if (!st.hasMoreTokens())
                {
                    targetName = element;
                }
                VirtualFile child = currentFile.findChild(element);
                if (child != null)
                {
                    currentFile = child;
                }
                else
                {
                    keepLooking = false;
                }
            }

            if (currentFile != null &&
                targetName != null &&
                targetName.equals(currentFile.getName()))
            {
                file = currentFile;
            }
        }
        return file;
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
        // no-op
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
                                                VirtualFile parent,
                                                String name) throws IOException
    {
        final MemoryVirtualFile file = new MemoryVirtualFile(name,
                                                       null);
        file.setParent(parent);
        addFile(file);
        return file;
    }

    // javadoc inherited
    protected MemoryVirtualFile createChildDirectory(Object object,
                                                     VirtualFile parent,
                                                     String name) throws IOException
    {
        MemoryVirtualFile file = new MemoryVirtualFile(name);
        ((MemoryVirtualFile)parent).addChild(file);
        addFile(file);
        return file;
    }

    /**
     * Fires an event to notify listeners of file creation.
     *
     * @param file the new file
     */
    private void fireFileCreated(final VirtualFile file)
    {
        ApplicationManager.getApplication().runWriteAction(new Runnable()
        {
            public void run()
            {
                fireFileCreated(null,
                                file);
            }
        });
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
    public MemoryVirtualFile getFileForPackage(@NotNull String packageName)
    {
        StringTokenizer st = new StringTokenizer(packageName, ".");
        List<String> names = new ArrayList<String>();
        while (st.hasMoreTokens())
        {
            names.add(st.nextToken());
        }
        return getFileForPackage(names,
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
    private MemoryVirtualFile getFileForPackage(@NotNull List<String> names,
                                                @NotNull MemoryVirtualFile parent)
    {
        MemoryVirtualFile child = null;
        if (!names.isEmpty())
        {
            String name = names.remove(0);
            child = parent.getChild(name);
            if (child == null)
            {
                try
                {
                    child = createChildDirectory(null,
                                                 parent,
                                                 name);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (child != null && !names.isEmpty())
        {
            child = getFileForPackage(names,
                                     child);
        }
        return child;
    }

    // javadoc inherited
    public void projectOpened()
    {
    }

    // javadoc inherited
    public void projectClosed()
    {
        files.clear();
    }
}
