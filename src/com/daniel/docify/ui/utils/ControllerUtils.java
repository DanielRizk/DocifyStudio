package com.daniel.docify.ui.utils;

import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.ui.Controller;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class ControllerUtils {

    protected Controller controller;
    private boolean isGetFromSearchResultCalled = false;
    private ControllerUtils.SearchResultModel currentSelectedItem;

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
        controller.getInfoLabel().setText(initialValue);
        Duration initialDuration = Duration.seconds(3);
        Timeline timeline = new Timeline(
                new KeyFrame(initialDuration, event -> controller.getInfoLabel().setText(""))
        );
        timeline.play();
    }

    public String readFileToString(String path) throws IOException {
        try (InputStream is = new FileInputStream(path)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }catch (FileNotFoundException e){
            Platform.runLater(()->{
                popUpAlert(Alert.AlertType.ERROR,"Resource missing", "Failed to locate resource: " + path);
            });
            throw new FileNotFoundException();
        }
    }

    private Set<SearchResultModel> searchList(String searchWord){
        ObservableList<FileNodeModel> allFiles = controller.explorer.updateExplorerListView();
        Set<SearchResultModel> searchResults = new TreeSet<>();

        for (FileNodeModel file : allFiles) {
            if (file.getFileInfo() != null) {
                for (FileInfoModel.ItemNameAndProperty itemName : file.getFileInfo().getItemNames()) {
                    if (isMatch(itemName.toString(), searchWord)) {
                        searchResults.add(new SearchResultModel(itemName, file));
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
                controller.mainWindow.getDocumentationView().getEngine().executeScript("highlightSearch('" + escapeJavaScriptString(selectedItem) + "')");
            } catch (JSException e) {
                Controller.LOGGER.log(Level.SEVERE, "Error executing highlightSearch script", e);
            }
        }
    }

    private String escapeJavaScriptString(String str) {
        return str.replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
    }
    public void searchAndDisplay(){
        String searchKeyword = controller.getSearchBar().getText();
        if (searchKeyword != null) {

            Set<SearchResultModel> result = searchList(searchKeyword);

            if (!result.isEmpty()){
                controller.mainWindow.getDocumentationView().getEngine().loadContent("");
                controller.getSearchResultListView().getItems().clear();
                controller.getFileContentListView().getItems().clear();
                controller.mainWindow.getDocumentationView().setVisible(false);
                controller.getSearchResultListView().getItems().addAll(result);
                controller.getSearchResultListView().setVisible(true);
                updateInfoLabel(result.size()+" records found");
            }
            else{
                updateInfoLabel("No results found!");
            }
        }
    }

    public static void popUpAlert(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void getFromSearchResult(){
        isGetFromSearchResultCalled = true;
        currentSelectedItem = controller.getSearchResultListView().getSelectionModel().getSelectedItem();

        if (controller.getSearchResultListView().getSelectionModel().getSelectedItem() != null) {
            controller.mainWindow.getDocumentationView().getEngine().loadContent("");
            controller.mainWindow.compileWebViewDisplay(currentSelectedItem.getParentFileNode().getFileInfo());
        }
        controller.getSearchResultListView().getItems().clear();
        controller.getSearchResultListView().setVisible(false);
        controller.mainWindow.getDocumentationView().setVisible(true);

        controller.mainWindow.getDocumentationView().getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // Now that the page has loaded, we can highlight the search term
                if (currentSelectedItem != null &&
                        !currentSelectedItem.toString().isEmpty() &&
                        isGetFromSearchResultCalled){
                    scrollToLine(currentSelectedItem.toString());
                    isGetFromSearchResultCalled = false;
                }
            }
        });

    }

    public record SearchResultModel(FileInfoModel.ItemNameAndProperty item, FileNodeModel fileNodeModel) implements Comparable<SearchResultModel>{
        public FileNodeModel getParentFileNode() {
            return fileNodeModel;
        }

        public FileInfoModel.ItemNameAndProperty getNameAndProperty(){
            return item;
        }
        @Override
        public String toString() {
            return item.toString();
        }

        @Override
        public int compareTo(@NotNull ControllerUtils.SearchResultModel o) {
            // Check if 'o' is an instance of SearchResultModel without pattern matching
            // Explicitly cast 'o' to SearchResultModel
            return this.item.toString().compareTo(((SearchResultModel) o).item.toString());
            // Additional comparison logic can be added here
        }
    }
}
