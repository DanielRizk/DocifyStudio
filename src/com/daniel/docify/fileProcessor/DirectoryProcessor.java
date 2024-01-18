package com.daniel.docify.fileProcessor;

import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.parser.clang.ClangParser;
import com.daniel.docify.ui.Controller;
import javafx.application.Platform;
import javafx.scene.control.Alert;

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
    private final WatchService watchService;
    private List<String> ignoreList;

    public DirectoryProcessor(Controller controller) throws IOException {
        this.controller = controller;
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    private List<String> getIgnoreList(File directory){
        List<String> ignore = new ArrayList<>();
        File[] directoryListing = directory.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.getName().equals("doci.ignore")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(child))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            ignore.add(directory.getAbsolutePath()+line);
                        }
                        return ignore;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
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
                registerDirectory(directory.toPath());
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
        ignoreList = getIgnoreList(directory);

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