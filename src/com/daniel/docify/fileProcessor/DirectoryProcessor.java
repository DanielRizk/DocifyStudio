package com.daniel.docify.fileProcessor;

import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.parser.clang.ClangParser;
import com.daniel.docify.ui.Controller;
import javafx.application.Platform;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static com.daniel.docify.ui.Controller.C_PROJECT;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @brief   This class provides all necessary methods to process
 *          projects directories and create Dir-tree structure
 */
public class DirectoryProcessor {

    private final Controller controller;
    private List<String> ignoreList;
    public static volatile boolean watchThreadKeepRunning = true;

    public DirectoryProcessor(Controller controller){
        this.controller = controller;
    }

    private List<String> getIgnoreList(File directory){
        List<String> ignore = new ArrayList<>();
        File[] directoryListing = directory.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.getName().equals("doci.ignore") || child.getName().equals("temp.ignore")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(child))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.replaceAll("/", "\\\\");
                            ignore.add(directory.getAbsolutePath()+line);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return ignore;
    }

    public Controller getController() {
        return controller;
    }

    public double currentFileCount = 0.0;
    public double totalFileCount = 0.0;

    @FunctionalInterface
    public interface DirectoryProcessorCallback {
        void onCompletion(FileNodeModel rootFileNode) throws MalformedURLException;
    }

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
     * This method is the core of the system, it creates the fileNode tree
     * structure and parses each file according to the specified type, and assigns
     * each fileInfo to the respective fileNode
     *
     */
    public FileNodeModel buildDirTree(File directory, String projectType) throws IOException {
        String fullPath = directory.getAbsolutePath();
        FileNodeModel node = new FileNodeModel(directory.getName(), projectType, false, fullPath);

        File[] files = directory.listFiles();
        if (files != null) {
            boolean containsFileType = false;
            for (File file : files) {
                currentFileCount++;
                controller.getProgressBar().setProgress(currentFileCount / totalFileCount);

                // Skip processing if the file/directory is in the ignore list
                if (ignoreList != null && ignoreList.contains(file.getAbsolutePath())) {
                    continue;
                }

                if (file.isDirectory()) {
                    FileNodeModel childNode = buildDirTree(file, projectType);
                    if (childNode != null) {
                        node.addChild(childNode);
                        containsFileType = true; // Set to true if any child directory contains the file type
                    }
                } else {
                    // Process files based on project type
                    if (isRelevantFile(file, projectType)) {
                        FileNodeModel childNode = new FileNodeModel(file.getName(), projectType, true, file.getAbsolutePath());
                        node.addChild(childNode);
                        containsFileType = true;
                    }
                }
            }
            // If the directory or any of its subdirectories contains the file type, return the node
            if (containsFileType) {
                return node;
            }
        }
        return null;
    }

    private boolean isRelevantFile(File file, String projectType) {
        if (Objects.equals(projectType, C_PROJECT)) {
            return file.getName().endsWith(".h") || file.getName().endsWith(".c");
        } else {
            return file.getName().endsWith(projectType);
        }
    }

    public FileNodeModel RebuildDirTree(File directory, String projectType) throws IOException {
        FileNodeModel node = controller.menuActions.getFileFormatModel().getRootNode();
        if (node != null) ignoreList = getIgnoreList(new File(node.getFullPath()));
        return buildDirTree(directory, projectType);
    }

    public void buildAndProcessDirectory(File directory, String projectType, DirectoryProcessorCallback callback) throws IOException {
        controller.getProgressBar().setVisible(true);
        controller.getProgressBar().setProgress(0);
        ignoreList = getIgnoreList(directory);
        watchThreadKeepRunning = true;

        new Thread(() -> {
            try {
                totalFileCount = countFiles(directory) * 2;
                FileNodeModel rootFileNode = buildDirTree(directory, projectType);
                if (rootFileNode != null) {
                    processDirTree(rootFileNode, projectType);
                    DirectoryWatchService.syncOn(rootFileNode, this);
                }

                // Use the callback to return the result
                if (callback != null) {
                    Platform.runLater(() -> {
                        try {
                            callback.onCompletion(rootFileNode);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                        controller.getProgressBar().setVisible(false);
                    });
                }

                totalFileCount = 0.0;
                currentFileCount = 0.0;
            } catch (IOException e) {
                Controller.LOGGER.log(Level.SEVERE, "", e);
            }
        }).start();
    }

    public void processDirTree(FileNodeModel node, String projectType) throws IOException {
        if (node.isFile()) {
            currentFileCount++;
            controller.getProgressBar().setProgress(currentFileCount/totalFileCount);
            if (Objects.equals(projectType, C_PROJECT)){// && node.getName().endsWith(".h")) {
                ClangParser parser = new ClangParser();
                node.setFileInfo(parser.parseFile(node, projectType));
            }
        }
        for (FileNodeModel child : node.getChildren()) {
            processDirTree(child, projectType);
        }
    }

    /**
     * @brief   This method prints the file node structure to the console for debugging
     */
    public void printFileTree(FileNodeModel node, int depth) {

        System.out.println("  ".repeat(Math.max(0, depth)) + (node.isFile() ? "- " : "+ ") + node.getName() + " (" + node.getFullPath() + ")");

        for (FileNodeModel child : node.getChildren()) {
            printFileTree(child, depth + 1);
        }
    }

}