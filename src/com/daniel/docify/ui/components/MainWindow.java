package com.daniel.docify.ui.components;

import com.daniel.docify.component.Clang.*;
import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.model.fileInfo.CFileInfo;
import com.daniel.docify.model.fileInfo.JavaFileInfo;
import com.daniel.docify.model.fileInfo.PythonFileInfo;
import com.daniel.docify.ui.Controller;
import com.daniel.docify.ui.utils.ControllerUtils;
import com.sun.javafx.webkit.WebConsoleListener;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * This class represents the main window of the software, and it is responsible for displaying the
 * documentation of the file and also the raw code.
 */
public class MainWindow extends ControllerUtils {

    private final StringBuilder htmlContent = new StringBuilder();
    private final WebView documentationView = new WebView();
    public FileInfoModel fileInfoBuff = null;
    private int id = 0; /* unique id for each collapsible box */
    public MainWindow(Controller controller) {
        super(controller);
    }

    /* getter methods for private variables */
    public WebView getDocumentationView(){
        return documentationView;
    }


    /**
     * This method is responsible for preparing and building the HTML content
     * of the WebView Object to compile it, this method is triggered whenever
     * a file is selected either from the TreeView or the ListView.
     */
    public void compileWebViewDisplay(FileInfoModel fileInfo) {

        fileInfoBuff = fileInfo;

        controller.getSearchResultListView().setVisible(false);
        controller.getSearchResultListView().getItems().clear();
        documentationView.getEngine().loadContent("");
        documentationView.setVisible(true);

        String pageHtmlStyling = htmlContent.toString();
        StringBuilder dynamicHtmlContent;

        if (fileInfo != null) {

            controller.fileRawCode.getCodeView().clear();
            controller.fileRawCode.getCodeView().replaceText(0, 0, fileInfo.getFileContent());
            controller.fileRawCode.getCodeView().showParagraphAtTop(0);
            controller.getPrimaryStage().setTitle("Docify Studio - " + controller.menuActions.getFileFormatModel().getRootNode().getName() + " - " + ((CFileInfo) fileInfo).getFileName());

            if (fileInfo.getHtmlContent() == null){

                dynamicHtmlContent = constructFileInfoHTML(fileInfo);

                if (dynamicHtmlContent.isEmpty()){
                    dynamicHtmlContent.append("<div class='noContentFound'><b>No documentation was detected for this file<b></div>");
                }

                String finalPage = pageHtmlStyling.replace("<!-- dynamic HTML content placeholder -->", dynamicHtmlContent.toString());
                fileInfo.setHtmlContent(finalPage);
            }

            documentationView.getEngine().loadContent(fileInfo.getHtmlContent());
            controller.docContent.updateFileContentListView(fileInfo);
            id = 0;
        }else {
            controller.getPrimaryStage().setTitle("Docify Studio - " + controller.menuActions.getFileFormatModel().getRootNode().getName());
            controller.fileRawCode.getCodeView().clear();
            controller.getFileContentListView().getItems().clear();
            updateInfoLabel("File has no documentation!");
        }
    }

    private StringBuilder constructFileInfoHTML(FileInfoModel fileInfo){
        new StringBuilder();

        return switch (fileInfo.getFileType()) {
            case Controller.C_PROJECT -> {
                CFileInfo cFileInfo = (CFileInfo) fileInfo;
                yield constructCLangFileInfoHTML(cFileInfo);
            }
            case Controller.JAVA_PROJECT -> {
                JavaFileInfo javaFileInfo = (JavaFileInfo) fileInfo;
                yield constructJavaFileInfoHTML(javaFileInfo);
            }
            case Controller.PYTHON_PROJECT -> {
                PythonFileInfo pythonFileInfo = (PythonFileInfo) fileInfo;
                yield constructPythonFileInfoHTML(pythonFileInfo);
            }
            default -> throw new IllegalStateException("Unexpected value: " + fileInfo.getFileType());
        };
    }

