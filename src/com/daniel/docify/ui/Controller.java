package com.daniel.docify.ui;

import com.daniel.docify.core.ActionManager;
import com.daniel.docify.fileProcessor.FileNodeModel;
import com.daniel.docify.fileProcessor.UserConfiguration;
import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.model.FunctionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.daniel.docify.core.ActionManager.rootNode;
import static com.daniel.docify.fileProcessor.DirectoryProcessor.buildDirTree;

public class Controller {

    @FXML
    private BorderPane MainBorderPaneLayout;

    @FXML
    private TextArea MainDisplayTextArea;

    @FXML
    private ListView<FileNodeModel> explorerListView = new ListView<>();

    private final ObservableList<FileNodeModel> items = FXCollections.observableArrayList();

    @FXML
    private CheckBox documentedFilesCheckbox;

    @FXML
    private TreeView<FileNodeModel> explorerTreeView;

    @FXML
    private Menu fileSubMenu;

    @FXML
    private MenuItem file_closeMenuItem;

    @FXML
    private Menu file_newSubMenu;

    @FXML
    private MenuItem file_new_cProjectMenuItem;

    @FXML
    private MenuItem file_new_javaProjectMenuItem;

    @FXML
    private MenuItem file_new_pythonProjectMenuItem;

    @FXML
    private MenuItem file_openMenuItem;

    @FXML
    private Menu file_saveAsSubMenu;

    @FXML
    private MenuItem file_save_docifyMenuItem;

    @FXML
    private MenuItem file_save_pdfMenuItem;

    @FXML
    private Menu helpSubMenu;

    @FXML
    private TabPane leftSide_tabbedPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    void treeViewFileSelection(MouseEvent event) {
        if(explorerTreeView.getSelectionModel().getSelectedItem() != null &&
                explorerTreeView.getSelectionModel().getSelectedItem().isLeaf()){
            updateMainTextArea(explorerTreeView.getSelectionModel().getSelectedItem().getValue().getFileInfo());
        }
    }

    @FXML
    void listViewFileSelection(MouseEvent event) {
        if(explorerListView.getSelectionModel().getSelectedItem() != null){
            updateMainTextArea(explorerListView.getSelectionModel().getSelectedItem().getFileInfo());
        }
    }

    @FXML
    void cProjectMenuItemStart(ActionEvent event) {
        startNew(ActionManager.CProject);
    }

    @FXML
    void javaProjectMenuItemStart(ActionEvent event) {
        startNew(ActionManager.JavaProject);
    }

    @FXML
    void pythonProjectMenuItemStart(ActionEvent event) {
        startNew(ActionManager.PythonProject);
    }

    @FXML
    void isDocumentedFilesCheckbox(MouseEvent event) {
        updateFilteredListView();
    }


    /**
     * @brief   This method is triggered when the create new option is clicked
     *          it build the tree view and the list view of the root dir
     *          and prepares the UI and populates the views
     */
    void startNew(String fileType) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File lastOpenPath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastOpenConfig()));

        directoryChooser.setInitialDirectory(lastOpenPath);
        File selectedDir = directoryChooser.showDialog(new Stage());

        System.out.println("Selected Directory " + selectedDir.getAbsolutePath());
        UserConfiguration.saveUserLastOpenConfig(selectedDir.getAbsolutePath());

        try {
            rootNode = buildDirTree(selectedDir, fileType);
            assert rootNode != null;
            updateTreeView(rootNode);
            updateListView();
        } catch (IOException e){
                throw new RuntimeException(e);
        }

    }

    /**
     * @brief   This method is displays and updates the main display view
     *          with the file content when the file is selected from the
     *          tree view or the list view
     *
     * @note    experimental
     */
    private void updateMainTextArea(FileInfoModel fileInfo) {
        MainDisplayTextArea.clear();
        if (fileInfo != null) {
            for (FunctionModel function : fileInfo.getFunctionModel()) {
                if (function.getName() != null)
                    MainDisplayTextArea.appendText("Function Name: " + function.getName() + "\n");
                if (function.getDocumentation() != null) {
                    if (function.getDocumentation().getBrief() != null)
                        MainDisplayTextArea.appendText("Function Brief: " + function.getDocumentation().getBrief() + "\n");
                    for (String params : function.getDocumentation().getParams())
                        if (params != null) MainDisplayTextArea.appendText("Function Param: " + params + "\n");
                    if (function.getDocumentation().getReturn() != null)
                        MainDisplayTextArea.appendText("Function Return: " + function.getDocumentation().getReturn() + "\n");
                    if (function.getDocumentation().getNotes() != null)
                        MainDisplayTextArea.appendText("Note: " + function.getDocumentation().getNotes() + "\n");
                } else {
                    MainDisplayTextArea.appendText("No documentation available!\n");
                }
                if (function.getLineNumber() != null)
                    MainDisplayTextArea.appendText("Declared on line: " + function.getLineNumber() + "\n\n");
            }
        }
    }

    /**
     * @brief   This method populates and updates the Tree view, and calls
     *          the generateListView method
     */
    private void updateTreeView(FileNodeModel rootFileNode){
        TreeItem<FileNodeModel> treeItem;
        if (rootFileNode == null){
            treeItem = new TreeItem<>();
        }
        else {
            treeItem = convertToTreeItem(rootFileNode);
        }
        explorerTreeView.setRoot(treeItem);
        assert rootFileNode != null;
        items.clear();
        generateListview(rootFileNode);
    }

    /**
     * @brief   This method converts FileNodeModel instances to TreeItem model
     *          to be compatible with TreeView model
     */
    private TreeItem<FileNodeModel> convertToTreeItem(FileNodeModel fileNode){

        TreeItem<FileNodeModel> treeItem = new TreeItem<>(fileNode);
        for(FileNodeModel child : fileNode.getChildren()){
            treeItem.getChildren().add(convertToTreeItem(child));
        }
        return treeItem;
    }

    /**
     * @brief   This method sets and updates the list view
     *          whenever a new project is created
     */
    private void updateListView(){
        explorerListView.getItems().clear();
        explorerListView.getItems().addAll(items);
    }

    /**
     * @brief   This method generates a list of FileNodeModel
     *          from the root node and adds it to the global
     *          list container
     */
    private void generateListview(FileNodeModel fileNode){
        if (fileNode.isFile()) {
            items.add(fileNode);
        }
        for (FileNodeModel child : fileNode.getChildren()) {
            generateListview(child);
        }
    }

    /**
     * @brief   This method generates a filtered list from the original list
     *          and updates the list view based on the filter selection
     */
    private void updateFilteredListView(){
        ObservableList<FileNodeModel> filteredItems = FXCollections.observableArrayList();
        filteredItems.clear();
        for(FileNodeModel item : items){
            if (item.getFileInfo() != null || !documentedFilesCheckbox.isSelected()){
                filteredItems.add(item);
            }
        }
        explorerListView.getItems().clear();
        explorerListView.getItems().addAll(filteredItems);
    }


    /*&& fileNode.getFileInfo() != null*/


}




//directoryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Select Directory", "*"));