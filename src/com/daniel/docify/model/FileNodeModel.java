package com.daniel.docify.model;

import com.daniel.docify.fileProcessor.DirectoryProcessor;
import com.daniel.docify.fileProcessor.FileSerializer;
import com.daniel.docify.ui.Controller;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * This class represents a node in a directory structure hierarchy,
 * also provides useful method to create a node-tree structure
 */
public class FileNodeModel extends FileSerializer implements Serializable {
    private final String name;
    private final String projectType;
    private final boolean isFile;
    private final String fullPath;
    private final List<FileNodeModel> children;
    private FileInfoModel fileInfo;

    public FileNodeModel(String name,String projectType ,boolean isFile, String fullPath) {
        this.name = name;
        this.projectType = projectType;
        this.isFile = isFile;
        this.fullPath = fullPath;
        this.children = new ArrayList<>();
    }

    public void updateNode(Path path, boolean isFile, WatchEvent.Kind<?> kind, DirectoryProcessor directoryProcessor, String projectType) {
        // Check if the event is for file modification
        if (kind == ENTRY_MODIFY) {
            for (int i = 0; i < children.size(); i++) {
                FileNodeModel child = children.get(i);
                if (child.getFullPath().equals(path.toString())) {
                    try {
                        // Rebuild the node for the modified file/directory
                        FileNodeModel updatedNode = isFile ?
                                new FileNodeModel(path.getFileName().toString(), projectType, true, path.toString()) :
                                directoryProcessor.buildDirTree(path.toFile(), projectType);

                        if (updatedNode != null) {
                            directoryProcessor.processDirTree(updatedNode, projectType);
                            children.set(i, updatedNode); // Replace the old node with the updated one
                        }

                        // UI update logic
                        Platform.runLater(() -> {
                            Controller controller = directoryProcessor.getController();
                            try {
                                controller.explorer.updateTreeView(controller.menuActions.getFileFormatModel().getRootNode());
                                controller.mainWindow.refreshWebViewDisplay();
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        break; // Exit the loop as the relevant node has been found and updated
                    } catch (IOException e) {
                        e.printStackTrace(); // Or handle the exception as appropriate
                    }
                }else {
                    if (!child.isFile()){
                        Path childPath = Path.of(child.getFullPath());
                        updateNode(childPath, child.isFile, kind, directoryProcessor, projectType);
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getProjectType() {
        return projectType;
    }

    public boolean isFile() {
        return isFile;
    }

    public String getFullPath() {
        return fullPath;
    }

    public List<FileNodeModel> getChildren() {
        return children;
    }

    public void addChild(FileNodeModel child) {
        children.add(child);
    }

    public void setFileInfo (FileInfoModel fileInfo){
        this.fileInfo = fileInfo;
    }

    public FileInfoModel getFileInfo () {
        return this.fileInfo;
    }

    @Override
    public String toString() {
        return name;
    }
}
