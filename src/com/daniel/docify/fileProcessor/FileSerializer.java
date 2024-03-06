package com.daniel.docify.fileProcessor;

import com.daniel.docify.core.Main;
import com.daniel.docify.model.FileFormatModel;
import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.ui.utils.ControllerUtils;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @brief   This class enables the system to save the documentation
 *          generated to a file of doci type, and allows to open
 *          this file and read the documentation again
 */
public class FileSerializer {

    private static final Logger LOGGER = Logger.getLogger(FileSerializer.class.getName());

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String SECRET_KEY = "IbLaOtVaEbAeWtIo"; // Replace with your secret key

    /**
     * @brief   This method serializes the FileNodeModel object, encrypts it, and saves it
     */
    public void save(FileFormatModel formatModel, String filePath) {
        formatModel.setAuthorName(System.getProperty("user.name"));
        formatModel.setFileFormatVersion(FileFormatModel.FILE_FORMAT_VERSION);
        formatModel.setCreationDate(getCurrentDateAndTime());
        formatModel.setSoftwareVersion(Main.SOFTWARE_VERSION);
        formatModel.setSavedLocation(filePath);

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new CipherOutputStream(new FileOutputStream(filePath), createCipher(Cipher.ENCRYPT_MODE)))) {
            objectOutputStream.writeObject(formatModel);
            System.out.println("Object saved successfully to " + filePath);
        } catch (IOException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "Error saving object.", e);
        }
    }

    /**
     * @brief   This method loads the encrypted FileNodeModel object, decrypts it, and returns the object
     */
    public FileFormatModel load(String filePath) {
        FileFormatModel formatModel = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(
                new CipherInputStream(new FileInputStream(filePath), createCipher(Cipher.DECRYPT_MODE)))) {

            Object obj = objectInputStream.readObject();

            if (obj instanceof FileFormatModel) {
                formatModel = (FileFormatModel) obj;
                System.out.println("Object loaded successfully from " + filePath);
            } else {
                // Handle the case where obj is not a FileFormatModel
                String errorMessage = "Incompatible object type: expected FileFormatModel, found "
                        + obj.getClass().getName();
                LOGGER.log(Level.SEVERE, errorMessage);
                Platform.runLater(() -> {
                    ControllerUtils.popUpAlert(Alert.AlertType.ERROR,
                            "Loading file", "The file you are trying to open is not " +
                                    "compatible with this version of Docify Studio v"+ Main.SOFTWARE_VERSION);
                });
                return null;
            }

        } catch (IOException | ClassNotFoundException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "Error loading object.");
            Platform.runLater(() -> {
                ControllerUtils.popUpAlert(Alert.AlertType.ERROR,
                        "Loading file", "The file you are trying to open is not " +
                                "compatible with this version of "+ Main.SOFTWARE_VERSION);
            });
        }
        return formatModel;
    }

    private Cipher createCipher(int mode) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(mode, generateSecretKey());
        return cipher;
    }

    private SecretKeySpec generateSecretKey() {
        byte[] key = SECRET_KEY.getBytes();
        return new SecretKeySpec(key, ENCRYPTION_ALGORITHM);
    }

    private String getCurrentDateAndTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return currentDateTime.format(formatter);
    }
}
