package com.daniel.docify.core;

import com.daniel.docify.fileProcessor.FileNode;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

import static com.daniel.docify.fileProcessor.FileProcessor.buildFileTree;
import static com.daniel.docify.fileProcessor.FileProcessor.printFileTree;
import static com.daniel.docify.ui.TreeModelUI.updateFileTree;

public class ActionStarter {
    public final static String CProject = ".h";
    public final static String PythonProject = ".py";
    public final static String JavaProject = ".java";

    public static void openDociFile() throws IOException{
        JFileChooser fileChooser = new JFileChooser();

        // Set a file filter to only allow files with the .doci extension
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Docify Files (*.doci)", "doci");
        fileChooser.setFileFilter(filter);

        // Show the file chooser dialog
        int result = fileChooser.showOpenDialog(null);

        // Process the selected file or directory
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file or directory
            File selectedFile = fileChooser.getSelectedFile();

            System.out.println("Selected File/Folder: " + selectedFile.getAbsolutePath());
        }
    }

    public static void closeFile() throws IOException{

        updateFileTree(null);
    }

    public static void startCLang() throws IOException{
        JFileChooser fileChooser = new JFileChooser();
        // Optionally, set the file chooser to allow selecting only directories
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            // Get the absolute path
            String absolutePath = selectedFile.getAbsolutePath();

            // Print or use the absolute path as needed
            System.out.println("Selected File/Folder: " + absolutePath);
            try {
                File rootDir = new File(absolutePath);
                FileNode root = null;


                root = buildFileTree(rootDir, CProject);
                //processNode(root);
                //getNodesFileInfo(root);
                printFileTree(root, 0);
                updateFileTree(root);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
