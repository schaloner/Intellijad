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
package net.stevechaloner.intellijad.actions;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.stevechaloner.intellijad.IntelliJad;
import net.stevechaloner.intellijad.IntelliJadConstants;
import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import net.stevechaloner.intellijad.gui.tree.CheckBoxTree;
import net.stevechaloner.intellijad.gui.tree.CheckBoxTreeNode;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFile;
import net.stevechaloner.intellijad.vfs.MemoryVirtualFileSystem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A basic management GUI for the memory file system, allowing users to see and delete files.
 */
public class MemoryFileSystemManager
{
    /**
     * The content pane.
     */
    private JPanel contentPane;
    private JTree fsTree;
    private JButton expandButton;
    private JButton collapseButton;
    private JButton deleteButton;

    /**
     * Initialises a new instance of this
     * @param project the associated project, if any
     */
    public MemoryFileSystemManager(@Nullable final Project project)
    {
        expandButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Object o = fsTree.getLastSelectedPathComponent();
                expand(o == null ? (DefaultMutableTreeNode)fsTree.getModel().getRoot() : (DefaultMutableTreeNode)o);
            }
        });
        collapseButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Object o = fsTree.getLastSelectedPathComponent();
                collapse(o == null ? (DefaultMutableTreeNode)fsTree.getModel().getRoot() : (DefaultMutableTreeNode)o);
            }
        });
        deleteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                delete(project);
            }
        });
    }

    /**
     * Pending user confirmation, delete selected files from the memory file system.  All associated editors
     * are closed in the process.
     *
     * @param project the project
     */
    private void delete(@Nullable Project project)
    {
        List<MemoryVirtualFile> files = getSelectedFiles();
        if (files.size() > 0)
        {
            int option = JOptionPane.showConfirmDialog(contentPane,
                                                       IntelliJadResourceBundle.message("message.confirm-delete-memory"),
                                                       IntelliJadResourceBundle.message("message.confirm-delete"),
                                                       JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION)
            {
                try
                {
                    MemoryVirtualFileSystem vfs = (MemoryVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(IntelliJadConstants.INTELLIJAD_PROTOCOL);
                    if (project != null)
                    {
                        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                        for (MemoryVirtualFile file : files)
                        {
                            fileEditorManager.closeFile(file);
                            vfs.deleteFile(this,
                                           file);
                        }
                    }
                    else
                    {
                        for (MemoryVirtualFile file : files)
                        {
                            vfs.deleteFile(this,
                                           file);
                        }
                    }
                    rebuildTreeModel();
                }
                catch (IOException ioe)
                {
                    IntelliJad.getLogger().error(ioe);
                }
            }
        }
    }

    /**
     * Get the selected files from the tree.  They will be ordered deepest-first, to ensure
     * files lower down in the system are deleted before their ancestors.
     *
     * @return a list of selected files
     */
    @NotNull
    private List<MemoryVirtualFile> getSelectedFiles()
    {
        List<MemoryVirtualFile> files = new ArrayList<MemoryVirtualFile>();

        DefaultMutableTreeNode root = (DefaultMutableTreeNode)fsTree.getModel().getRoot();
        int count = root.getChildCount();
        for (int i = 0; i < count; i++)
        {
            getSelectedFiles((DefaultMutableTreeNode)root.getChildAt(i),
                             files);
        }

        Collections.sort(files,
                         new Comparator<MemoryVirtualFile>() {
                             public int compare(MemoryVirtualFile o1,
                                                MemoryVirtualFile o2)
                             {
                                 return o1.getPath().compareTo(o2.getPath());
                             }
                         });
        Collections.reverse(files);
        return files;
    }

    /**
     * Recursively gets all selected files from the given node.
     *
     * @param node the root node
     * @param files the list of files
     */
    private void getSelectedFiles(DefaultMutableTreeNode node,
                                  List<MemoryVirtualFile> files)
    {
        CheckBoxTreeNode cbtn = (CheckBoxTreeNode)node.getUserObject();
        if (cbtn.isSelected())
        {
            files.add((MemoryVirtualFile)cbtn.getUserObject());
        }
        int count = node.getChildCount();
        for (int i = 0; i < count; i++)
        {
            getSelectedFiles((DefaultMutableTreeNode)node.getChildAt(i),
                             files);
        }
    }

    /**
     * Fully expand the given tree node.
     *
     * @param node the node to expand
     */
    private void expand(DefaultMutableTreeNode node)
    {
        if (node.isLeaf() && node.getParent() != null)
        {
            fsTree.expandPath(new TreePath(((DefaultMutableTreeNode)node.getParent()).getPath()));
        }
        else
        {
            int count = node.getChildCount();
            for (int i = 0; i < count; i++)
            {
                expand((DefaultMutableTreeNode)node.getChildAt(i));
            }
        }
    }

    /**
     * Fully collapses the given tree node.
     *
     * @param node the node to collapse
     */
    public void collapse(DefaultMutableTreeNode node)
    {
        if (node.isLeaf() && node.getParent() != null)
        {
            fsTree.collapsePath(new TreePath(((DefaultMutableTreeNode)node.getParent()).getPath()));
        }
        else
        {
            int count = node.getChildCount();
            for (int i = 0; i < count; i++)
            {
                collapse((DefaultMutableTreeNode)node.getChildAt(i));
            }
        }
    }

    /**
     * Recursively populates the tree representing the memory file system.
     *
     * @param node the current node
     */
    private void populateChildren(DefaultMutableTreeNode node)
    {
        CheckBoxTreeNode cbtn = (CheckBoxTreeNode)node.getUserObject();
        MemoryVirtualFile file = (MemoryVirtualFile)cbtn.getUserObject();
        for (VirtualFile childFile : file.getChildren())
        {
            CheckBoxTreeNode childPayload = new CheckBoxTreeNode(childFile);
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(childPayload);
            node.add(child);
            populateChildren(child);
        }
    }

    /**
     * Gets the content pane containing the manager GUI.
     *
     * @return the content pane
     */
    public JPanel getContentPane()
    {
        return contentPane;
    }

    private void createUIComponents()
    {
        rebuildTreeModel();
    }

    /**
     * (Re)builds the tree model based on the memory file system.
     */
    private void rebuildTreeModel()
    {
        MemoryVirtualFileSystem vfs = (MemoryVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(IntelliJadConstants.INTELLIJAD_PROTOCOL);
        MemoryVirtualFile rootFile = (MemoryVirtualFile) vfs.findFileByPath(IntelliJadConstants.INTELLIJAD_ROOT);

        CheckBoxTreeNode rootPayload = new CheckBoxTreeNode(rootFile);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootPayload);
        populateChildren(root);
        DefaultTreeModel model = new DefaultTreeModel(root);

        if (fsTree == null)
        {
            fsTree = new CheckBoxTree(model);
        }
        else
        {
            fsTree.setModel(model);
        }
    }
}
