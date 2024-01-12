package com.daniel.docify.ui.components;

import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.ui.Controller;
import com.daniel.docify.ui.utils.ControllerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * This Class represents the right pane of the UI, and it contains file specific info
 * such as the names of all components contained within the file.*/

public class FileDocContent extends ControllerUtils {

    public FileDocContent(Controller controller){
        super(controller);
    }

    /**
     * This method generates a list of all items names contained
     * in a file, gets triggered whenever a file is selected from
     * the tree view or the list view.
     */
    public void updateFileContentListView(FileInfoModel fileInfoModel){
        controller.getFileContentListView().getItems().clear();
        ObservableList<FileInfoModel.ItemNameAndProperty> fileContentList = FXCollections.observableArrayList();
        fileContentList.clear();
        if(fileInfoModel != null){
            fileContentList.addAll(fileInfoModel.getItemNames());
        }
        controller.getFileContentListView().getItems().addAll(fileContentList);
    }

    /**
     * This method initializes the fileContentListView and assigns cell factory
     * in order to stylize the text and add an icon to each entry according to
     * certain rules.
     */
    public void initializeFileContentListView(){
        controller.getFileContentListView().setCellFactory(new Callback<ListView<FileInfoModel.ItemNameAndProperty>, ListCell<FileInfoModel.ItemNameAndProperty>>() {
            @Override
            public ListCell<FileInfoModel.ItemNameAndProperty> call(ListView<FileInfoModel.ItemNameAndProperty> listView) {
                return new FileDocContent.FileContentItemCell();
            }
        });
    }

    /**
     * This subclass assists the cell factory of the fileContentListView, and it contains the
     * logic behind styling the cells.
     */
    private static class FileContentItemCell extends ListCell<FileInfoModel.ItemNameAndProperty> {
        private final ImageView imageView = new ImageView();
        private final Text text = new Text();

        @Override
        protected void updateItem(FileInfoModel.ItemNameAndProperty item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                text.setText(item.toString());
                switch (item.getType()) {
                    case FileInfoModel.ObjectType.EXTREN:
                        try {
                            imageView.setImage(setIconForList("assets/icons/clang_extern.png"));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        text.setFill(Color.web("#574e3b"));
                        break;
                    case FileInfoModel.ObjectType.MACRO:
                        try {
                            imageView.setImage(setIconForList("assets/icons/clang_macro.png"));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        text.setFill(Color.web("#215973"));
                        break;
                    case FileInfoModel.ObjectType.STATIC:
                        try {
                            imageView.setImage(setIconForList("assets/icons/clang_staticVar.png"));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        text.setFill(Color.web("#a86900"));
                        break;
                    case FileInfoModel.ObjectType.ENUM:
                        try {
                            imageView.setImage(setIconForList("assets/icons/clang_enum.png"));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        text.setFill(Color.web("#13522d"));
                        break;
                    case FileInfoModel.ObjectType.STRUCT:
                        try {
                            imageView.setImage(setIconForList("assets/icons/clang_struct.png"));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        text.setFill(Color.web("#5e1c12"));
                        break;
                    case FileInfoModel.ObjectType.FUNCTION:
                        try {
                            imageView.setImage(setIconForList("assets/icons/clang_function.png"));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        text.setFill(Color.web("#541f80"));
                        break;
                    default:
                        try {
                            imageView.setImage(setIconForList("assets/icons/cprog.png"));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        text.setFill(Color.BLACK);
                        break;
                }

                imageView.setFitHeight(20.0);
                imageView.setFitWidth(20.0);
                HBox cellBox = new HBox(imageView, text);
                cellBox.setSpacing(10);
                setGraphic(cellBox);
            }
        }

        /**
         * This method is a helper method used to load an icon and returns
         * an Image object.
         */
        private Image setIconForList(String iconPath) throws FileNotFoundException {
            FileInputStream input = new FileInputStream(iconPath);
            return new Image(input);
        }
    }


}
