package com.daniel.docify.ui;

import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.fileProcessor.UserConfiguration;
import com.daniel.docify.model.FileInfoModel.ItemNameAndProperty;
import com.daniel.docify.ui.components.*;
import com.daniel.docify.ui.utils.ControllerUtils;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.logging.Logger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


import static com.daniel.docify.core.Main.LOAD_ICONS;
import static com.daniel.docify.core.Main.VERSION;
import static com.daniel.docify.fileProcessor.DirectoryProcessor.BuildAndProcessDirectory;

public class Controller implements Initializable {

    /* Core variables */
    public static FileNodeModel rootNode = null;
    public Stage primaryStage;

    /* UI components objects */
    public final ControllerUtils utils = new ControllerUtils(this);
    private final MenuBarActions menuActions = new MenuBarActions(this);
    public final ProjectExplorer explorer = new ProjectExplorer(this);
    public final FileDocContent docContent = new FileDocContent(this);
    public final MainWindow mainWindow = new MainWindow(this);
    public final FileRawCode fileRawCode = new FileRawCode(this);

    /* Logger */
    public static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    /* Static variables */
    public final static String CProject         = ".h";
    public final static String PythonProject    = ".py";
    public final static String JavaProject      = ".java";

    /* getters and setter for private Variables */


    /* JavaFX SceneBuilder specific variables */
    @FXML
    public TreeView<FileNodeModel> explorerTreeView;
    @FXML
    public ListView<FileNodeModel> explorerListView = new ListView<>();
    @FXML
    public ListView<ItemNameAndProperty> fileContentListView;
    @FXML
    public ListView<ControllerUtils.SearchResultModel> searchResultListView;
    @FXML
    private Menu file_saveAsSubMenu;
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
    private MenuItem file_save_docifyMenuItem;
    @FXML
    private MenuItem file_save_pdfMenuItem;
    @FXML
    private MenuItem file_closeMenuItem;
    @FXML
    private Tab fileContentTab;
    @FXML
    private Tab fileDocumentationTab;
    @FXML
    public CheckBox documentedFilesCheckbox;
    @FXML
    public Label infoLabel;
    @FXML
    private Label versionLabel;
    @FXML
    public TextField searchBar;
    @FXML
    public ProgressBar progressBar;

    /* JavaFX SceneBuilder specific methods */
    @FXML
    void cProjectMenuItemStart(ActionEvent event) {
        menuActions.startNew(CProject);
    }

    @FXML
    void javaProjectMenuItemStart(ActionEvent event) {
        utils.popUpAlert(Alert.AlertType.INFORMATION, "Information",
                "Java documentation will be available in the next release");
    }

    @FXML
    void pythonProjectMenuItemStart(ActionEvent event) {
        utils.popUpAlert(Alert.AlertType.INFORMATION, "Information",
                "Python documentation will be available in the next release");
    }

    @FXML
    void openDociFile(ActionEvent event) throws MalformedURLException {

        menuActions.openDociFile();
    }

    @FXML
    void saveDociFile(ActionEvent event) {
        menuActions.saveDociFile();
    }

    @FXML
    void closeOpenedProject(ActionEvent event) {
        menuActions.closeRoutine();
    }

    @FXML
    void treeViewFileSelection(MouseEvent event) {
        if(explorerTreeView.getSelectionModel().getSelectedItem() != null &&
                explorerTreeView.getSelectionModel().getSelectedItem().isLeaf()){
            mainWindow.compileWebViewDisplay(explorerTreeView.getSelectionModel().getSelectedItem().getValue().getFileInfo());
        }
    }

    @FXML
    void listViewFileSelection(MouseEvent event) {
        if(explorerListView.getSelectionModel().getSelectedItem() != null){
            mainWindow.compileWebViewDisplay(explorerListView.getSelectionModel().getSelectedItem().getFileInfo());
        }
    }

    @FXML
    void fileContentListSelection(MouseEvent event) {
        ItemNameAndProperty selectedItem = fileContentListView.getSelectionModel().getSelectedItem();
        utils.scrollToLine(selectedItem.toString());
    }

    @FXML
    void searchFromButton(MouseEvent event){
        utils.searchAndDisplay();
    }

    @FXML
    void searchFromKey(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER){
            utils.searchAndDisplay();
        }
    }

    @FXML
    void getFromSearchResult(MouseEvent event){
        utils.getFromSearchResult();
    }

    @FXML
    void isDocumentedFilesCheckbox(MouseEvent event) {
        explorer.updateFilteredListView();
    }


/**
 * This method is a part of Interface {@link Initializable} and it is the entry
 * point of the controller allowing to set up and initialize necessary variables
 * and objects
 */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (LOAD_ICONS) loadSystemIcons();

        fileContentTab.setContent(fileRawCode.codeAreaScrollPane);
        fileDocumentationTab.setContent(mainWindow.documentationView);

        fileRawCode.initializeCodeArea();
        docContent.initializeFileContentListView();
        explorer.initializeExplorerListView();
        mainWindow.loadWebViewStyling();

        progressBar.setVisible(false);
        progressBar.setStyle("-fx-accent: green;");

        infoLabel.setText("");
        versionLabel.setText(VERSION);
        utils.updateInfoLabel("Initialization complete!");
    }


    /**
     * This method allows the controller to access the primary stage from main
     */
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


/**
 * This Method loads all system Icons to the UI MenuBar
 */
    private void loadSystemIcons(){
        try {
            utils.setIcon(file_newSubMenu, "assets/icons/new.png");
            utils.setIcon(file_saveAsSubMenu, "assets/icons/save.png");
            utils.setIcon(file_openMenuItem, "assets/icons/open.png");
            utils.setIcon(file_closeMenuItem, "assets/icons/close.png");
            utils.setIcon(file_new_cProjectMenuItem, "assets/icons/cprog.png");
            utils.setIcon(file_new_javaProjectMenuItem, "assets/icons/javaprog.png");
            utils.setIcon(file_new_pythonProjectMenuItem, "assets/icons/pyprog.png");
            utils.setIcon(file_save_docifyMenuItem, "assets/icons/doci.png");
            utils.setIcon(file_save_pdfMenuItem, "assets/icons/pdf.png");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}