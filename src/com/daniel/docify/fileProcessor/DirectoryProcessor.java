package com.daniel.docify.fileProcessor;

import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.parser.clang.ClangParser;
import com.daniel.docify.ui.Controller;
import javafx.application.Platform;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Objects;
import java.util.logging.Level;

import static com.daniel.docify.ui.Controller.CProject;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @brief   This class provides all necessary methods to process
 *          projects directories and create Dir-tree structure
 */
public class DirectoryProcessor {

    private final Controller controller;
    private final WatchService watchService;

    public DirectoryProcessor(Controller controller) throws IOException {
        this.controller = controller;
        this.watchService = FileSystems.getDefault().newWatchService();
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
                controller.getProgressBar().setProgress(currentFileCount/totalFileCount);
                if (file.isDirectory()) {
                    FileNodeModel childNode = buildDirTree(file, projectType);
                    if (childNode != null) {
                        node.addChild(childNode);
                        containsFileType = true; // Set to true if any child directory contains the file type
                    }
                } else {
                    if (Objects.equals(projectType, CProject)){
                        if (file.getName().endsWith(".h") || file.getName().endsWith(".c")) {
                            FileNodeModel childNode = new FileNodeModel(file.getName(), CProject,true, file.getAbsolutePath());
                            node.addChild(childNode);
                            containsFileType = true;
                        }
                    } else {
                        if (file.getName().endsWith(projectType)) {
                            FileNodeModel childNode = new FileNodeModel(file.getName(), projectType, true, file.getAbsolutePath());
                            node.addChild(childNode);
                            containsFileType = true;
                        }
                    }
                }
            }
            // If the directory or any of its subdirectories contains the file type, return the node
            if (containsFileType) {
                registerDirectory(directory.toPath());
                return node;
            } else {
                return null;
            }
        }
        return null;
    }

    private void registerDirectory(Path dir) throws IOException {
        dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    public void syncOn(FileNodeModel rootNode) {
        new Thread(() -> {
            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    break;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
                    Path dir = (Path) key.watchable();
                    Path fullPath = dir.resolve(filename);

                    boolean isFile = Files.isRegularFile(fullPath);
                    Platform.runLater(() -> {
                        rootNode.updateNode(fullPath, isFile, kind, this, rootNode.getProjectType());
                    });
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }).start();
    }

    public void buildAndProcessDirectory(File directory, String projectType, DirectoryProcessorCallback callback) throws IOException {
        controller.getProgressBar().setVisible(true);
        controller.getProgressBar().setProgress(0);

        new Thread(() -> {
            try {
                totalFileCount = countFiles(directory) * 2;
                FileNodeModel rootFileNode = buildDirTree(directory, projectType);
                if (rootFileNode != null) {
                    processDirTree(rootFileNode, projectType);
                    syncOn(rootFileNode);
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
            if (Objects.equals(projectType, CProject)){// && node.getName().endsWith(".h")) {
                node.setFileInfo(ClangParser.parseFile(node));
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