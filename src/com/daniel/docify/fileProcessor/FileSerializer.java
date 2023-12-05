package com.daniel.docify.fileProcessor;

import java.io.*;

/**
 * @brief   This class enables the system to save the documentation
 *          generated to a file of doci type, and allows to open
 *          this file and read the documentation again
 */
public class FileSerializer {

    /**
     * @brief   This method serializes the FileNodeModel object and saves it
     */
    public static void save(FileNodeModel fileNodeModel, String filePath) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            objectOutputStream.writeObject(fileNodeModel);
            System.out.println("Object saved successfully to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief   This method loads the serialized FileNodeModel object and deserializes it
     *          and returns the object
     */
    public static FileNodeModel load(String filePath) {
        FileNodeModel fileNodeModel = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            fileNodeModel = (FileNodeModel) objectInputStream.readObject();
            System.out.println("Object loaded successfully from " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return fileNodeModel;
    }
}