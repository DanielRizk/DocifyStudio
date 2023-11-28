package com.daniel.docify.ui;

import com.daniel.docify.fileProcessor.FileNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TreeModelUI extends JFrame implements ActionListener {

    private static DefaultTreeModel treeModel;

    TreeModelUI(MainWindow mainWindow){
        // Create an empty root node for the initial state
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(rootNode);
        JTree fileTree = new JTree(treeModel);

        // Set up a scroll pane for the tree
        JScrollPane scrollPane = new JScrollPane(fileTree);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        mainWindow.add(scrollPane,BorderLayout.WEST);
    }

    // Method to update the file tree when a new root node is created
    public static void updateFileTree(FileNode rootFileNode) {
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

    private static DefaultMutableTreeNode convertToFileTreeNode(FileNode fileNode) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileNode.getName());

        for (FileNode child : fileNode.getChildren()) {
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
    public void actionPerformed(ActionEvent e) {
        // Call the method that creates the root node and updates the tree
        //FileNode rootFileNode = fileProcessor.buildFileTree(); // Change this based on your actual method
        //updateFileTree(rootFileNode);
    }

}
