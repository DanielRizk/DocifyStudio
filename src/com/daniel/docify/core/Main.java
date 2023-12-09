package com.daniel.docify.core;

import com.daniel.docify.testingUI.MainWindow;
import com.daniel.docify.ui.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;

public class Main extends Application {
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
        primaryStage.setScene(new Scene(root, 800, 600));
        controller.setStage(primaryStage);
        primaryStage.show();
    }
}
