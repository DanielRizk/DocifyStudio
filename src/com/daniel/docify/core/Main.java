package com.daniel.docify.core;

import com.daniel.docify.fileProcessor.UserConfiguration;
import com.daniel.docify.ui.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    public static final String SOFTWARE_VERSION = "1.0";
    public static final boolean LOAD_ICONS = true;
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //URL url = getClass().getResource("../resources/ui/MainWindowUI.fxml");
        String relativePath = "resources/ui/MainWindowUI.fxml";
        Path path = Paths.get(relativePath).toAbsolutePath();
        URL url = path.toUri().toURL();
        System.out.println("FXML file located at: "+ url);
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        Controller controller = loader.getController();
        primaryStage.setTitle("Docify Studio");

        try {
            File iconFile = new File("resources/assets/icons/doci.png");
            String iconUrl = iconFile.toURI().toURL().toExternalForm();
            Image icon = new Image(iconUrl);
            primaryStage.getIcons().add(icon);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        controller.setStage(primaryStage);
        UserConfiguration.checkUserConfiguration();
        primaryStage.setOnCloseRequest(event -> {
            controller.menuActions.closeRoutine();
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }
}
