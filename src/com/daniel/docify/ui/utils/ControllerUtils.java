package com.daniel.docify.ui.utils;

import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.ui.Controller;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import netscape.javascript.JSException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ControllerUtils {

    protected Controller controller;

    public ControllerUtils (Controller controller){
        this.controller = controller;
    }

    public void setIcon(MenuItem menuItem, String iconPath) throws MalformedURLException {
        File iconFile = new File(iconPath);
        String iconUrl = iconFile.toURI().toURL().toExternalForm();
        ImageView imageView = new ImageView(new Image(iconUrl));
        imageView.setFitHeight(20.0);
        imageView.setFitWidth(20.0);
        menuItem.setGraphic(imageView);
    }

    private void setIcon(Menu menu, String iconPath) throws MalformedURLException {

        var iconFile = new File(iconPath);
        String iconUrl = iconFile.toURI().toURL().toExternalForm();
        ImageView imageView = new ImageView(new Image(iconUrl));
        imageView.setFitHeight(20.0);
        imageView.setFitWidth(20.0);
        menu.setGraphic(imageView);
    }

    protected Node setIcon(String iconPath) throws MalformedURLException {

        var iconFile = new File(iconPath);
        String iconUrl = iconFile.toURI().toURL().toExternalForm();
        ImageView imageView = new ImageView(new Image(iconUrl));
        imageView.setFitHeight(20.0);
        imageView.setFitWidth(20.0);

        return imageView;
    }

    public void updateInfoLabel(String initialValue){
        controller.infoLabel.setText(initialValue);
        Duration initialDuration = Duration.seconds(3);
        Timeline timeline = new Timeline(
                new KeyFrame(initialDuration, event -> controller.infoLabel.setText(""))
        );
        timeline.play();
    }

    public String readFileToString(String path) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private List<SearchResultModel> searchList(String searchWord){
        ObservableList<FileNodeModel> allFiles = controller.explorer.updateFilteredListView();
        List<SearchResultModel> searchResults = new ArrayList<>();

        for (FileNodeModel file : allFiles) {
            if (file.getFileInfo() != null) {
                for (FileInfoModel.ItemNameAndProperty itemName : file.getFileInfo().getItemNames()) {
                    if (isMatch(itemName.toString(), searchWord)) {
                        searchResults.add(new SearchResultModel(itemName.toString(), file));
                    }
                }
            }
        }
        return searchResults;
    }

    private boolean isMatch(String itemName, String searchWord) {
        List<String> result = Arrays.asList(searchWord.trim().split(" "));
        return result.stream().allMatch(word ->
                itemName.toLowerCase().contains(word.toLowerCase()));
    }



    public void scrollToLine(String selectedItem) {
        if (selectedItem != null && !selectedItem.isEmpty()) {
            try {
                controller.mainWindow.documentationView.getEngine().executeScript("highlightSearch('" + escapeJavaScriptString(selectedItem) + "')");
            } catch (JSException e) {
                Controller.LOGGER.log(Level.SEVERE, "Error executing highlightSearch script", e);
            }
        }
    }

    private String escapeJavaScriptString(String str) {
        return str.replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
    }
    public void searchAndDisplay(){
        String searchKeyword = controller.searchBar.getText();
        if (searchKeyword != null) {

            List<SearchResultModel> result = searchList(searchKeyword);

            if (!result.isEmpty()){
                controller.mainWindow.documentationView.getEngine().loadContent("");
                controller.searchResultListView.getItems().clear();
                controller.fileContentListView.getItems().clear();
                controller.mainWindow.documentationView.setVisible(false);
                controller.searchResultListView.getItems().addAll(result);
                controller.searchResultListView.setVisible(true);
                updateInfoLabel(result.size()+" records found");
            }
            else{
                updateInfoLabel("No results found!");
            }
        }
    }

    public void popUpAlert(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);

        // Show the alert dialog
        alert.showAndWait();

    }

    public void getFromSearchResult(){
        ControllerUtils.SearchResultModel selectedItem = controller.searchResultListView.getSelectionModel().getSelectedItem();

        if (controller.searchResultListView.getSelectionModel().getSelectedItem() != null) {
            controller.mainWindow.documentationView.getEngine().loadContent("");
            controller.mainWindow.compileWebViewDisplay(selectedItem.getParentFileNode().getFileInfo());
        }
        controller.searchResultListView.getItems().clear();
        controller.searchResultListView.setVisible(false);
        controller.mainWindow.documentationView.setVisible(true);

        controller.mainWindow.documentationView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // Now that the page has loaded, we can highlight the search term
                if (selectedItem != null) scrollToLine(selectedItem.toString());
            }
        });
    }

    public record SearchResultModel(String itemName, FileNodeModel fileNodeModel) {
        public FileNodeModel getParentFileNode() {
            return fileNodeModel;
        }
        @Override
        public String toString() {
            return itemName;
        }
    }
}