    private StringBuilder constructCLangFileInfoHTML(CFileInfo cFileInfo){
        StringBuilder dynamicHtmlContent = new StringBuilder();
        dynamicHtmlContent.append(constructCLangExternHTML(cFileInfo));
        dynamicHtmlContent.append(constructCLangMacroHTML(cFileInfo));
        dynamicHtmlContent.append(constructCLangStaticVarHTML(cFileInfo));
        dynamicHtmlContent.append(constructCLangEnumHTML(cFileInfo));
        dynamicHtmlContent.append(constructCLangStructHTML(cFileInfo));
        dynamicHtmlContent.append(constructCLangFunctionHTML(cFileInfo));
        return dynamicHtmlContent;
    }

    private StringBuilder constructJavaFileInfoHTML(JavaFileInfo javaFileInfo) {
        StringBuilder dynamicHtmlContent = new StringBuilder();
        return dynamicHtmlContent;
    }

    private StringBuilder constructPythonFileInfoHTML(PythonFileInfo pythonFileInfo) {
        StringBuilder dynamicHtmlContent = new StringBuilder();
        return dynamicHtmlContent;
    }

    private String constructCLangExternHTML(CFileInfo fileInfo){

        StringBuilder dynamicHtmlContent = new StringBuilder();
        StringBuilder externContent = new StringBuilder();
        String externsContainerId = "externs-container";
        boolean hasExterns = false;

        for (CExtern extern : fileInfo.getExterns()) {
            hasExterns = true;
            String contentID = "content-" + (id++);
            if (extern.getName() != null)
                externContent.append("<div class='collapsible extern' onclick=\"toggleCollapse('").append(contentID).append("')\"><b>")
                        .append(extern.getName()).append("</b>").append("</div>");
            externContent.append("<div class='content extern' id='").append(contentID).append("' style='display: none;'>");/* set display to block/flex to expand all*/
            if (extern.getValue() != null) {
                externContent.append("<div class='attr'><b>Extern value:</b> ").append(extern.getValue()).append("</div>");
            }
            if (extern.getLineNumber() != null)
                externContent.append("<div class='attr'><b>Line number:</b> ").append(extern.getLineNumber()).append("</div>");
            externContent.append("</div>");
        }

        if (hasExterns) {
            dynamicHtmlContent.append("<div class='groupCollapse extern' data-content='").append(externsContainerId).append("' onclick=\"toggleCollapse('").append(externsContainerId).append("')\">Externs</div>");
            dynamicHtmlContent.append("<div class='emptyBox' id='").append(externsContainerId).append("' style='display: none;'>")
                    .append(externContent)
                    .append("</div>").append("</div>");
        }
        return dynamicHtmlContent.toString();
    }

    private String constructCLangMacroHTML(CFileInfo fileInfo){

        StringBuilder dynamicHtmlContent = new StringBuilder();
        StringBuilder macroContent = new StringBuilder();
        String macrosContainerId = "macros-container";
        boolean hasMacros = false;

        for (CMacro macro : fileInfo.getMacros()) {
            hasMacros = true;
            String contentID = "content-" + (id++);
            if (macro.getName() != null)
                macroContent.append("<div class='collapsible macro' onclick=\"toggleCollapse('").append(contentID).append("')\"><b>")
                        .append(macro.getName()).append("</b>").append("</div>");
            macroContent.append("<div class='content macro' id='").append(contentID).append("' style='display: none;'>");/* set display to block/flex to expand all*/
            if (macro.getValue() != null) {
                macroContent.append("<div class='attr'><b>Macro value:</b> ").append(macro.getValue()).append("</div>");
            } else {
                macroContent.append("<div class='attr'><b>Macro is empty</b></div> ");
            }
            if (macro.getLineNumber() != null)
                macroContent.append("<div class='attr'><b>Line number:</b> ").append(macro.getLineNumber()).append("</div>");
            macroContent.append("</div>");
        }

        if (hasMacros) {
            dynamicHtmlContent.append("<div class='groupCollapse macro' data-content='").append(macrosContainerId).append("'onclick=\"toggleCollapse('").append(macrosContainerId).append("')\">Macros</div>");
            dynamicHtmlContent.append("<div class='emptyBox' id='").append(macrosContainerId).append("' style='display: none;'>")
                    .append(macroContent)
                    .append("</div>").append("</div>");
        }
        return dynamicHtmlContent.toString();
    }

