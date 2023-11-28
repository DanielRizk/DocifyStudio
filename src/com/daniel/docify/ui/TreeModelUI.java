package com.daniel.docify.ui;

import com.daniel.docify.fileProcessor.FileNodeModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TreeModelUI extends JFrame implements TreeSelectionListener {

    private static DefaultTreeModel treeModel;

    TreeModelUI(@NotNull MainWindow mainWindow){
        // Create an empty root node for the initial state
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(rootNode);
        JTree fileTree = new JTree(treeModel);

        fileTree.addTreeSelectionListener(this);

        // Set up a scroll pane for the tree
        JScrollPane scrollPane = new JScrollPane(fileTree);
        updateScrollPaneWidth(scrollPane, mainWindow.getWidth()); // Set initial width
        mainWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateScrollPaneWidth(scrollPane, mainWindow.getWidth());
            }
        });
        mainWindow.add(scrollPane,BorderLayout.WEST);
    }

    private void updateScrollPaneWidth(JScrollPane scrollPane, int mainWindowWidth) {
        // Set the preferred size of the scroll pane based on the MainWindow width
        scrollPane.setPreferredSize(new Dimension(mainWindowWidth / 6, 200));
        scrollPane.revalidate();
    }
    // Method to update the file tree when a new root node is created
    public static void updateFileTree(FileNodeModel rootFileNode) {
        DefaultMutableTreeNode rootNode;
       if (rootFileNode == null){
           rootNode = new DefaultMutableTreeNode("Root");
       }
       else {
           rootNode = convertToFileTreeNode(rootFileNode);
       }
        treeModel.setRoot(rootNode);
        //expandAllNodes(fileTree, 0, fileTree.getRowCount());
    }

    private static DefaultMutableTreeNode convertToFileTreeNode(FileNodeModel fileNode) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileNode);

        for (FileNodeModel child : fileNode.getChildren()) {
            node.add(convertToFileTreeNode(child));
        }

        return node;
    }

    // Helper method to expand all nodes in the tree
    private static void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree tree = (JTree) e.getSource();
        TreePath path = e.getNewLeadSelectionPath();

        if (path != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object userObject = selectedNode.getUserObject();

            if (userObject instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) userObject;
                // Perform action based on the selected child node
                System.out.println("Selected child node: " + childNode.getUserObject());
            } else if (userObject instanceof FileNodeModel) {
                FileNodeModel selectedFileNode = (FileNodeModel) userObject;
                if (selectedFileNode.isFile()) {
                    // Perform action based on the selected file node
                    System.out.println("Selected file: " + selectedFileNode.getFullPath());
                    DocDisplayModelUI.updateDisplayModelUI(selectedFileNode.getFileInfo());

                }
            }
        }
    }

}
