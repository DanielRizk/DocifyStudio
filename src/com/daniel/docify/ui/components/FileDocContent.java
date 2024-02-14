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
import java.util.HashMap;
import java.util.Map;

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
        private final HBox cellBox = new HBox(imageView, text); // Pre-create HBox
        private static final Map<String, Image> iconCache = new HashMap<>();

        private static final String ICON_EXTERN     = "resources/assets/icons/clang_extern.png";
        private static final String ICON_MACRO      = "resources/assets/icons/clang_macro.png";
        private static final String ICON_STATIC_VAR = "resources/assets/icons/clang_staticVar.png";
        private static final String ICON_ENUM       = "resources/assets/icons/clang_enum.png";
        private static final String ICON_STRUCT     = "resources/assets/icons/clang_struct.png";
        private static final String ICON_FUNCTION   = "resources/assets/icons/clang_function.png";
        private static final String ICON_DEFAULT    = "resources/assets/icons/cprog.png";

        static {
            // Load specific icons if they are not loaded already in the cache
            loadIcon(ICON_EXTERN);
            loadIcon(ICON_MACRO);
            loadIcon(ICON_STATIC_VAR);
            loadIcon(ICON_ENUM);
            loadIcon(ICON_STRUCT);
            loadIcon(ICON_FUNCTION);
            loadIcon(ICON_DEFAULT);
        }

        private static void loadIcon(String path) {
            try {
                iconCache.put(path, new Image(new FileInputStream(path)));
            } catch (FileNotFoundException e) {
                System.err.println("Icon file not found: " + path);
            }
        }

        @Override
        protected void updateItem(FileInfoModel.ItemNameAndProperty item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                text.setText(item.toString());
                setTextColorAndIcon(item);

                imageView.setFitHeight(20.0);
                imageView.setFitWidth(20.0);
                cellBox.setSpacing(10);
                setGraphic(cellBox);
            }
        }

        private void setTextColorAndIcon(FileInfoModel.ItemNameAndProperty item) {
            Image icon;
            switch (item.getType()) {
                case EXTREN:
                    icon = iconCache.get(ICON_EXTERN);
                    text.setFill(Color.web("#574e3b"));
                    break;
                case MACRO:
                    icon = iconCache.get(ICON_MACRO);
                    text.setFill(Color.web("#215973"));
                    break;
                case STATIC:
                    icon = iconCache.get(ICON_STATIC_VAR);
                    text.setFill(Color.web("#a86900"));
                    break;
                case ENUM:
                    icon = iconCache.get(ICON_ENUM);
                    text.setFill(Color.web("#13522d"));
                    break;
                case STRUCT:
                    icon = iconCache.get(ICON_STRUCT);
                    text.setFill(Color.web("#5e1c12"));
                    break;
                case FUNCTION:
                    icon = iconCache.get(ICON_FUNCTION);
                    text.setFill(Color.web("#541f80"));
                    break;
                default:
                    icon = iconCache.get(ICON_DEFAULT);
                    text.setFill(Color.BLACK);
                    break;
            }
            if (icon != null) {
                imageView.setImage(icon);
            }
        }
    }
}

