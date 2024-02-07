package com.daniel.docify.ui.components;

import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.ui.Controller;
import com.daniel.docify.ui.utils.ControllerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 * This class represents the left pane of the UI, and it contains the TreeView structure
 * and the ListView of all the file within the project.
 */
public class ProjectExplorer extends ControllerUtils{


    private final ObservableList<FileNodeModel> projectNodesList = FXCollections.observableArrayList();
    private static final Map<String, Image> iconCache = new HashMap<>();

    private static final String ICON_HEADER        = "assets/icons/c_header.png";
    private static final String ICON_SOURCE        = "assets/icons/c_src.png";
    private static final String ICON_JAVA          = "assets/icons/java_file.png";
    private static final String ICON_PYTHON        = "assets/icons/py_file.png";
    private static final String ICON_FOLDER        = "assets/icons/open.png";

    static {
        // Pre-load icons
        loadIcon(ICON_HEADER);
        loadIcon(ICON_SOURCE);
        loadIcon(ICON_JAVA);
        loadIcon(ICON_PYTHON);
        loadIcon(ICON_FOLDER);
        // Add more icons as needed
    }

    private static void loadIcon(String path) {
        try {
            iconCache.put(path, new Image(new FileInputStream(path)));
        } catch (FileNotFoundException e) {
            System.err.println("Icon file not found: " + path);
        }
    }

    public ProjectExplorer(Controller controller){
        super(controller);
    }

    /* getter methods for private variables */
    public ObservableList<FileNodeModel> getProjectNodesList(){
        return projectNodesList;
    }

    /**
     * This method populates and updates the TreeView of the UI with the current {@link FileNodeModel} object selected,
     * and it calls the methods to update the ListView.
     */
    public void updateTreeView(FileNodeModel rootFileNode) throws MalformedURLException {
        TreeItem<FileNodeModel> treeItem;
        if (rootFileNode == null){
            treeItem = new TreeItem<>();
        }
        else {
            treeItem = convertToTreeItem(rootFileNode);
        }
        controller.getExplorerTreeView().setCellFactory(tv -> new FileNodeTreeCell(controller));
        controller.getExplorerTreeView().setRoot(treeItem);
        assert rootFileNode != null;
        projectNodesList.clear();
        generateExplorerListview(rootFileNode);
        updateExplorerListView();
    }

    /**
     * This method converts FileNodeModel instances to TreeItem model
     * to be compatible with TreeView model, and it is also responsible for
     * assigning icon to the directories and files in the tree.
     */
    private TreeItem<FileNodeModel> convertToTreeItem(FileNodeModel fileNode) throws MalformedURLException {
        TreeItem<FileNodeModel> treeItem = new TreeItem<>(fileNode);
        treeItem.setExpanded(true);

        // Get the children of the current node
        List<FileNodeModel> children = fileNode.getChildren();

        // Sort the children: directories first, then files
        children.sort((o1, o2) -> {
            // Assuming isFile is a boolean where false indicates a directory
            if (o1.isFile() && !o2.isFile()) {
                return 1; // o1 is a file and o2 is a directory, o1 should come after o2
            } else if (!o1.isFile() && o2.isFile()) {
                return -1; // o1 is a directory and o2 is a file, o1 should come before o2
            }
            // If both are files or both are directories, sort alphabetically or by any other criteria you prefer
            return o1.getName().compareToIgnoreCase(o2.getName());
        });

        // Recursively add sorted children to the tree
        for (FileNodeModel child : children) {
            treeItem.getChildren().add(convertToTreeItem(child));
        }

        return treeItem;
    }



    /**
     * This method generates a list of FileNodeModel from the current RootNode and adds it to the project nodes
     * list container.
     */
    private void generateExplorerListview(FileNodeModel fileNode){
        if (fileNode != null && fileNode.isFile()) {
            projectNodesList.add(fileNode);
        }
        if (fileNode != null &&fileNode.getChildren() != null) {
            for (FileNodeModel child : fileNode.getChildren()) {
                generateExplorerListview(child);
            }
        }
    }

    /**
     * This method generates a filtered list of the projectNodesList
     * based on user inputs and updates the ListView in the UI.
     */
    public ObservableList<FileNodeModel> updateExplorerListView(){
        controller.getExplorerListView().getItems().clear();
        ObservableList<FileNodeModel> filteredItems = FXCollections.observableArrayList();
        filteredItems.clear();
        for(FileNodeModel fileNode : projectNodesList){
            if (fileNode.getFileInfo() != null || !controller.getDocumentedFilesCheckbox().isSelected()){
                filteredItems.add(fileNode);
            }
        }
        controller.getExplorerListView().getItems().clear();
        controller.getExplorerListView().getItems().addAll(filteredItems);
        return filteredItems;
    }

    /**
     * This method initializes the explorerListView and assigns cell factory
     * in order to stylize the text and add an icon to each entry according to
     * certain rules.
     */
    public void initializeExplorerListView(){
        controller.getExplorerListView().setCellFactory(new Callback<ListView<FileNodeModel>, ListCell<FileNodeModel>>() {
            @Override
            public ListCell<FileNodeModel> call(ListView<FileNodeModel> listView) {
                return new ProjectExplorer.ExplorerItemCell(controller);
            }
        });

    }

