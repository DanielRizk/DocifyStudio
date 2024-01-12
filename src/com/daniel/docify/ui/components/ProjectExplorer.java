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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

/**
 * This class represents the left pane of the UI, and it contains the TreeView structure
 * and the ListView of all the file within the project.
 */
public class ProjectExplorer extends ControllerUtils{


    private final ObservableList<FileNodeModel> projectNodesList = FXCollections.observableArrayList();

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
        if (fileNode.isFile()){
            if (fileNode.getName().endsWith(Controller.CProject)) {
                treeItem.setGraphic(setIcon("assets/icons/c_header.png")); // path to your file icon
            } else if (fileNode.getName().endsWith(".c") || fileNode.getName().endsWith(".cpp")){
                treeItem.setGraphic(setIcon("assets/icons/c_src.png")); // path to your folder icon
            } else if (fileNode.getName().endsWith(Controller.JavaProject)) {
                treeItem.setGraphic(setIcon("assets/icons/java_file.png")); // path to your folder icon
            } else if (fileNode.getName().endsWith(Controller.PythonProject)) {
                treeItem.setGraphic(setIcon("assets/icons/py_file.png")); // path to your folder icon
            }
        }
        else{
            treeItem.setGraphic(setIcon("assets/icons/open.png"));
        }
        for(FileNodeModel child : fileNode.getChildren()){
            treeItem.getChildren().add(convertToTreeItem(child));
        }
        return treeItem;
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

        @Override
        protected void updateItem(FileNodeModel node, boolean empty) {
            super.updateItem(node, empty);

            if (empty || node == null) {
                text.setText(null);
                imageView.setImage(null);
                setGraphic(null);
            } else {
                text.setText(node.getName());

                try {
                    if (node.getName().endsWith(Controller.CProject)) {
                        imageView.setImage(setIconForList("assets/icons/c_header.png"));
                        text.setFill(Color.web("#28725f"));
                    } else if (node.getName().endsWith(".c") || node.getName().endsWith(".cpp")){
                        imageView.setImage(setIconForList("assets/icons/c_src.png"));
                        text.setFill(Color.web("#4b245b"));
                    } else if (node.getName().endsWith(Controller.JavaProject)) {
                        imageView.setImage(setIconForList("assets/icons/java_file.png"));
                        text.setFill(Color.web("#824d00"));
                    } else if (node.getName().endsWith(Controller.PythonProject)) {
                        imageView.setImage(setIconForList("assets/icons/py_file.png"));
                        text.setFill(Color.web("#62664d"));
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                imageView.setFitHeight(30.0);
                imageView.setFitWidth(30.0);
                HBox cellBox = new HBox(imageView, text);
                cellBox.setSpacing(10);
                setGraphic(cellBox);
            }
        }

        /**
         * This method is a helper method used to load an icon and returns
         * an Image object.
         */
        private Image setIconForList(String iconPath) throws FileNotFoundException {
            FileInputStream input = new FileInputStream(iconPath);
            return new Image(input);
        }
    }
}
