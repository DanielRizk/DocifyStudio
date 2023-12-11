package com.daniel.docify.core;

import com.daniel.docify.ui.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;

public class Main extends Application {

    public static final String VERSION = "Docify Studio v1.0";
    public static final boolean LOAD_ICONS = true;
    public static void main(String[] args){

        /* for debugging only */
        //MainWindow mainWindow = new MainWindow();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/daniel/docify/ui/MainWindowUI.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        primaryStage.setTitle("Docify Studio");

        try {
            File iconFile = new File("assets/icons/doci.png");
            String iconUrl = iconFile.toURI().toURL().toExternalForm();
            Image icon = new Image(iconUrl);
            primaryStage.getIcons().add(icon);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }

        Scene scene = new Scene(root);
        //scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("darkmode.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        controller.setStage(primaryStage);
        primaryStage.show();
    }
}
