package com.daniel.docify.ui;

import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.model.FileInfoModel.ItemNameAndProperty;
import com.daniel.docify.ui.components.*;
import com.daniel.docify.ui.utils.ControllerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


import static com.daniel.docify.core.Main.LOAD_ICONS;
import static com.daniel.docify.core.Main.SOFTWARE_VERSION;

/**
 * This class is the controller for JavaFX SceneBuilder application, It manages all the actions
 * from the user in the UI
 */
public class Controller implements Initializable {

    /* Core variables */
    private Stage primaryStage;

    /* UI components objects */
    public final ControllerUtils utils = new ControllerUtils(this);
    public final MenuBarActions menuActions = new MenuBarActions(this);
    public final ProjectExplorer explorer = new ProjectExplorer(this);
    public final FileDocContent docContent = new FileDocContent(this);
    public final MainWindow mainWindow = new MainWindow(this);
    public final FileRawCode fileRawCode = new FileRawCode(this);

    /* Logger */
    public static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    /* Static variables */
    public final static String C_PROJECT = ".h";
    public final static String PYTHON_PROJECT = ".py";
    public final static String JAVA_PROJECT = ".java";

    public Controller() throws IOException {
    }

    /* Class private Singleton instance */ /** NOT USED! */
    private static Controller instance;

    /* Singleton constructor */ /** NOT USED! */
    private Controller(String data) throws IOException {
    }

    /* Singleton getter method */ /** NOT USED! */
    public static Controller getInstance() throws IOException {
        Controller result = instance;
        if (result == null) {
            synchronized (Controller.class) {
                result = instance;
                if (instance == null) {
                    instance = new Controller("test");
                }
            }
        }
        return result;
    }

    /* getters for private Variables */
    public Stage getPrimaryStage(){
        return primaryStage;
    }

    /* JavaFX SceneBuilder specific variables */
    @FXML
    private TreeView<FileNodeModel> explorerTreeView;
    @FXML
    private ListView<FileNodeModel> explorerListView = new ListView<>();
    @FXML
    private ListView<ItemNameAndProperty> fileContentListView;
    @FXML
    private ListView<ControllerUtils.SearchResultModel> searchResultListView;
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
    private MenuItem file_refresh;
    @FXML
    private MenuItem file_save;
    @FXML
    private MenuItem file_save_docifyMenuItem;
    @FXML
    private MenuItem file_save_pdfMenuItem;
    @FXML
    private MenuItem file_closeMenuItem;
    @FXML
    private MenuItem about_menuItem;
    @FXML
    private MenuItem help_menuItem;
    @FXML
    private Tab fileContentTab;
    @FXML
    private Tab fileDocumentationTab;
    @FXML
    private CheckBox documentedFilesCheckbox;
    @FXML
    private Label infoLabel;
    @FXML
    private Label versionLabel;
    @FXML
    private TextField searchBar;
    @FXML
    private ProgressBar progressBar;

    /* getters for JavaFX objects Variables */

    public TreeView<FileNodeModel> getExplorerTreeView(){
        return explorerTreeView;
    }

    public ListView<FileNodeModel> getExplorerListView(){
        return explorerListView;
    }

    public ListView<ItemNameAndProperty> getFileContentListView(){
        return fileContentListView;
    }

    public ListView<ControllerUtils.SearchResultModel> getSearchResultListView(){
        return searchResultListView;
    }

    public CheckBox getDocumentedFilesCheckbox(){
        return documentedFilesCheckbox;
    }

    public Label getInfoLabel(){
        return infoLabel;
    }

    public TextField getSearchBar(){
        return searchBar;
    }

    public ProgressBar getProgressBar(){
        return progressBar;
    }


    /* JavaFX SceneBuilder specific methods */

    @FXML
    void cProjectMenuItemStart(ActionEvent event) {
        menuActions.startNew(C_PROJECT);
    }

    @FXML
    void javaProjectMenuItemStart(ActionEvent event) {
        menuActions.startNew(JAVA_PROJECT);
    }

    @FXML
    void pythonProjectMenuItemStart(ActionEvent event) {
        menuActions.startNew(PYTHON_PROJECT);
    }

    @FXML
    void openDociFile(ActionEvent event) throws MalformedURLException {
        menuActions.openDociFile();
    }

    @FXML
    void refreshProject(ActionEvent event) throws MalformedURLException {
        menuActions.refreshProject();
    }

    @FXML
    void saveDociFile(ActionEvent event) throws MalformedURLException {
        menuActions.saveDociFile();
    }

    @FXML
    void saveAsDociFile(ActionEvent event) {
        menuActions.saveAsDociFile();
    }

    @FXML
    void saveAsPDF(ActionEvent event) { menuActions.saveAsPDF();}

    @FXML
    void closeOpenedProject(ActionEvent event) {
        menuActions.closeRoutine();
    }

