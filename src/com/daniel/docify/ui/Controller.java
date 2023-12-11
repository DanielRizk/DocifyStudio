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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static com.daniel.docify.core.ActionManager.rootNode;
import static com.daniel.docify.core.Main.LOAD_ICONS;
import static com.daniel.docify.core.Main.VERSION;
import static com.daniel.docify.fileProcessor.DirectoryProcessor.buildDirTree;

public class Controller implements Initializable {

    private Stage primaryStage;

    @FXML
    private BorderPane MainBorderPaneLayout;

    @FXML
    private TextArea mainDisplayTextArea;

    @FXML
    private ListView<FileNodeModel> explorerListView = new ListView<>();

    @FXML
    private ListView<String> fileContentListView;

    @FXML
    private ListView<SearchResultModel> searchResultListView;

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
    private Label fileNameLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private TextField searchBar;

    @FXML
    public ProgressBar progressBar;

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
    void scrollToLine(MouseEvent event) {
        String selectedFunction = fileContentListView.getSelectionModel().getSelectedItem();

        if (selectedFunction != null) {
            int startIndex = mainDisplayTextArea.getText().indexOf(selectedFunction);
            int endIndex = startIndex + selectedFunction.length();

            // Highlight the function in the TextArea
            mainDisplayTextArea.selectRange(startIndex, endIndex);
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

    @FXML
    void closeOpenedProject(ActionEvent event) {
        if (rootNode != null) {
            rootNode = null;
            explorerTreeView.setRoot(null);
            explorerListView.getItems().clear();
            items.clear();
            mainDisplayTextArea.clear();
            fileContentListView.getItems().clear();
            searchResultListView.getItems().clear();
            searchResultListView.setVisible(false);
            mainDisplayTextArea.setVisible(true);
            primaryStage.setTitle("Docify Studio");
            fileNameLabel.setText(null);
        }
    }
    @FXML
    void getFromSearchResult(MouseEvent event){
        mainDisplayTextArea.clear();
        updateMainTextArea(
                searchResultListView.getSelectionModel().getSelectedItem().getParentFileNode().getFileInfo()
        );
        searchResultListView.getItems().clear();
        searchResultListView.setVisible(false);
        mainDisplayTextArea.setVisible(true);

    }

    @FXML
    void searchFromButton(MouseEvent event){
        searchAndDisplay();
    }

    @FXML
    void searchFromKey(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER){
            searchAndDisplay();
        }
    }

    @FXML
    void searchAndDisplay(){
        String searchKeyword = searchBar.getText();
        if (searchKeyword != null) {
            List<SearchResultModel> result = searchList(searchKeyword);
            mainDisplayTextArea.clear();
            searchResultListView.getItems().clear();
            fileContentListView.getItems().clear();
            mainDisplayTextArea.setVisible(false);
            searchResultListView.getItems().addAll(result);
            searchResultListView.setVisible(true);
        }
    }

    private List<SearchResultModel> searchList(String searchWord){
        ObservableList<FileNodeModel> allFiles = updateFilteredListView();
        List<SearchResultModel> searchResults = new ArrayList<>();

        for (FileNodeModel file : allFiles) {
            if (file.getFileInfo() != null) {
                for (String itemName : file.getFileInfo().getItemNames()) {
                    if (isMatch(itemName, searchWord)) {
                        SearchResultModel result = new SearchResultModel(itemName, file);
                        searchResults.add(result);
                    }
                }
            }
        }
        return searchResults;
    }

    private boolean isMatch(String itemName, String searchWord) {
        List<String> result = Arrays.asList(searchWord.trim().split(" "));
        return result.stream().allMatch(word ->
                itemName.toLowerCase().contains(word.toLowerCase()));
    }

    /**
     * @brief   This method allows the controller to access the
     *          primary stage from main
     */
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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

        if (selectedDir != null) {
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
            primaryStage.setTitle("Docify Studio - "+rootNode.getName());
            fileNameLabel.setText("Project name: "+rootNode.getName());
        }
    }

