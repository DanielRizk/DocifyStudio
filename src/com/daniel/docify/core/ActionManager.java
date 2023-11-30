package com.daniel.docify.core;

import com.daniel.docify.fileProcessor.FileNodeModel;
import com.daniel.docify.fileProcessor.UserConfiguration;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static com.daniel.docify.fileProcessor.DirectoryProcessor.buildFileTree;
import static com.daniel.docify.fileProcessor.DirectoryProcessor.printFileTree;
import static com.daniel.docify.ui.DocDisplayModelUI.updateDisplayModelUI;
import static com.daniel.docify.ui.TreeModelUI.updateFileTree;

public class ActionManager implements Serializable {
    public static FileNodeModel root = null;
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
        updateDisplayModelUI(null);
    }

    public static void saveDocify() throws IOException{
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String lastSavePath = UserConfiguration.loadUserLastOpenConfig();
        if(lastSavePath != null){
            fileChooser.setCurrentDirectory(new File(lastSavePath));
        }
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            // Get the absolute path
            String absolutePath = selectedFile.getAbsolutePath();

            // Print or use the absolute path as needed
            System.out.println("Selected File/Folder: " + absolutePath);
            UserConfiguration.saveUserLastSaveConfig(absolutePath);
        }
    }

    public static void startCLang() throws IOException{
        JFileChooser fileChooser = new JFileChooser();
        // Optionally, set the file chooser to allow selecting only directories
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String lastOpenPath = UserConfiguration.loadUserLastOpenConfig();
        if(lastOpenPath != null){
            fileChooser.setCurrentDirectory(new File(lastOpenPath));
        }
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            // Get the absolute path
            String absolutePath = selectedFile.getAbsolutePath();

            // Print or use the absolute path as needed
            System.out.println("Selected File/Folder: " + absolutePath);
            UserConfiguration.saveUserLastOpenConfig(absolutePath);
            try {
                File rootDir = new File(absolutePath);
                root = buildFileTree(rootDir, CProject);
                printFileTree(root, 0);
                updateFileTree(root);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}