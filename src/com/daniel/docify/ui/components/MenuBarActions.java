package com.daniel.docify.ui.components;

import com.daniel.docify.core.Main;
import com.daniel.docify.fileProcessor.DirectoryProcessor;
import com.daniel.docify.fileProcessor.UserConfiguration;
import com.daniel.docify.model.FileFormatModel;
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

/**
 * This class handles all actions associated with the UI MenuBar options.
 */
public class MenuBarActions extends ControllerUtils {

    private FileFormatModel fileFormatModel = new FileFormatModel(new FileNodeModel(null,null, false, null));
    private final DirectoryProcessor processor = new DirectoryProcessor(controller);

    public FileFormatModel getFileFormatModel() {
        return fileFormatModel;
    }

    public MenuBarActions(Controller controller) throws IOException {
        super(controller);
    }

    /**
     * This method is triggered when the create new option is clicked, it prompts
     * the user to choose the path to the project and give the control to the
     * {@link com.daniel.docify.fileProcessor.DirectoryProcessor} to build the TreeView.
     */
    public void startNew(String fileType) {
        if (Objects.equals(fileType, Controller.JavaProject)){
            controller.utils.popUpAlert(Alert.AlertType.INFORMATION, "Information",
                    "Java documentation will be available in the next release");
            return;
        }else if (Objects.equals(fileType, Controller.PythonProject)){
            controller.utils.popUpAlert(Alert.AlertType.INFORMATION, "Information",
                    "Python documentation will be available in the next release");
            return;
        }
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
                processor.buildAndProcessDirectory(selectedDir, fileType, rootNode -> {
                    fileFormatModel.setRootNode(rootNode);
                    assert fileFormatModel.getRootNode() != null;
                    controller.explorer.updateTreeView(fileFormatModel.getRootNode());
                    controller.getPrimaryStage().setTitle("Docify Studio - " + fileFormatModel.getRootNode().getName());
                    controller.utils.updateInfoLabel("Project Documentation - " + fileFormatModel.getRootNode().getName() + " - created successfully");
                });


            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method saves the built project as a .doci file using the {@link com.daniel.docify.fileProcessor.FileSerializer}.
     */
    public void saveDociFile(){
        if (fileFormatModel.getRootNode() != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Docify File");
            File lastSavePath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastSaveConfig()));

            fileChooser.setInitialDirectory(lastSavePath);
            FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
            FileChooser.ExtensionFilter docifyFilter = new FileChooser.ExtensionFilter("Docify File (*.doci)", "*.doci");
            fileChooser.getExtensionFilters().addAll(docifyFilter, allFilesFilter);

            File selectedDir = fileChooser.showSaveDialog(null);

            if (selectedDir != null) {
                System.out.println("Selected Directory " + selectedDir.getParent());
                UserConfiguration.saveUserLastSaveConfig(selectedDir.getParent());

                if (selectedDir.getAbsolutePath().endsWith(".doci")) {
                    fileFormatModel.save(fileFormatModel, selectedDir.getAbsolutePath());
                } else {
                    fileFormatModel.save(fileFormatModel, selectedDir.getAbsolutePath() + ".doci");
                }
                controller.utils.updateInfoLabel("File saved successfully");
            }
        }else {
            controller.utils.popUpAlert(Alert.AlertType.ERROR, "Error", "No project opened");
        }
    }

    /**
     * This method opens saved .doci file using the {@link com.daniel.docify.fileProcessor.FileSerializer} and
     * it repopulates all necessary UI components.
     */
    public void openDociFile() throws MalformedURLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Docify File");
        File lastSavePath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastSaveConfig()));

        fileChooser.setInitialDirectory(lastSavePath);
        //directoryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Select Directory", "*"));
        FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
        FileChooser.ExtensionFilter docifyFilter = new FileChooser.ExtensionFilter("Docify File (*.doci)", "*.doci");
        fileChooser.getExtensionFilters().addAll(docifyFilter, allFilesFilter);

        File selectedDir = fileChooser.showOpenDialog(null);

        if (selectedDir != null) {
            closeRoutine();
            System.out.println("Selected Directory " + selectedDir.getParent());
            UserConfiguration.saveUserLastSaveConfig(selectedDir.getParent());

            if (selectedDir.getAbsolutePath().endsWith(".doci")) {
                fileFormatModel = fileFormatModel.load(selectedDir.getAbsolutePath());
                if (fileFormatModel == null) {
                    fileFormatModel = new FileFormatModel(new FileNodeModel(null, null,false, null));
                    closeRoutine();
                    return;
                }
                controller.explorer.updateTreeView(fileFormatModel.getRootNode());
                controller.utils.updateInfoLabel("File - " + fileFormatModel.getRootNode().getName() + " - opened successfully");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Error opening file");
                alert.showAndWait();
            }
            controller.getPrimaryStage().setTitle("Docify Studio - " + fileFormatModel.getRootNode().getName());
            controller.utils.updateInfoLabel("Project Documentation - " + fileFormatModel.getRootNode().getName() + " - loaded successfully");
        }
    }


    /**
     * This method closes the currently open project and performs
     * the exiting routine of all involved UI components.
     */
    public void closeRoutine(){
        if (fileFormatModel != null) {
            fileFormatModel.setRootNode(null);
            controller.getExplorerTreeView().setRoot(null);
            controller.getExplorerListView().getItems().clear();
            controller.explorer.getProjectNodesList().clear();
            controller.mainWindow.getDocumentationView().getEngine().loadContent("");
            controller.mainWindow.fileInfoBuff = null;
            controller.getFileContentListView().getItems().clear();
            controller.getSearchResultListView().getItems().clear();
            controller.fileRawCode.getCodeView().clear();
            controller.getSearchResultListView().setVisible(false);
            controller.mainWindow.getDocumentationView().setVisible(true);
            controller.getPrimaryStage().setTitle("Docify Studio");
            controller.getInfoLabel().setText(null);
        }
    }

    /**
     * This method is used to display the metadata of the file and
     * the software to the user.
     */
    public void displayMetaData() {
        if (fileFormatModel != null) {
            controller.utils.popUpAlert(Alert.AlertType.INFORMATION, "About",
                    (fileFormatModel.getAuthorName() == null ? "" : "author: " + fileFormatModel.getAuthorName()) +
                            (fileFormatModel.getFileFormatVersion() == null ? "" : "\nFile version: " + fileFormatModel.getFileFormatVersion()) +
                            (fileFormatModel.getCreationDate() == null ? "" : "\nDate and time: " + fileFormatModel.getCreationDate()) +
                            "\nSoftware version: " + Main.SOFTWARE_VERSION
            );
        }else{
            controller.utils.popUpAlert(Alert.AlertType.INFORMATION, "About",
                            "Software version: " + Main.SOFTWARE_VERSION
            );
        }
    }
}
