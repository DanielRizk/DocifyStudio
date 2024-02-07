package com.daniel.docify.model;

import com.daniel.docify.core.Main;
import com.daniel.docify.fileProcessor.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.daniel.docify.ui.utils.ControllerUtils.popUpAlert;

public class FileFormatModel extends FileSerializer implements DociSerializable, Serializable {

    public static final String FILE_FORMAT_VERSION = "1.0";

    /* Metadata objects */
    private String authorName;
    private String creationDate;
    private String fileFormatVersion;
    private String softwareVersion;
    private String savedLocation;

    private FileNodeModel rootNode;

    public FileFormatModel(FileNodeModel rootNode){
        this.rootNode = rootNode;
    }

    public String getSavedLocation() {
        return savedLocation;
    }

    @Metadata
    public void setSavedLocation(String savedLocation) {
        this.savedLocation = savedLocation;
    }

    public FileNodeModel getRootNode() {
        return rootNode;
    }

    @Metadata
    public void setRootNode(FileNodeModel rootNode) {
        this.rootNode = rootNode;
    }

    public String getAuthorName() {
        return authorName;
    }

    @Metadata
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    @Metadata
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getFileFormatVersion() {
        return fileFormatVersion;
    }

    @Metadata
    public void setFileFormatVersion(String fileFormatVersion) {
        this.fileFormatVersion = fileFormatVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    @Metadata
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public void clear() {
        Class<?> clazz = this.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Metadata.class)) {
                try {
                    method.invoke(this, new Object[]{null});
                } catch (Exception e) {
                    throw new RuntimeException("Could not clear FileFormatModel. " + e);
                }
            }
        }
    }

    private void prepareFileFormatMetaData(String filePath){
        setAuthorName(System.getProperty("user.name"));
        setFileFormatVersion(FileFormatModel.FILE_FORMAT_VERSION);
        setCreationDate(getCurrentDateAndTime());
        setSoftwareVersion(Main.SOFTWARE_VERSION);
        setSavedLocation(filePath);
    }

    private String getCurrentDateAndTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return currentDateTime.format(formatter);
    }

    @Override
    public void serialize(ExtendedFileOutputStream out) throws IOException {
        prepareFileFormatMetaData(out.getFilePath());

        out.writeValidationKey(VALIDATION_KEY);
        out.writeStringFieldMappingToStream(FILE_FORMAT_MODEL_TAG, "");
        out.writeStringFieldMappingToStream(FILE_VERSION_TAG, getFileFormatVersion());
        out.writeStringFieldMappingToStream(SOFTWARE_VERSION_TAG, getSoftwareVersion());
        out.writeStringFieldMappingToStream(AUTHOR_NAME_TAG, getAuthorName());
        out.writeStringFieldMappingToStream(CREATION_DATE_TAG, getCreationDate());
        out.writeStringFieldMappingToStream(SAVED_LOCATION_TAG, getSavedLocation());
        out.writeStringFieldMappingToStream(FILE_NODE_MODEL_TAG, "");
        getRootNode().serialize(out);

    }

    public static FileFormatModel deserialize(ExtendedFileInputStream in) throws IOException {
        FileFormatModel formatModel = new FileFormatModel(new FileNodeModel(null, null, false, null));
        TagDataPair pair;

        if (!Objects.equals(in.readValidationKey(), VALIDATION_KEY)){
            Platform.runLater(() ->{
                popUpAlert(Alert.AlertType.WARNING,
                        "File incompatibility",
                        "The file you are trying to open is corrupted");
            });
            throw new IncompatibleClassChangeError("File format is incompatible");
        }

        // validate the first tag is FILE_FORMAT_MODEL_TAG 0x10
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() != FILE_FORMAT_MODEL_TAG){
            Platform.runLater(() ->{
                popUpAlert(Alert.AlertType.WARNING,
                        "File incompatibility",
                        "The file you are trying to open is corrupted");
            });
            throw new IncompatibleClassChangeError("File format is incompatible");
        }

        // validate the second tag is FILE_VERSION_TAG 0x11
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == FILE_VERSION_TAG){
            formatModel.setFileFormatVersion(pair.SData());
            if (!Objects.equals(pair.SData(), FILE_FORMAT_VERSION)){
                TagDataPair finalPair = pair;
                Platform.runLater(() ->{
                    popUpAlert(Alert.AlertType.WARNING,
                            "File version incompatibility",
                            "The file you are trying to open is from an older version "+
                            finalPair.SData() + " (Latest is " + FILE_FORMAT_VERSION + ") and might cause " +
                                    "incompatibility issues");
                });
            throw new IncompatibleClassChangeError("File format is incompatible");
            }
        }

        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == SOFTWARE_VERSION_TAG){
            formatModel.setSoftwareVersion(pair.SData());
        }

        // read author name
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == AUTHOR_NAME_TAG){
            formatModel.setAuthorName(pair.SData());
        }

        // read creation date
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == CREATION_DATE_TAG){
            formatModel.setCreationDate(pair.SData());
        }

        // read saved location date
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == SAVED_LOCATION_TAG){
            formatModel.setSavedLocation(pair.SData());
        }

        // read saved location date
        pair = in.readStringFieldMappingFromStream();
        if (pair.tag() == FILE_NODE_MODEL_TAG){
            formatModel.setRootNode(FileNodeModel.deserialize(in));
        }

        return formatModel;
    }
}
