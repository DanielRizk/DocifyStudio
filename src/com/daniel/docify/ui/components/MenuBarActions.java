package com.daniel.docify.ui.components;

import com.daniel.docify.core.Main;
import com.daniel.docify.fileProcessor.*;
import com.daniel.docify.model.FileFormatModel;
import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.ui.Controller;
import com.daniel.docify.ui.utils.ControllerUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Objects;
import java.util.Optional;

/**
 * This class handles all actions associated with the UI MenuBar options.
 */
public class MenuBarActions extends ControllerUtils {

    private FileFormatModel fileFormatModel = new FileFormatModel(new FileNodeModel(null,null, false, null));
    private final DirectoryProcessor processor = new DirectoryProcessor(controller);

    public FileFormatModel getFileFormatModel() {
        return fileFormatModel;
    }

    public MenuBarActions(Controller controller){
        super(controller);
    }

    /**
     * This method is triggered when the create new option is clicked, it prompts
     * the user to choose the path to the project and give the control to the
     * {@link com.daniel.docify.fileProcessor.DirectoryProcessor} to build the TreeView.
     */
    public void startNew(String fileType) {
        if (Objects.equals(fileType, Controller.JAVA_PROJECT)){
            ControllerUtils.popUpAlert(Alert.AlertType.INFORMATION, "Information",
                    "Java documentation will be available in the next release");
            return;
        }else if (Objects.equals(fileType, Controller.PYTHON_PROJECT)){
            ControllerUtils.popUpAlert(Alert.AlertType.INFORMATION, "Information",
                    "Python documentation will be available in the next release");
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Create new project");
        try {
            UserConfiguration.checkUserConfiguration();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                    if (fileFormatModel.getRootNode() != null) {
                        controller.explorer.updateTreeView(fileFormatModel.getRootNode());
                        controller.getPrimaryStage().setTitle("Docify Studio - " + fileFormatModel.getRootNode().getName());
                        controller.utils.updateInfoLabel("Project Documentation - " + fileFormatModel.getRootNode().getName() + " - created successfully");
                    }
                });
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method opens a dialog when user tries to use key bindings
     * to create new project documentation
     */
    public Optional<ButtonType> showCreateNewOptionDialog() throws FileNotFoundException, MalformedURLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Create new");
        alert.setHeaderText("Select project type");

        ButtonType option1 = new ButtonType("C/C++ project");
        ButtonType option2 = new ButtonType("Java project");
        ButtonType option3 = new ButtonType("Python project");
        ButtonType cancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(option1, option2, option3, cancel);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("dialog-pane");

        return alert.showAndWait();
    }

    /**
     * This method refreshes and rebuilds the existing opened project
     */
    public void refreshProject() throws MalformedURLException {
        if (fileFormatModel.getRootNode().getFullPath() != null) {
            controller.explorer.updateTreeView(fileFormatModel.getRootNode());
        }
    }

