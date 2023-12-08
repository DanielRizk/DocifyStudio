package com.daniel.docify.core;

import com.daniel.docify.testingUI.MainWindow;
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
        MainWindow mainWindow = new MainWindow();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/daniel/docify/ui/MainWindowUI.fxml")));
        primaryStage.setTitle("Docify Studio");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