    private String constructCLangStaticVarHTML(CFileInfo fileInfo){

        StringBuilder dynamicHtmlContent = new StringBuilder();
        StringBuilder staticVarContent = new StringBuilder();
        String staticsContainerId = "statics-container";
        boolean hasStatics = false;

        for (CStaticVar var : fileInfo.getStaticVars()) {
            hasStatics = true;
            String contentID = "content-" + (id++);
            if (var.getName() != null)
                staticVarContent.append("<div class='collapsible staticVar' onclick=\"toggleCollapse('").append(contentID).append("')\"><b>")
                        .append(var.getName()).append("</b>").append("</div>");
            staticVarContent.append("<div class='content staticVar' id='").append(contentID).append("' style='display: none;'>");/* set display to block/flex to expand all*/
            if (var.getValue() != null) {
                staticVarContent.append("<div class='attr'><b>Static variable value:</b> ").append(var.getValue()).append("</div>");
            } else {
                staticVarContent.append("<div class='attr'><b>Static variable has not initial value!</b></div> ");
            }
            if (var.getLineNumber() != null)
                staticVarContent.append("<div class='attr'><b>Line number:</b> ").append(var.getLineNumber()).append("</div>");
            staticVarContent.append("</div>");
        }

        if (hasStatics) {
            dynamicHtmlContent.append("<div class='groupCollapse staticVar' data-content='").append(staticsContainerId).append("'onclick=\"toggleCollapse('").append(staticsContainerId).append("')\">Static variables</div>");
            dynamicHtmlContent.append("<div class='emptyBox' id='").append(staticsContainerId).append("' style='display: none;'>")
                    .append(staticVarContent)
                    .append("</div>").append("</div>");
        }
        return dynamicHtmlContent.toString();
    }

    private String constructCLangEnumHTML(CFileInfo fileInfo){

        StringBuilder dynamicHtmlContent = new StringBuilder();
        StringBuilder enumContent = new StringBuilder();
        String enumsContainerId = "enums-container";
        boolean hasEnums = false;

        for (CEnum en : fileInfo.getEnums()) {
            hasEnums = true;
            String contentID = "content-" + (id++);
            if (en.getName() != null)
                enumContent.append("<div class='collapsible enums' onclick=\"toggleCollapse('").append(contentID).append("')\"><b>")
                        .append(en.getName()).append("</b>").append("</div>");
            enumContent.append("<div class='content enums' id='").append(contentID).append("' style='display: none;'>");/* set display to block/flex to expand all*/
            if (en.getDocumentation() != null) {
                enumContent.append("<div class='attr'><b>Enum Brief:</b> ").append(en.getDocumentation()).append("</div>");
            } else {
                enumContent.append("<div class='attr'><b>No documentation available!</b></div> ");
            }
            for (String members : en.getMembers())
                if (members != null)
                    enumContent.append("<div class='attr'><b>Member:</b> ").append(members).append("</div>");
            if (en.getEnumType() != null)
                enumContent.append("<div class='attr'><b>Enum type:</b> ").append(en.getEnumType()).append("</div>");
            if (en.getLineNumber() != null)
                enumContent.append("<div class='attr'><b>Line number:</b> ").append(en.getLineNumber()).append("</div>");
            enumContent.append("</div>");
        }

        if (hasEnums) {
            dynamicHtmlContent.append("<div class='groupCollapse enum' data-dontent='").append(enumsContainerId).append("'onclick=\"toggleCollapse('").append(enumsContainerId).append("')\">Enums</div>");
            dynamicHtmlContent.append("<div class='emptyBox' id='").append(enumsContainerId).append("' style='display: none;'>")
                    .append(enumContent)
                    .append("</div>").append("</div>");
        }
        return dynamicHtmlContent.toString();
    }