    /**
     * @brief   This method is displays and updates the main display view
     *          with the file content when the file is selected from the
     *          tree view or the list view, and call the update file content
     *          list method
     *
     * @note    experimental
     */
    private void updateMainTextArea(FileInfoModel fileInfo) {
        mainDisplayTextArea.clear();
        if (fileInfo != null) {
            for (FunctionModel function : fileInfo.getFunctionModel()) {
                if (function.getName() != null)
                    mainDisplayTextArea.appendText("Function Name: " + function.getName() + "\n");
                if (function.getDocumentation() != null) {
                    if (function.getDocumentation().getBrief() != null)
                        mainDisplayTextArea.appendText("Function Brief: " + function.getDocumentation().getBrief() + "\n");
                    for (String params : function.getDocumentation().getParams())
                        if (params != null) mainDisplayTextArea.appendText("Function Param: " + params + "\n");
                    if (function.getDocumentation().getReturn() != null)
                        mainDisplayTextArea.appendText("Function Return: " + function.getDocumentation().getReturn() + "\n");
                    if (function.getDocumentation().getNotes() != null)
                        mainDisplayTextArea.appendText("Note: " + function.getDocumentation().getNotes() + "\n");
                } else {
                    mainDisplayTextArea.appendText("No documentation available!\n");
                }
                if (function.getLineNumber() != null)
                    mainDisplayTextArea.appendText("Declared on line: " + function.getLineNumber() + "\n\n");
            }
        }
        updateFileContentListView(fileInfo);
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
        updateFilteredListView();
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
    private ObservableList<FileNodeModel> updateFilteredListView(){
        ObservableList<FileNodeModel> filteredItems = FXCollections.observableArrayList();
        filteredItems.clear();
        for(FileNodeModel item : items){
            if (item.getFileInfo() != null || !documentedFilesCheckbox.isSelected()){
                filteredItems.add(item);
            }
        }
        explorerListView.getItems().clear();
        explorerListView.getItems().addAll(filteredItems);
        return filteredItems;
    }

    /**
     * @brief   This method generates a list of all items names contained
     *          in a file, gets triggered whenever a file is selected from
     *          the tree view or the list view, and called by update main
     *          text display method
     */
    private void updateFileContentListView(FileInfoModel fileInfoModel){
        fileContentListView.getItems().clear();
        ObservableList<String> fileContentList = FXCollections.observableArrayList();
        fileContentList.clear();
        if (fileInfoModel != null && fileInfoModel.getFunctionModel() != null){
            for (FunctionModel func : fileInfoModel.getFunctionModel()){
                fileContentList.add(func.getName());
            }
        }
        fileContentListView.getItems().addAll(fileContentList);
    }

    private void setIcon(MenuItem menuItem, String iconPath) throws MalformedURLException {
        File iconFile = new File(iconPath);
        String iconUrl = iconFile.toURI().toURL().toExternalForm();
        ImageView imageView = new ImageView(new Image(iconUrl));
        imageView.setFitHeight(20.0);
        imageView.setFitWidth(20.0);
        menuItem.setGraphic(imageView);
    }

    private void setIcon(Menu menu, String iconPath) throws MalformedURLException {

        var iconFile = new File(iconPath);
        String iconUrl = iconFile.toURI().toURL().toExternalForm();
        ImageView imageView = new ImageView(new Image(iconUrl));
        imageView.setFitHeight(20.0);
        imageView.setFitWidth(20.0);
        menu.setGraphic(imageView);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (LOAD_ICONS) {
            try {
                setIcon(file_newSubMenu, "D:\\Projects\\Technical\\Programming\\DocifyStudio\\icons\\png\\new.png");
                setIcon(file_saveAsSubMenu, "D:\\Projects\\Technical\\Programming\\DocifyStudio\\icons\\png\\save.png");
                setIcon(file_openMenuItem, "D:\\Projects\\Technical\\Programming\\DocifyStudio\\icons\\png\\open.png");
                setIcon(file_closeMenuItem, "D:\\Projects\\Technical\\Programming\\DocifyStudio\\icons\\png\\close.png");
                setIcon(file_new_cProjectMenuItem, "D:\\Projects\\Technical\\Programming\\DocifyStudio\\icons\\png\\cprog.png");
                setIcon(file_new_javaProjectMenuItem, "D:\\Projects\\Technical\\Programming\\DocifyStudio\\icons\\png\\javaprog.png");
                setIcon(file_new_pythonProjectMenuItem, "D:\\Projects\\Technical\\Programming\\DocifyStudio\\icons\\png\\pyprog.png");
                setIcon(file_save_docifyMenuItem, "D:\\Projects\\Technical\\Programming\\DocifyStudio\\icons\\png\\docifyStudioLogo.png");
                setIcon(file_save_pdfMenuItem, "D:\\Projects\\Technical\\Programming\\DocifyStudio\\icons\\png\\pdf.png");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }


        //progressBar.setVisible(false);
        progressBar.setStyle("-fx-accent: green;");

        fileNameLabel.setText("");
        versionLabel.setText(VERSION);
    }

    private record SearchResultModel(String itemName, FileNodeModel fileNodeModel) {
        public FileNodeModel getParentFileNode() {
                return fileNodeModel;
            }
            @Override
            public String toString() {
                return itemName;
            }
        }
}

//    public void updateProgressBar(double currentFilesCount) {
//        double x = (currentFilesCount / filesCount);
//        x = Math.max(0.0, Math.min(1.0, x));
//        double mappedValue = (x - 0.0);
//        // Assuming progressBar is an instance variable representing your ProgressBar
//        progressBar.setProgress(mappedValue);
//    }

//    public static void updateProgressBar(double currentFilesCount) {
//        double x = (currentFilesCount / filesCount);
//        x = Math.max(0.0, Math.min(1.0, x));
//        double mappedValue = (x - 0.0);
//        Assuming progressBar is an instance variable representing your ProgressBar
//        progressBar.setProgress(mappedValue);
//    }

//directoryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Select Directory", "*"));