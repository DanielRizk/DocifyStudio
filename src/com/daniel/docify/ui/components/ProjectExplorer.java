package com.daniel.docify.ui.components;

import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.ui.Controller;
import com.daniel.docify.ui.utils.ControllerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

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

        Image icon = getIconForNode(fileNode);
        if (icon != null) {
            ImageView iconView = new ImageView(icon);
            iconView.setFitHeight(20.0); // Set the height as per your requirement
            iconView.setFitWidth(20.0);  // Set the width as per your requirement
            treeItem.setGraphic(iconView);
        }

        for (FileNodeModel child : fileNode.getChildren()) {
            treeItem.getChildren().add(convertToTreeItem(child));
        }

        return treeItem;
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

    /**
     * This method generates a list of FileNodeModel from the current RootNode and adds it to the project nodes
     * list container.
     */
    private void generateExplorerListview(FileNodeModel fileNode){
        if (fileNode.isFile()) {
            projectNodesList.add(fileNode);
        }
        for (FileNodeModel child : fileNode.getChildren()) {
            generateExplorerListview(child);
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
                return new ProjectExplorer.ExplorerItemCell();
            }
        });

    }

    /**
     * This subclass assists the cell factory of the explorerListView, and it contains the
     * logic behind styling the cells.
     */
    public static class ExplorerItemCell extends ListCell<FileNodeModel> {
        private final ImageView imageView = new ImageView();
        private final Text text = new Text();

        private final HBox cellBox = new HBox(imageView, text);

        @Override
        protected void updateItem(FileNodeModel node, boolean empty) {
            super.updateItem(node, empty);

            if (empty || node == null) {
                text.setText(null);
                imageView.setImage(null);
                setGraphic(null);
            } else {
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

}
