package com.daniel.docify.fileProcessor;

import com.daniel.docify.model.FileNodeModel;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.GeneralSecurityException;
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
    public static void save(FileNodeModel fileNodeModel, String filePath) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new CipherOutputStream(new FileOutputStream(filePath), createCipher(Cipher.ENCRYPT_MODE)))) {
            objectOutputStream.writeObject(fileNodeModel);
            System.out.println("Object saved successfully to " + filePath);
        } catch (IOException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "Error saving object.", e);
        }
    }

    /**
     * @brief   This method loads the encrypted FileNodeModel object, decrypts it, and returns the object
     */
    public static FileNodeModel load(String filePath) {
        FileNodeModel fileNodeModel = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(
                new CipherInputStream(new FileInputStream(filePath), createCipher(Cipher.DECRYPT_MODE)))) {
            fileNodeModel = (FileNodeModel) objectInputStream.readObject();
            System.out.println("Object loaded successfully from " + filePath);
        } catch (IOException | ClassNotFoundException | GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "Error loading object.", e);
        }
        return fileNodeModel;
    }

    private static Cipher createCipher(int mode) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(mode, generateSecretKey());
        return cipher;
    }

    private static SecretKeySpec generateSecretKey() {
        byte[] key = SECRET_KEY.getBytes();
        return new SecretKeySpec(key, ENCRYPTION_ALGORITHM);
    }
}
