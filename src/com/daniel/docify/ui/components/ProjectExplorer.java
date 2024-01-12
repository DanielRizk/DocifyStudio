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

public class ProjectExplorer extends ControllerUtils{


    public final ObservableList<FileNodeModel> projectNodesList = FXCollections.observableArrayList();

    public ProjectExplorer(Controller controller){
        super(controller);
    }

    /**
     * @brief   This method populates and updates the Tree view, and calls
     *          the generateListView method
     */
    public void updateTreeView(FileNodeModel rootFileNode) throws MalformedURLException {
        TreeItem<FileNodeModel> treeItem;
        if (rootFileNode == null){
            treeItem = new TreeItem<>();
        }
        else {
            treeItem = convertToTreeItem(rootFileNode);
        }
        controller.explorerTreeView.setRoot(treeItem);
        assert rootFileNode != null;
        projectNodesList.clear();
        generateExplorerListview(rootFileNode);
        updateExplorerListView();
    }

    /**
     * @brief   This method converts FileNodeModel instances to TreeItem model
     *          to be compatible with TreeView model
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
     * This method generates a list of FileNodeModel
     * from the root node and adds it to the global
     * list container
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
     * @brief   This method sets and updates the list view
     *          whenever a new project is created
     */
    private void updateExplorerListView(){
        controller.explorerListView.getItems().clear();
        updateFilteredListView();
    }

    /**
     * This method generates a filtered list from the original list
     * and updates the list view based on the filter selection
     */
    public ObservableList<FileNodeModel> updateFilteredListView(){
        ObservableList<FileNodeModel> filteredItems = FXCollections.observableArrayList();
        filteredItems.clear();
        for(FileNodeModel fileNode : projectNodesList){
            if (fileNode.getFileInfo() != null || !controller.documentedFilesCheckbox.isSelected()){
                filteredItems.add(fileNode);
            }
        }
        controller.explorerListView.getItems().clear();
        controller.explorerListView.getItems().addAll(filteredItems);
        return filteredItems;
    }

    public void initializeExplorerListView(){
        controller.explorerListView.setCellFactory(new Callback<ListView<FileNodeModel>, ListCell<FileNodeModel>>() {
            @Override
            public ListCell<FileNodeModel> call(ListView<FileNodeModel> listView) {
                return new ProjectExplorer.ExplorerItemCell();
            }
        });

    }

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
                text.setText(node.getName()); // Use your actual getter method for the name

                try {
                    if (node.getName().endsWith(Controller.CProject)) {
                        imageView.setImage(setIconForList("assets/icons/c_header.png")); // path to your file icon
                        text.setFill(Color.web("#28725f"));
                    } else if (node.getName().endsWith(".c") || node.getName().endsWith(".cpp")){
                        imageView.setImage(setIconForList("assets/icons/c_src.png")); // path to your folder icon
                        text.setFill(Color.web("#4b245b"));
                    } else if (node.getName().endsWith(Controller.JavaProject)) {
                        imageView.setImage(setIconForList("assets/icons/java_file.png")); // path to your folder icon
                        text.setFill(Color.web("#824d00"));
                    } else if (node.getName().endsWith(Controller.PythonProject)) {
                        imageView.setImage(setIconForList("assets/icons/py_file.png")); // path to your folder icon
                        text.setFill(Color.web("#62664d"));
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                imageView.setFitHeight(30.0);
                imageView.setFitWidth(30.0);
                HBox cellBox = new HBox(imageView, text);
                cellBox.setSpacing(10); // Set spacing as needed
                setGraphic(cellBox);
            }
        }

        private Image setIconForList(String iconPath) throws FileNotFoundException {
            FileInputStream input = new FileInputStream(iconPath);
            return new Image(input);
        }
    }
}
