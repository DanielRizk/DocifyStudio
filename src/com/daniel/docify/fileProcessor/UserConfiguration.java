package com.daniel.docify.fileProcessor;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @brief   This class enable the system to save user configurations
 *          and preferences, and load them again when the system boots
 */
public class UserConfiguration {
    private String lastOpenDir;
    private String lastSaveDir;

    public String getLastOpenDir() {
        return this.lastOpenDir;
    }

    public void setLastOpenDir(String rootDir) {
        this.lastOpenDir = rootDir;
    }

    public String getLastSaveDir() {
        return this.lastSaveDir;
    }

    public void setLastSaveDir(String rootDir) {
        this.lastSaveDir = rootDir;
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

    /**
     * @brief   This method saves the last opened path from the user
     *          to the user config file
     */
    public static void saveUserLastOpenConfig(String lastOpenDir) {
        try {
            // Read the existing configuration from the file
            UserConfiguration userConfiguration = loadUserConfiguration();

            // Update the last open directory
            userConfiguration.setLastOpenDir(lastOpenDir);

            // Save the updated configuration back to the file
            saveUserConfiguration(userConfiguration);

            System.out.println("UserConfiguration saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief   This method loads the last opened path from the user
     *          to the user config file
     */
    public static String loadUserLastOpenConfig() {
        try {
            // Read the existing configuration from the file
            UserConfiguration userConfiguration = loadUserConfiguration();

            // Use the last open directory from the configuration
            return userConfiguration.getLastOpenDir();
        } catch (FileNotFoundException e) {
            // Handle the FileNotFoundException
            System.out.println("Configuration file not found. Using default configuration.");
            return null; // Provide a default configuration or null
        } catch (IOException e) {
            // Handle other IOExceptions
            e.printStackTrace();
            System.err.println("Error loading user configuration: " + e.getMessage());
            return null;
        }
    }

    /**
     * @brief   This method saves the last saved path from the user
     *          to the user config file
     */
    public static void saveUserLastSaveConfig(String lastSaveDir) {
        try {
            // Read the existing configuration from the file
            UserConfiguration userConfiguration = loadUserConfiguration();

            // Update the last save directory
            userConfiguration.setLastSaveDir(lastSaveDir);

            // Save the updated configuration back to the file
            saveUserConfiguration(userConfiguration);

            System.out.println("UserConfiguration saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief   This method loads the last saved path from the user
     *          to the user config file
     */
    public static String loadUserLastSaveConfig() {
        try {
            // Read the existing configuration from the file
            UserConfiguration userConfiguration = loadUserConfiguration();

            // Use the last save directory from the configuration
            return userConfiguration.getLastSaveDir();
        } catch (FileNotFoundException e) {
            // Handle the FileNotFoundException
            System.out.println("Configuration file not found. Using default configuration.");
            return null; // Provide a default configuration or null
        } catch (IOException e) {
            // Handle other IOExceptions
            e.printStackTrace();
            System.err.println("Error loading user configuration: " + e.getMessage());
            return null;
        }
    }

    private static UserConfiguration loadUserConfiguration() throws IOException {
        try (FileReader reader = new FileReader("config/config.json")) {
            Gson gson = new Gson();
            return gson.fromJson(reader, UserConfiguration.class);
        }
    }

    private static void saveUserConfiguration(UserConfiguration userConfiguration) throws IOException {
        try (FileWriter writer = new FileWriter("config/config.json")) {
            writer.append(userConfiguration.toJson());
        }
    }
}