    private String constructCLangStructHTML(CFileInfo fileInfo){

        StringBuilder dynamicHtmlContent = new StringBuilder();
        StringBuilder structContent = new StringBuilder();
        String structsContainerId = "structs-container";
        boolean hasStructs = false;

        for (CStruct st : fileInfo.getStructs()) {
            hasStructs = true;
            String contentID = "content-" + (id++);
            if (st.getName() != null)
                structContent.append("<div class='collapsible struct' onclick=\"toggleCollapse('").append(contentID).append("')\"><b>")
                        .append(st.getName()).append("</b>").append("</div>");
            structContent.append("<div class='content struct' id='").append(contentID).append("' style='display: none;'>");/* set display to block/flex to expand all*/
            if (st.getDocumentation() != null) {
                structContent.append("<div class='attr'><b>Struct Brief:</b> ").append(st.getDocumentation()).append("</div>");
            } else {
                structContent.append("<div class='attr'><b>No documentation available!</b></div> ");
            }
            for (String member : st.getMembers())
                if (member != null)
                    structContent.append("<div class='attr'><b>Member:</b> ").append(member).append("</div>");
            if (st.getMembers() != null)
                structContent.append("<div class='attr'><b>Struct type:</b> ").append(st.getMembers()).append("</div>");
            if (st.getLineNumber() != null)
                structContent.append("<div class='attr'><b>Line number:</b> ").append(st.getLineNumber()).append("</div>");
            structContent.append("</div>");
        }

        if (hasStructs) {
            dynamicHtmlContent.append("<div class='groupCollapse struct' data-content='").append(structsContainerId).append("'onclick=\"toggleCollapse('").append(structsContainerId).append("')\">Structs</div>");
            dynamicHtmlContent.append("<div class='emptyBox' id='").append(structsContainerId).append("' style='display: none;'>")
                    .append(structContent)
                    .append("</div>").append("</div>");
        }
        return dynamicHtmlContent.toString();
    }

    private String constructCLangFunctionHTML(CFileInfo fileInfo){

        StringBuilder dynamicHtmlContent = new StringBuilder();
        StringBuilder functionsContent = new StringBuilder();
        String functionsContainerId = "functions-container";
        boolean hasFunctions = false;

        for (CFunction fun : fileInfo.getFunctions()) {
            hasFunctions = true;
            String contentID = "content-" + (id++);
            if (fun.getName() != null)
                functionsContent.append("<div class='collapsible function' onclick=\"toggleCollapse('").append(contentID).append("')\"><b>")
                        .append(fun.getName()).append("</b>").append("</div>");
            functionsContent.append("<div class='content function' id='").append(contentID).append("' style='display: none;'>");/* set display to block/flex to expand all*/
            if (fun.getDocumentation() != null) {
                functionsContent.append("<div class='attr'><b>Function Brief:</b> ").append(fun.getDocumentation()).append("</div>");
            } else {
                functionsContent.append("<div class='attr'><b>No documentation available!</b></div> ");
            }
            for (String params : fun.getParams())
                if (params != null && !params.isEmpty())
                    functionsContent.append("<div class='attr'><b>Param:</b> ").append(params).append("</div>");
                else functionsContent.append("<div class='attr'><b>Param:</b> ").append("Function has no arguments ").append("</div>");
            if (fun.getReturnType() != null)
                functionsContent.append("<div class='attr'><b>Return type:</b> ").append(fun.getReturnType()).append("</div>");
            if (fun.getLineNumber() != null)
                functionsContent.append("<div class='attr'><b>Line number:</b> ").append(fun.getLineNumber()).append("</div>");
            functionsContent.append("</div>");
        }

        if (hasFunctions) {
            dynamicHtmlContent.append("<div class='groupCollapse function' data-content='").append(functionsContainerId).append("'onclick=\"toggleCollapse('").append(functionsContainerId).append("')\">Functions</div>");
            dynamicHtmlContent.append("<div class='emptyBox' id='").append(functionsContainerId).append("' style='display: none;'>")
                    .append(functionsContent)
                    .append("</div>").append("</div>");
        }
        return dynamicHtmlContent.toString();
    }

    /**
     * This method refreshes the main window when a file is modified on the system,
     * it retrieves the last opened file.
     */
    public void refreshWebViewDisplay() {
        if (fileInfoBuff != null){
            for (FileNodeModel node : controller.explorer.getProjectNodesList()){
                if (fileInfoBuff.getFileName().equals(node.getFileInfo().getFileName())){
                    compileWebViewDisplay(node.getFileInfo());
                }
            }
        }
    }

