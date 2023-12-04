package com.daniel.docify.core;

import com.daniel.docify.fileProcessor.FileNodeModel;
import com.daniel.docify.fileProcessor.FileSerializer;
import com.daniel.docify.fileProcessor.UserConfiguration;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static com.daniel.docify.fileProcessor.DirectoryProcessor.*;
import static com.daniel.docify.ui.DocDisplayModelUI.updateDisplayModelUI;
import static com.daniel.docify.ui.TreeModelUI.updateFileTree;

public class ActionManager {
    public static FileNodeModel root = null;
    public final static String CProject = ".h";
    public final static String PythonProject = ".py";
    public final static String JavaProject = ".java";

    public static void openDociFile() throws IOException{
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Set a file filter to only allow files with the .doci extension
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Docify Files (*.doci)", "doci");
        fileChooser.setFileFilter(filter);

        String lastOpenPath = UserConfiguration.loadUserLastOpenConfig();
        if(lastOpenPath != null){
            fileChooser.setCurrentDirectory(new File(lastOpenPath));
        }

        // Show the file chooser dialog
        int result = fileChooser.showOpenDialog(null);

        // Process the selected file or directory
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file or directory
            File selectedFile = fileChooser.getSelectedFile();

            System.out.println("Selected File/Folder: " + selectedFile.getAbsolutePath());
            String absolutePath = selectedFile.getAbsolutePath();
            if (!absolutePath.contains(".")) UserConfiguration.saveUserLastOpenConfig(absolutePath = getParentDir(absolutePath));
            updateFileTree(FileSerializer.load(absolutePath));
        }
    }

    public static void closeFile() throws IOException{

        updateFileTree(null);
        updateDisplayModelUI(null);
    }

    public static void saveDocify() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Docify Files (*.doci)", "doci");
        fileChooser.setFileFilter(filter);

        String lastSavePath = UserConfiguration.loadUserLastOpenConfig();
        if (lastSavePath != null) {
            fileChooser.setCurrentDirectory(new File(lastSavePath));
        }

        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            // Get the absolute path
            String absolutePath = selectedFile.getAbsolutePath();

            // Combine the user-provided file name with the directory path
            String filePath = absolutePath + ".doci";

            // Print or use the absolute path as needed
            System.out.println("Selected File/Folder: " + filePath);

            // Save the FileNodeModel using the user-provided file name
            FileSerializer.save(root, filePath);

            // Save the last save path to user configuration
            if (!absolutePath.contains(".")) UserConfiguration.saveUserLastSaveConfig(absolutePath);
            else{
                absolutePath = getParentDir(absolutePath);
                UserConfiguration.saveUserLastSaveConfig(absolutePath);
            }

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
            if (!absolutePath.contains(".")) UserConfiguration.saveUserLastOpenConfig(absolutePath = getParentDir(absolutePath));

            try {
                File rootDir = new File(absolutePath);
                root = buildDirTree(rootDir, CProject);
                assert root != null;
                printFileTree(root, 0);
                updateFileTree(root);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static String getParentDir(String path){
        int lastIndex = path.lastIndexOf("\\"); // Use "\\" for backslash in a string

        if (lastIndex != -1) {
            // Extract the substring excluding the last directory
            String newPath = path.substring(0, lastIndex);
            return newPath;
        } else {
            return null;
        }
    }
}
