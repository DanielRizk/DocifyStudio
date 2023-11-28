package com.daniel.docify.fileProcessor;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserConfiguration {

    @SerializedName("lastOpenDir")
    private String lastOpenDir;

    @SerializedName("lastSaveDir")
    private String lastSaveDir;


    public UserConfiguration(String rootDir) {
        this.lastOpenDir = rootDir;
        this.lastSaveDir = rootDir;
    }

    public String getLastOpenDir() {
        return lastOpenDir;
    }

    public String getLastSaveDir() {
        return lastSaveDir;
    }

    public void setRootDir(String rootDir) {
        this.lastOpenDir = rootDir;
    }

    // Convert the object to JSON
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Create a UserConfiguration object from JSON
    public static UserConfiguration fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, UserConfiguration.class);
    }

    public static void saveUserLastOpenConfig(String lastOpenDir) {
        try {
            // Create a UserConfiguration object
            UserConfiguration userConfiguration = new UserConfiguration(lastOpenDir);

            // Write the object to a JSON file
            try (FileWriter writer = new FileWriter("config/config.json")) {
                writer.write(userConfiguration.toJson());
            }

            System.out.println("UserConfiguration saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadUserLastOpenConfig() {
        try {
            // Read the JSON file and convert to a UserConfiguration object
            try (FileReader reader = new FileReader("config/config.json")) {
                Gson gson = new Gson();
                UserConfiguration userConfiguration = gson.fromJson(reader, UserConfiguration.class);

                // Use the userConfiguration object
                System.out.println("Root Directory: " + userConfiguration.getLastOpenDir());
                return userConfiguration.getLastOpenDir();
            }

        } catch (FileNotFoundException e) {
            // Handle the FileNotFoundException
            System.err.println("Configuration file not found. Using default configuration.");
            return null; // Provide a default configuration or null
        } catch (IOException e) {
            // Handle other IOExceptions
            e.printStackTrace();
            System.err.println("Error loading user configuration: " + e.getMessage());
            return null;
        }
    }

    public static void saveUserLastSaveConfig(String lastSaveDir) {
        try {
            // Create a UserConfiguration object
            UserConfiguration userConfiguration = new UserConfiguration(lastSaveDir);

            // Write the object to a JSON file
            try (FileWriter writer = new FileWriter("config/config.json")) {
                writer.write(userConfiguration.toJson());
            }

            System.out.println("UserConfiguration saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadUserLastSaveConfig() {
        try {
            // Read the JSON file and convert to a UserConfiguration object
            try (FileReader reader = new FileReader("config/config.json")) {
                Gson gson = new Gson();
                UserConfiguration userConfiguration = gson.fromJson(reader, UserConfiguration.class);

                // Use the userConfiguration object
                System.out.println("Root Directory: " + userConfiguration.getLastOpenDir());
                return userConfiguration.getLastSaveDir();
            }

        } catch (FileNotFoundException e) {
            // Handle the FileNotFoundException
            System.err.println("Configuration file not found. Using default configuration.");
            return null; // Provide a default configuration or null
        } catch (IOException e) {
            // Handle other IOExceptions
            e.printStackTrace();
            System.err.println("Error loading user configuration: " + e.getMessage());
            return null;
        }
    }
}