    @FXML
    void displayMetadata(ActionEvent event) {
        menuActions.displayMetaData();
    }
    @FXML
    void displayHelpMenu(ActionEvent event) {
        HelpMenu help = new HelpMenu();
    }

    @FXML
    void treeViewFileSelection(MouseEvent event) {
        if(explorerTreeView.getSelectionModel().getSelectedItem() != null &&
                explorerTreeView.getSelectionModel().getSelectedItem().isLeaf() &&
                event.getButton() == MouseButton.PRIMARY){
            mainWindow.compileWebViewDisplay(explorerTreeView.getSelectionModel().getSelectedItem().getValue().getFileInfo());
        }
    }

    @FXML
    void listViewFileSelection(MouseEvent event) {
        if(explorerListView.getSelectionModel().getSelectedItem() != null
                && event.getButton() == MouseButton.PRIMARY){
            mainWindow.compileWebViewDisplay(explorerListView.getSelectionModel().getSelectedItem().getFileInfo());
        }
    }

    @FXML
    void fileContentListSelection(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            ItemNameAndProperty selectedItem = fileContentListView.getSelectionModel().getSelectedItem();
            utils.scrollToLine(selectedItem.toString());
        }
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
        explorer.updateExplorerListView();
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
        fileDocumentationTab.setContent(mainWindow.getDocumentationView());

        fileRawCode.initializeCodeArea();
        docContent.initializeFileContentListView();
        explorer.initializeExplorerListView();
        mainWindow.initializeSearchResultListView();
        //mainWindow.initializeWebViewEngine(); /* for debugging */
        mainWindow.loadWebViewStyling();

        progressBar.setVisible(false);
        progressBar.setStyle("-fx-accent: green;");

        infoLabel.setText("");
        versionLabel.setText("Docify Studio v"+SOFTWARE_VERSION);
        utils.updateInfoLabel("Initialization complete!");
    }


    /**
     * This method allows the controller to access the primary stage from main
     */
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeKeyBindings();
    }


/**
 * This Method loads all system Icons to the UI MenuBar
 */
    private void loadSystemIcons(){
        try {
            utils.setIcon(file_newSubMenu, "resources/assets/icons/new.png");
            utils.setIcon(file_saveAsSubMenu, "resources/assets/icons/save.png");
            utils.setIcon(file_save, "resources/assets/icons/save.png");
            utils.setIcon(file_openMenuItem, "resources/assets/icons/open.png");
            utils.setIcon(file_closeMenuItem, "resources/assets/icons/close.png");
            utils.setIcon(file_refresh, "resources/assets/icons/refresh.png");
            utils.setIcon(file_new_cProjectMenuItem, "resources/assets/icons/cprog.png");
            utils.setIcon(file_new_javaProjectMenuItem, "resources/assets/icons/javaprog.png");
            utils.setIcon(file_new_pythonProjectMenuItem, "resources/assets/icons/pyprog.png");
            utils.setIcon(file_save_docifyMenuItem, "resources/assets/icons/doci.png");
            utils.setIcon(file_save_pdfMenuItem, "resources/assets/icons/pdf.png");
            utils.setIcon(about_menuItem, "resources/assets/icons/about.png");
            utils.setIcon(help_menuItem, "resources/assets/icons/help.png");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    KeyCombination ctrlN = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
    KeyCombination ctrlO = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
    KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    KeyCombination ctrlQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
    KeyCombination ctrlShS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);

    private void initializeKeyBindings(){
        primaryStage.getScene().addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (ctrlN.match(event)) {
                Optional<ButtonType> result;
                try {
                    result = menuActions.showCreateNewOptionDialog();
                } catch (FileNotFoundException | MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                result.ifPresent(buttonType -> {
                    if (Objects.equals(buttonType.getText(), "C/C++ project")){
                        menuActions.startNew(C_PROJECT);
                    } else if (Objects.equals(buttonType.getText(), "Java project")) {
                        menuActions.startNew(JAVA_PROJECT);
                    } else if (Objects.equals(buttonType.getText(), "Python project")) {
                        menuActions.startNew(PYTHON_PROJECT);
                    }
                });
                event.consume();
            } else if (ctrlO.match(event)) {
                try {
                    menuActions.openDociFile();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                event.consume();
            }else if (ctrlS.match(event)) {
                try {
                    menuActions.saveDociFile();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                event.consume();
            } else if (ctrlShS.match(event)) {
                Optional<ButtonType> result;
                result = menuActions.showSaveAsOptionDialog();
                result.ifPresent(buttonType -> {
                    if (Objects.equals(buttonType.getText(), "Doci file")){
                        menuActions.saveAsDociFile();
                    } else if (Objects.equals(buttonType.getText(), "PDF file")) {
                        menuActions.saveAsPDF();
                    }
                });
                event.consume();
            }
            else if (ctrlQ.match(event)) {
                menuActions.closeRoutine();
                event.consume();
            }
        });
    }
}