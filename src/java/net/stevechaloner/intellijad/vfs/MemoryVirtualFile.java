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

    private final String name;

    private final String content;

    private final boolean isDirectory;

    private Map<String, MemoryVirtualFile> children = new HashMap<String, MemoryVirtualFile>();

    private VirtualFile parent;

    public MemoryVirtualFile(String name,
                             String content)
    {
        this(name,
             content,
             false);
    }

    public MemoryVirtualFile(String name)
    {
        this(name,
             null,
             true);
    }

    private MemoryVirtualFile(String name,
                              String content,
                              boolean isDirectory)
    {
        this.name = name;
        this.content = content;
        this.isDirectory = isDirectory;
    }

    @NotNull
    @NonNls
    public String getName()
    {
        return name;
    }

    @NotNull
    public VirtualFileSystem getFileSystem()
    {
        return VirtualFileManager.getInstance().getFileSystem(MemoryVirtualFileSystem.PROTOCOL);
    }

    public String getPath()
    {
        VirtualFile parent = getParent();
        return (parent == null) ? name : parent.getPath() + name;
    }

    public boolean isWritable()
    {
        return false;
    }

    public boolean isDirectory()
    {
        return isDirectory;
    }

    public boolean isValid()
    {
        return true;
    }

    public void setParent(VirtualFile parent)
    {
        this.parent = parent;
    }

    @Nullable
    public VirtualFile getParent()
    {
        return parent;
    }

    public void addChild(MemoryVirtualFile file)
    {
        file.setParent(this);
        children.put(file.getName(),
                     file);
    }

    public VirtualFile[] getChildren()
    {
        return children.values().toArray(new VirtualFile[children.size()]);
    }

    public OutputStream getOutputStream(Object object,
                                        long l,
                                        long l1) throws IOException
    {
        return new ByteArrayOutputStream();
    }

    public byte[] contentsToByteArray() throws IOException
    {
        return content.getBytes();
    }

    public long getTimeStamp()
    {
        return 0L;
    }

    public long getLength()
    {
        return content.getBytes().length;
    }

    public void refresh(boolean b, boolean b1, Runnable runnable)
    {
    }

    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Nullable
    public MemoryVirtualFile getChild(String name)
    {
        return children.get(name);
    }

    public long getModificationStamp()
    {
        return 0L;
    }
}