    /**
     * This method saves the built project as a .doci file using the {@link com.daniel.docify.fileProcessor.FileSerializer}.
     */
    public void saveAsDociFile(){
        if (fileFormatModel.getRootNode().getName() != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Docify File");

            try {
                UserConfiguration.checkUserConfiguration();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File lastSavePath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastSaveConfig()));
            if (lastSavePath.exists() && !lastSavePath.getAbsolutePath().isEmpty()) {
                fileChooser.setInitialDirectory(lastSavePath);
            }

            FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
            FileChooser.ExtensionFilter docifyFilter = new FileChooser.ExtensionFilter("Docify File (*.doci)", "*.doci");
            fileChooser.getExtensionFilters().addAll(docifyFilter, allFilesFilter);

            File selectedDir = fileChooser.showSaveDialog(null);

            if (selectedDir != null) {
                System.out.println("Selected Directory " + selectedDir.getParent());
                UserConfiguration.saveUserLastSaveConfig(selectedDir.getParent());

                String finalPath = getFinalPath(selectedDir);

                // Java native serialization
                //fileFormatModel.save(fileFormatModel, finalPath);

                try (ExtendedFileOutputStream out = new ExtendedFileOutputStream(finalPath)){
                    fileFormatModel.serialize(out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                controller.utils.updateInfoLabel("File saved successfully");
            }
        }else {
            ControllerUtils.popUpAlert(Alert.AlertType.WARNING, "Warning", "No project opened to be saved");
        }
    }


    public void saveAsPDF() {
        ControllerUtils.popUpAlert(Alert.AlertType.INFORMATION, "Information",
                "PDF file will be supported in the upcoming release.");
    }

    @NotNull
    private static String getFinalPath(File selectedDir) {
        String finalPath;

        if (selectedDir.getAbsolutePath().endsWith(".doci")) {
            finalPath = selectedDir.getAbsolutePath();
        } else {
            finalPath = selectedDir.getAbsolutePath() + ".doci";
        }
        return finalPath;
    }

    public void saveDociFile() throws MalformedURLException {
        if (fileFormatModel.getRootNode() != null && fileFormatModel.getRootNode().getName() != null) {
            if (fileFormatModel.getSavedLocation() != null) {

                // Java native serialization
                //fileFormatModel.save(fileFormatModel, fileFormatModel.getSavedLocation());

                try (ExtendedFileOutputStream out = new ExtendedFileOutputStream(fileFormatModel.getSavedLocation())){
                    fileFormatModel.serialize(out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                controller.utils.updateInfoLabel("Project saved successfully!");
            }else {
                saveAsDociFile();
            }
        }
    }

    /**
     * This method opens a dialog when user tries to use key bindings
     * to save project as project documentation
     */
    public Optional<ButtonType> showSaveAsOptionDialog(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save as");
        alert.setHeaderText("Save as");

        ButtonType option1 = new ButtonType("Doci file");
        ButtonType option2 = new ButtonType("PDF file");
        ButtonType cancel = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(option1, option2, cancel);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("dialog-pane");

        return alert.showAndWait();
    }

    /**
     * This method opens saved .doci file using the {@link com.daniel.docify.fileProcessor.FileSerializer} and
     * it repopulates all necessary UI components.
     */
    public void openDociFile() throws MalformedURLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Docify File");

        try {
            UserConfiguration.checkUserConfiguration();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File lastSavePath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastSaveConfig()));
        if (lastSavePath.exists() && !lastSavePath.getAbsolutePath().isEmpty()) {
            fileChooser.setInitialDirectory(lastSavePath);
        }

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
                // Java native deserialization
                //fileFormatModel = fileFormatModel.load(selectedDir.getAbsolutePath());

                try (ExtendedFileInputStream in = new ExtendedFileInputStream(selectedDir.getAbsolutePath())){
                    fileFormatModel = FileFormatModel.deserialize(in);
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }

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
            DirectoryWatchService.stop();
            cleanTempFiles();
            fileFormatModel.clear();
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

    private void cleanTempFiles(){
        FileNodeModel rootNode = controller.menuActions.getFileFormatModel().getRootNode();
        if (rootNode != null && rootNode.getFullPath() != null) {
            File file = new File(rootNode.getFullPath() + "\\temp.ignore");
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("temp file cleaned");
                }
            }
        }
    }

    /**
     * This method is used to display the metadata of the file and
     * the software to the user.
     */
    public void displayMetaData() {
        if (fileFormatModel != null) {
            ControllerUtils.popUpAlert(Alert.AlertType.INFORMATION, "About",
                    (fileFormatModel.getAuthorName() == null ? "" : "author: " + fileFormatModel.getAuthorName()) +
                            (fileFormatModel.getFileFormatVersion() == null ? "" : "\nFile version: " + fileFormatModel.getFileFormatVersion()) +
                            (fileFormatModel.getCreationDate() == null ? "" : "\nDate and time: " + fileFormatModel.getCreationDate()) +
                            "\nSoftware version: Docify Studio v" + Main.SOFTWARE_VERSION
            );
        }else{
            ControllerUtils.popUpAlert(Alert.AlertType.INFORMATION, "About",
                            "Software version: Docify Studio v" + Main.SOFTWARE_VERSION
            );
        }
    }
}
