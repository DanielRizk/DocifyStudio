package com.daniel.docify.ui.components;

import com.daniel.docify.fileProcessor.UserConfiguration;
import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.ui.Controller;
import com.daniel.docify.ui.utils.ControllerUtils;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;

import static com.daniel.docify.fileProcessor.DirectoryProcessor.BuildAndProcessDirectory;
import static com.daniel.docify.ui.Controller.rootNode;

public class MenuBarActions extends ControllerUtils {
    public MenuBarActions(Controller controller) {
        super(controller);
    }

    public void saveDociFile(){
        if (rootNode != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Docify File");
            File lastSavePath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastSaveConfig()));

            fileChooser.setInitialDirectory(lastSavePath);

            FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All Files", "*.*");

            // Set extension filter for suggested file type
            FileChooser.ExtensionFilter docifyFilter = new FileChooser.ExtensionFilter("Docify File (*.doci)", "*.doci");

            // Add the filters to the file chooser
            fileChooser.getExtensionFilters().addAll(docifyFilter, allFilesFilter);

            // Show the Save File dialog
            File selectedDir = fileChooser.showSaveDialog(null);

            if (selectedDir != null) {
                System.out.println("Selected Directory " + selectedDir.getParent());
                UserConfiguration.saveUserLastSaveConfig(selectedDir.getParent());

                if (selectedDir.getAbsolutePath().endsWith(".doci")) {
                    rootNode.save(rootNode, selectedDir.getAbsolutePath());
                } else {
                    rootNode.save(rootNode, selectedDir.getAbsolutePath() + ".doci");
                }
                controller.utils.updateInfoLabel("File saved successfully");
            }
        }else {
            controller.utils.popUpAlert(Alert.AlertType.ERROR, "Error", "No project opened");
        }
    }

    public void openDociFile() throws MalformedURLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Docify File");
        File lastSavePath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastSaveConfig()));

        fileChooser.setInitialDirectory(lastSavePath);

        // Set extension filters (optional)
        //directoryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Select Directory", "*"));
        FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All Files", "*.*");

        // Set extension filter for suggested file type
        FileChooser.ExtensionFilter docifyFilter = new FileChooser.ExtensionFilter("Docify File (*.doci)", "*.doci");

        // Add the filters to the file chooser
        fileChooser.getExtensionFilters().addAll(docifyFilter, allFilesFilter);

        // Show the Save File dialog
        File selectedDir = fileChooser.showOpenDialog(null);

        if (selectedDir != null) {
            closeRoutine();
            System.out.println("Selected Directory " + selectedDir.getParent());
            UserConfiguration.saveUserLastSaveConfig(selectedDir.getParent());

            if (selectedDir.getAbsolutePath().endsWith(".doci")) {
                rootNode = new FileNodeModel(null,false,null);
                rootNode = rootNode.load(selectedDir.getAbsolutePath());
                controller.explorer.updateTreeView(rootNode);
                controller.utils.updateInfoLabel("File -"+rootNode.getName()+"- opened successfully");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Error opening file");
                alert.showAndWait();
            }
            controller.primaryStage.setTitle("Docify Studio - "+rootNode.getName());
            controller.utils.updateInfoLabel("Project Documentation -"+rootNode.getName()+"- loaded successfully");
        }
    }

    /**
     * @brief   This method is triggered when the create new option is clicked
     *          it build the tree view and the list view of the root dir
     *          and prepares the UI and populates the views
     */
    public void startNew(String fileType) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Create new project");

        File lastOpenPath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastOpenConfig()));

        if (lastOpenPath.exists() && !lastOpenPath.getAbsolutePath().isEmpty()) {
            directoryChooser.setInitialDirectory(lastOpenPath);
        }

        File selectedDir = directoryChooser.showDialog(new Stage());

        if (selectedDir != null) {

            closeRoutine();

            System.out.println("Selected Directory " + selectedDir.getAbsolutePath());
            UserConfiguration.saveUserLastOpenConfig(selectedDir.getAbsolutePath());

            try {
                rootNode = BuildAndProcessDirectory(selectedDir, fileType);
                assert rootNode != null;
                controller.explorer.updateTreeView(rootNode);

            } catch (IOException e){
                throw new RuntimeException(e);
            }
            controller.primaryStage.setTitle("Docify Studio - "+rootNode.getName());
            controller.utils.updateInfoLabel("Project Documentation -"+rootNode.getName()+"- created successfully");
        }
    }

    public void closeRoutine(){
        if (rootNode != null) {
            rootNode = null;
            controller.explorerTreeView.setRoot(null);
            controller.explorerListView.getItems().clear();
            controller.explorer.projectNodesList.clear();
            controller.mainWindow.documentationView.getEngine().loadContent("");
            controller.fileContentListView.getItems().clear();
            controller.searchResultListView.getItems().clear();
            controller.fileRawCode.codeView.clear();
            controller.searchResultListView.setVisible(false);
            controller.mainWindow.documentationView.setVisible(true);
            controller.primaryStage.setTitle("Docify Studio");
            controller.infoLabel.setText(null);
        }
    }
}
