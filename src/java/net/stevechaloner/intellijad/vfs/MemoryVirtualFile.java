package net.stevechaloner.intellijad.vfs;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import net.stevechaloner.intellijad.IntelliJadConstants;
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
 * A memory-based file.
 *
 * @author Steve Chaloner
 */
public class MemoryVirtualFile extends VirtualFile
{
    /**
     * The name of the file.
     */
    private final String name;

    /**
     * The content of the file.
     */
    private final String content;

    /**
     * A flag to indicate if this file represents a directory.
     */
    private final boolean isDirectory;

    /**
     * The children of this file, if the file is a directory.
     */
    private Map<String, MemoryVirtualFile> children = new HashMap<String, MemoryVirtualFile>();

    /**
     * The parent of this file.  If this file is at the root of the file
     * system, it will not have a parent.
     */
    private VirtualFile parent;

    /**
     * Initialises a new instance of this class.
     *
     * @param name the name of the file
     * @param content the content of the file
     */
    public MemoryVirtualFile(String name,
                             String content)
    {
        this(name,
             content,
             false);
    }

    /**
     * Initialises a new instance of this class.
     *
     * @param name the name of the file
     */
    public MemoryVirtualFile(String name)
    {
        this(name,
             null,
             true);
    }

    /**
     * Initialises a new instance of this class.
     *
     * @param name the name of the file
     * @param content the content of the file.  This is mutually exclusive with
     * <code>isDirectory</code>.
     * @param isDirectory true iff this file is a directory.  This is mutually exclusive
     * with <code>content<code>.
     */
    private MemoryVirtualFile(String name,
                              String content,
                              boolean isDirectory)
    {
        this.name = name;
        this.content = content;
        this.isDirectory = isDirectory;
    }

    /** {@javadocInherited} */
    @NotNull
    @NonNls
    public String getName()
    {
        return name;
    }

    /** {@javadocInherited} */
    @NotNull
    public VirtualFileSystem getFileSystem()
    {
        return VirtualFileManager.getInstance().getFileSystem(IntelliJadConstants.INTELLIJAD_PROTOCOL);
    }

    /** {@javadocInherited} */
    public String getPath()
    {
        VirtualFile parent = getParent();
        return (parent == null) ? name : parent.getPath() + '/' + name;
    }

    /** {@javadocInherited} */
    public boolean isWritable()
    {
        return true;
    }

    /** {@javadocInherited} */
    public boolean isDirectory()
    {
        return isDirectory;
    }

    /** {@javadocInherited} */
    public boolean isValid()
    {
        return true;
    }

    /** {@javadocInherited} */
    public void setParent(VirtualFile parent)
    {
        this.parent = parent;
    }

    /** {@javadocInherited} */
    @Nullable
    public VirtualFile getParent()
    {
        return parent;
    }

    /**
     * Add the given file to the child list of this directory.
     *
     * @param file the file to add to the list of children
     * @throws IllegalStateException if this file is not a directory
     */
    public void addChild(MemoryVirtualFile file) throws IllegalStateException
    {
        if (isDirectory)
        {
            file.setParent(this);
            children.put(file.getName(),
                         file);
        }
        else
        {
            throw new IllegalStateException("files can only be added to a directory");
        }
    }

    /** {@javadocInherited} */
    public VirtualFile[] getChildren()
    {
        return children.values().toArray(new VirtualFile[children.size()]);
    }

    /** {@javadocInherited} */
    public OutputStream getOutputStream(Object object,
                                        long l,
                                        long l1) throws IOException
    {
        return new ByteArrayOutputStream();
    }

    /** {@javadocInherited} */
    public byte[] contentsToByteArray() throws IOException
    {
        return content.getBytes();
    }

    /** {@javadocInherited} */
    public long getTimeStamp()
    {
        return 0L;
    }

    /** {@javadocInherited} */
    public long getLength()
    {
        return content.getBytes().length;
    }

    /** {@javadocInherited} */
    public void refresh(boolean b,
                        boolean b1,
                        Runnable runnable)
    {
    }

    /** {@javadocInherited} */
    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(content.getBytes());
    }

    /**
     * Gets the file from this directory's children.
     *
     * @param name the name of the child to retrieve
     * @return the file, or null if it cannot be found
     */
    @Nullable
    public MemoryVirtualFile getChild(String name)
    {
        return children.get(name);
    }

    /** {@javadocInherited} */
    public long getModificationStamp()
    {
        return 0L;
    }
}
