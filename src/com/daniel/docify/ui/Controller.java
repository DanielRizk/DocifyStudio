package com.daniel.docify.ui;

import com.daniel.docify.fileProcessor.FileNodeModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import static com.daniel.docify.core.ActionManager.rootNode;

public class Controller {

    @FXML
    private BorderPane MainBorderPaneLayout;

    @FXML
    private ListView<?> esplorerListView;

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
    void SearchButton(ActionEvent event) {
        System.out.println("i have been chosen");

    }
}
