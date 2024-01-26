package com.daniel.docify.fileProcessor;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @brief   This class enable the system to save user configurations
 *          and preferences, and load them again when the system boots
 */
public class UserConfiguration {

    /** JSON elements */
    private String lastOpenDir;
    private String lastSaveDir;

    private static final Logger LOGGER = Logger.getLogger(UserConfiguration.class.getName());

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
            // Handle other IOExceptions
            LOGGER.log(Level.SEVERE, "Error loading user configuration.", e);
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
            LOGGER.log(Level.WARNING, "Configuration file not found. Using default configuration.", e);
            return null; // Provide a default configuration or null
        } catch (IOException e) {
            // Handle other IOExceptions
            LOGGER.log(Level.SEVERE, "Error loading user configuration.", e);
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
            // Handle other IOExceptions
            LOGGER.log(Level.SEVERE, "Error loading user configuration.", e);
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
            LOGGER.log(Level.WARNING, "Configuration file not found. Using default configuration.", e);
            return null; // Provide a default configuration or null
        } catch (IOException e) {
            // Handle other IOExceptions
            LOGGER.log(Level.SEVERE, "Error loading user configuration.", e);
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

    public static void checkUserConfiguration() throws IOException {
        File configFile = new File("config/config.json");
        File configDir = new File("config");

        // Ensure the config directory exists
        if (!configDir.exists()) {
            if (!configDir.mkdir()) {
                throw new IOException("Failed to create configuration directory.");
            }
        }

        UserConfiguration configuration;
        if (!configFile.exists()) {
            if (configFile.createNewFile()) {
                System.out.println("User configuration created successfully.");
                configuration = new UserConfiguration();
                configuration.setLastSaveDir("");
                configuration.setLastOpenDir("");
                saveUserConfiguration(configuration);
            } else {
                throw new IOException("Could not create User configuration file.");
            }
        } else {
            configuration = loadUserConfiguration();
            if (configuration == null) {
                configuration = new UserConfiguration();
                configuration.setLastOpenDir("");
                configuration.setLastSaveDir("");
            }
            if (configuration.getLastOpenDir() == null) {
                configuration.setLastOpenDir("");
            } else if (configuration.getLastSaveDir() == null) {
                configuration.setLastSaveDir("");
            }
            saveUserConfiguration(configuration);
        }
    }

}
