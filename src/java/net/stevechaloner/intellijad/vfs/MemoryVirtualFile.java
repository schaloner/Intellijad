package net.stevechaloner.intellijad.vfs;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steve Chaloner
 */
public class MemoryVirtualFile extends VirtualFile
{
    /**
     *
     */
    private final String name;

    /**
     *
     */
    private final String content;

    /**
     *
     */
    private final boolean isDirectory;

    /**
     *
     */
    private Map<String, MemoryVirtualFile> children = new HashMap<String, MemoryVirtualFile>();

    /**
     *
     */
    private VirtualFile parent;

    /**
     * @param name
     * @param content
     */
    public MemoryVirtualFile(String name,
                             String content)
    {
        this(name,
             content,
             false);
    }

    /**
     * @param name
     */
    public MemoryVirtualFile(String name)
    {
        this(name,
             null,
             true);
    }

    /**
     * @param name
     * @param content
     * @param isDirectory
     */
    private MemoryVirtualFile(String name,
                              String content,
                              boolean isDirectory)
    {
        this.name = name;
        this.content = content;
        this.isDirectory = isDirectory;
    }

    // javadoc inherited
    @NotNull
    @NonNls
    public String getName()
    {
        return name;
    }

    // javadoc inherited
    @NotNull
    public VirtualFileSystem getFileSystem()
    {
        return VirtualFileManager.getInstance().getFileSystem(MemoryVirtualFileSystem.PROTOCOL);
    }

    // javadoc inherited
    public String getPath()
    {
        VirtualFile parent = getParent();
        return (parent == null) ? name : parent.getPath() + name;
    }

    // javadoc inherited
    public boolean isWritable()
    {
        return false;
    }

    // javadoc inherited
    public boolean isDirectory()
    {
        return isDirectory;
    }

    // javadoc inherited
    public boolean isValid()
    {
        return true;
    }

    // javadoc inherited
    public void setParent(VirtualFile parent)
    {
        this.parent = parent;
    }

    // javadoc inherited
    @Nullable
    public VirtualFile getParent()
    {
        return parent;
    }

    /**
     * @param file
     */
    public void addChild(MemoryVirtualFile file)
    {
        file.setParent(this);
        children.put(file.getName(),
                     file);
    }

    // javadoc inherited
    public VirtualFile[] getChildren()
    {
        return children.values().toArray(new VirtualFile[children.size()]);
    }

    // javadoc inherited
    public OutputStream getOutputStream(Object object,
                                        long l,
                                        long l1) throws IOException
    {
        return new ByteArrayOutputStream();
    }

    // javadoc inherited
    public byte[] contentsToByteArray() throws IOException
    {
        return content.getBytes();
    }

    // javadoc inherited
    public long getTimeStamp()
    {
        return 0L;
    }

    // javadoc inherited
    public long getLength()
    {
        return content.getBytes().length;
    }

    // javadoc inherited
    public void refresh(boolean b, boolean b1, Runnable runnable)
    {
    }

    // javadoc inherited
    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(new byte[0]);
    }

    /**
     * @param name
     * @return
     */
    @Nullable
    public MemoryVirtualFile getChild(String name)
    {
        return children.get(name);
    }

    // javadoc inherited
    public long getModificationStamp()
    {
        return 0L;
    }
}