    /**
     * This method loads the styling files and concatenates them into a single string
     * to be used later by the WebView engine.
     */
    public void loadWebViewStyling() {
        try {
            documentationView.getEngine().setJavaScriptEnabled(true);
            String cssContent = readFileToString("com/daniel/docify/ui/styling/stylesheet.css");
            String jsContent = readFileToString("com/daniel/docify/ui/styling/script.js");
            String htmlTemplate = readFileToString("com/daniel/docify/ui/styling/index.html");

            String finalHtmlContent = htmlTemplate
                    .replace("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">", "<style>" + cssContent + "</style>")
                    .replace("<script src=\"script.js\"></script>", "<script>" + jsContent + "</script>");
            htmlContent.append(finalHtmlContent);
        } catch (IOException e) {
            Controller.LOGGER.log(Level.SEVERE, "Error loading styles.", e);
        }
    }

    /**
     * This method initializes the Web console to work with the
     * IDE providing log messages from the WebView engine.
     */
    public void initializeWebViewEngine() {
        WebConsoleListener.setDefaultListener(new WebConsoleListener() {
            @Override
            public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {
                System.out.println("Console: [" + sourceId + ":" + lineNumber + "] " + message);
            }
        });
    }

    /**
     * This method initializes the searchResultListView and assigns cell factory
     * in order to stylize the text and add an icon to each entry according to
     * certain rules.
     */
    public void initializeSearchResultListView(){
        controller.getSearchResultListView().setCellFactory(new Callback<ListView<ControllerUtils.SearchResultModel>, ListCell<ControllerUtils.SearchResultModel>>() {
            public FileContentItemCell call(ListView<ControllerUtils.SearchResultModel> listView) {
                return new MainWindow.FileContentItemCell();
            }
        });
    }

    /**
     * This subclass assists the cell factory of the searchResultListView, and it contains the
     * logic behind styling the cells.
     */
    private static class FileContentItemCell extends ListCell<ControllerUtils.SearchResultModel> {
        private final ImageView imageView = new ImageView();
        private final Text text = new Text();
        private final HBox cellBox = new HBox(imageView, text); // Pre-create HBox
        private static final Map<String, Image> iconCache = new HashMap<>();

        private static final String ICON_EXTERN = "assets/icons/clang_extern.png";
        private static final String ICON_MACRO = "assets/icons/clang_macro.png";
        private static final String ICON_STATIC_VAR = "assets/icons/clang_staticVar.png";
        private static final String ICON_ENUM = "assets/icons/clang_enum.png";
        private static final String ICON_STRUCT = "assets/icons/clang_struct.png";
        private static final String ICON_FUNCTION = "assets/icons/clang_function.png";
        private static final String ICON_DEFAULT = "assets/icons/cprog.png";

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
        protected void updateItem(ControllerUtils.SearchResultModel item, boolean empty) {
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

        private void setTextColorAndIcon(ControllerUtils.SearchResultModel item) {
            Image icon;
            switch (item.getNameAndProperty().getType()) {
                case FileInfoModel.ObjectType.EXTREN:
                    icon = iconCache.get(ICON_EXTERN);
                    text.setFill(Color.web("#574e3b"));
                    break;
                case FileInfoModel.ObjectType.MACRO:
                    icon = iconCache.get(ICON_MACRO);
                    text.setFill(Color.web("#215973"));
                    break;
                case FileInfoModel.ObjectType.STATIC:
                    icon = iconCache.get(ICON_STATIC_VAR);
                    text.setFill(Color.web("#a86900"));
                    break;
                case FileInfoModel.ObjectType.ENUM:
                    icon = iconCache.get(ICON_ENUM);
                    text.setFill(Color.web("#13522d"));
                    break;
                case FileInfoModel.ObjectType.STRUCT:
                    icon = iconCache.get(ICON_STRUCT);
                    text.setFill(Color.web("#5e1c12"));
                    break;
                case FileInfoModel.ObjectType.FUNCTION:
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
