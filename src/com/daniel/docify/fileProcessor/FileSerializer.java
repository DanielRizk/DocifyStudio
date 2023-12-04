package com.daniel.docify.fileProcessor;



import java.io.*;

public class FileSerializer {

    public static void save(FileNodeModel fileNodeModel, String filePath) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            objectOutputStream.writeObject(fileNodeModel);
            System.out.println("Object saved successfully to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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