    /**
     * This subclass assists the cell factory of the explorerListView, and it contains the
     * logic behind styling the cells.
     */
    public static class ExplorerItemCell extends ListCell<FileNodeModel> {
        private final ImageView imageView = new ImageView();
        private final Tooltip tooltip = new Tooltip();
        private final Text text = new Text();
        private final ContextMenu contextMenu = new ContextMenu();
        private final HBox cellBox = new HBox(imageView, text);

        Controller controller;
        public ExplorerItemCell(Controller controller) {
            MenuItem remove = new MenuItem("Remove from project");
            remove.setOnAction(event -> {
                try {
                    performRemove(getItem());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            });
            contextMenu.getItems().addAll(remove);
            this.controller = controller;
        }

        @Override
        protected void updateItem(FileNodeModel node, boolean empty) {
            super.updateItem(node, empty);

            if (empty || node == null) {
                text.setText(null);
                imageView.setImage(null);
                setGraphic(null);
                setTooltip(null);
            } else {
                tooltip.setShowDelay(new Duration(1000));
                tooltip.activatedProperty();
                if (node.isFile()) {
                    if(node.getFileInfo() != null) tooltip.setText("File has a Documentation\n"+node.getFullPath());
                    else tooltip.setText("File has no Documentation\n"+node.getFullPath());
                }else{
                    tooltip.setText(node.getFullPath());
                }

                setContextMenu(contextMenu);

                setTooltip(tooltip);
                text.setText(node.getName());
                Image icon = getIconForNode(node);
                if (icon != null) {
                    imageView.setImage(icon);
                }

                imageView.setFitHeight(30.0);
                imageView.setFitWidth(30.0);
                cellBox.setSpacing(10);
                setGraphic(cellBox);
            }
        }
        private void performRemove(FileNodeModel item) throws MalformedURLException {
            controller.menuActions.getFileFormatModel().getRootNode().removeChild(item.getFullPath());
            controller.menuActions.refreshProject();
        }

        private Image getIconForNode(FileNodeModel node) {
            if (node.getName().endsWith(Controller.C_PROJECT)) {
                return iconCache.get(ICON_HEADER);
            } else if (node.getName().endsWith(".c") || node.getName().endsWith(".cpp")) {
                return iconCache.get(ICON_SOURCE);
            } else if (node.getName().endsWith(Controller.JAVA_PROJECT)) {
                return iconCache.get(ICON_JAVA);
            } else if (node.getName().endsWith(Controller.PYTHON_PROJECT)) {
                return iconCache.get(ICON_PYTHON);
            }
            return null; // Default icon or null if none matches
        }
    }

    public static class FileNodeTreeCell extends TreeCell<FileNodeModel> {
        private final Tooltip tooltip = new Tooltip();
        private final ContextMenu contextMenu = new ContextMenu();

        Controller controller;
        public FileNodeTreeCell(Controller controller) {
            MenuItem remove = new MenuItem("Remove from project");
            remove.setOnAction(event -> {
                try {
                    performRemove(getItem());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            contextMenu.getItems().addAll(remove);
            this.controller = controller;
        }

        @Override
        protected void updateItem(FileNodeModel item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                setTooltip(null);
            } else {
                setText(item.getName()); // Set the text of the cell
                tooltip.setShowDelay(new Duration(1000));
                tooltip.activatedProperty();
                if (item.isFile()) {
                    if(item.getFileInfo() != null) tooltip.setText("File has a Documentation\n"+item.getFullPath());
                    else tooltip.setText("File has no Documentation\n"+item.getFullPath());
                }else{
                    tooltip.setText(item.getFullPath());
                }
                setTooltip(tooltip);

                Image icon = getIconForNode(item);
                if (icon != null) {
                    ImageView iconView = new ImageView(icon);
                    iconView.setFitHeight(20.0);
                    iconView.setFitWidth(20.0);
                    setGraphic(iconView);
                } else {
                    setGraphic(null);
                }
                setContextMenu(contextMenu);
            }
        }

        private void performRemove(FileNodeModel item) throws IOException {
            controller.menuActions.getFileFormatModel().getRootNode().removeChild(item.getFullPath());
            controller.menuActions.refreshProject();
            String rootPath = controller.menuActions.getFileFormatModel().getRootNode().getFullPath();
            File file = new File(rootPath + "\\temp.ignore");
            if (!file.exists()){
                try {
                    if (file.createNewFile()){
                        System.out.println("temp.ignore created.");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try (FileWriter writer = new FileWriter(file, true)){
                String relativePath = item.getFullPath().substring(item.getFullPath().indexOf(rootPath) + rootPath.length());
                writer.append(relativePath).append("\n");
            }
        }

        private Image getIconForNode(FileNodeModel fileNode) {
            if (fileNode.isFile()) {
                if (fileNode.getName().endsWith(Controller.C_PROJECT)) {
                    return iconCache.get(ICON_HEADER);
                } else if (fileNode.getName().endsWith(".c") || fileNode.getName().endsWith(".cpp")) {
                    return iconCache.get(ICON_SOURCE);
                } else if (fileNode.getName().endsWith(Controller.JAVA_PROJECT)) {
                    return iconCache.get(ICON_JAVA);
                } else if (fileNode.getName().endsWith(Controller.PYTHON_PROJECT)) {
                    return iconCache.get(ICON_PYTHON);
                }
            } else {
                return iconCache.get(ICON_FOLDER);
            }
            return null; // Default icon or null if none matches
        }
    }
}
