package com.daniel.docify.fileProcessor;

import com.daniel.docify.model2.FileNodeModel;
import com.daniel.docify.parser.clang.ClangParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.daniel.docify.ui.Controller.CProject;

/**
 * @brief   This class provides all necessary methods to process
 *          projects directories and create Dir-tree structure
 */
public class DirectoryProcessor {
    public double countFiles(File directory) {
        double filesCount = 0;

        if (directory.isDirectory()) {
            // List all files and directories in the directory
            File[] files = directory.listFiles();

            // Check if files is not null (i.e., directory is not empty)
            if (files != null) {
                for (File file : files) {
                    // Increment the count for the current file or directory
                    filesCount++;
                    // If it's a directory, recursively count its contents
                    if (file.isDirectory()) {
                        filesCount += countFiles(file);
                    }
                }
            }
        }
        return filesCount;
    }

    /**
     * @brief   This method is the core of the system, it creates the fileNode tree
     *          structure and parses each file according to the specified type, and assigns
     *          each fileInfo to the respective fileNode
     *
     * @return {@link com.daniel.docify.model2.FileNodeModel}
     */
    public static FileNodeModel buildDirTree(File directory, String projectType) throws IOException {
        //double currentFilesCount = 0;
        String fullPath = directory.getAbsolutePath();
        FileNodeModel node = new FileNodeModel(directory.getName(), false, fullPath);

        File[] files = directory.listFiles();
        if (files != null) {
            //currentFilesCount += (double) files.length;
            boolean containsFileType = false;
            for (File file : files) {
                if (file.isDirectory()) {
                    FileNodeModel childNode = buildDirTree(file, projectType);
                    if (childNode != null) {
                        node.addChild(childNode);
                        containsFileType = true; // Set to true if any child directory contains the file type
                    }
                } else {
                    if (Objects.equals(projectType, CProject)){
                        if (file.getName().endsWith(".h")) {
                            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                            FileNodeModel childNode = new FileNodeModel(file.getName(), true, file.getAbsolutePath());
                            childNode.setFileInfo(ClangParser.parseFile(reader, file.getName()));
                            node.addChild(childNode);
                            containsFileType = true; // Set to true if any file in the directory has the specified type
                            /*
                             * you can switch to parse src files as well by removing the else if part
                             * and adding the condition to the "if" part by ||
                             */
                        } else if (file.getName().endsWith(".c")) {
                            FileNodeModel childNode = new FileNodeModel(file.getName(), true, file.getAbsolutePath());
                            node.addChild(childNode);
                            containsFileType = true; // Set to true if any file in the directory has the specified type
                        }
                    }
                    else {
                        if (file.getName().endsWith(projectType)) {
                            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                            FileNodeModel childNode = new FileNodeModel(file.getName(), true, file.getAbsolutePath());
                            childNode.setFileInfo(ClangParser.parseFile(reader, file.getName()));
                            node.addChild(childNode);
                            containsFileType = true; // Set to true if any file in the directory has the specified type
                        }
                    }
                }
            }

            // If the directory or any of its subdirectories contains the file type, return the node
            if (containsFileType) {
                return node;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * @brief   This method prints the file node structure to the console for debugging
     */
    public static void printFileTree(FileNodeModel node, int depth) {

        System.out.println("  ".repeat(Math.max(0, depth)) + (node.isFile() ? "- " : "+ ") + node.getName() + " (" + node.getFullPath() + ")");

        for (FileNodeModel child : node.getChildren()) {
            printFileTree(child, depth + 1);
        }
    }

    public static List<FileNodeModel> getNodesFileInfo(FileNodeModel node) {
        List<FileNodeModel> fileNodes = new ArrayList<>();
        if (node.isFile() && node.getFileInfo() != null) {
            fileNodes.add(node);
        }
        for (FileNodeModel child : node.getChildren()) {
            getNodesFileInfo(child);
        }
        return fileNodes;
    }
}
