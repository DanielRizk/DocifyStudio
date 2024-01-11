package com.daniel.docify.ui;

import com.daniel.docify.component.Clang.*;
import com.daniel.docify.model.FileNodeModel;
import com.daniel.docify.fileProcessor.UserConfiguration;
import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.model.FileInfoModel.ItemNameAndProperty;
import com.daniel.docify.model.fileInfo.CFileInfo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import netscape.javascript.JSException;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.daniel.docify.core.Main.LOAD_ICONS;
import static com.daniel.docify.core.Main.VERSION;
import static com.daniel.docify.fileProcessor.DirectoryProcessor.BuildAndProcessDirectory;

public class Controller implements Initializable {

    public static FileNodeModel rootNode = null;
    public final static String CProject = ".h";
    public final static String PythonProject = ".py";
    public final static String JavaProject = ".java";


    private Stage primaryStage;

    StringBuilder htmlContent = new StringBuilder();

    private final ObservableList<FileNodeModel> items = FXCollections.observableArrayList();

    private final CodeArea codeArea = new CodeArea();

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    VirtualizedScrollPane<CodeArea> codeAreaScrollPane = new VirtualizedScrollPane<>(codeArea);


    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const", "int",
            "uint8_t","uint16_t","uint32_t","uin64_t","bool",
            "float","struct","typedef", "static", "while",
            "transient", "try", "void", "volatile",
            "do", "double", "for", "if", "else",
            "size_t", "switch", "case", "default"
    };

    private static final String[] PREPROCESSORS = new String[] {
            "#define", "#include", "#ifdef", "#ifndef", "#else", "#if defined"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PREPROCESSOR_PATTERN = "(" + String.join("|", PREPROCESSORS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(\\)";
    private static final String BRACE_PATTERN = "\\{\\}";
    private static final String BRACKET_PATTERN = "\\[\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PREPROCESSOR>" + PREPROCESSOR_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass = getStyleClass(matcher);
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @NotNull
    private static String getStyleClass(Matcher matcher) {
        String styleClass =
                matcher.group("KEYWORD") != null ? "keyword" :
                        matcher.group("PREPROCESSOR") != null ? "preprocessor" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */
        assert styleClass != null;
        return styleClass;
    }


    @FXML
    private Tab fileContentTab;

    @FXML
    private Tab fileDocumentationTab;

    private final WebView webViewDisplay = new WebView();

    @FXML
    private ListView<FileNodeModel> explorerListView = new ListView<>();

    @FXML
    private ListView<ItemNameAndProperty> fileContentListView;

    @FXML
    private ListView<SearchResultModel> searchResultListView;

    @FXML
    private CheckBox documentedFilesCheckbox;

    @FXML
    private TreeView<FileNodeModel> explorerTreeView;

    @FXML
    private MenuItem file_closeMenuItem;

    @FXML
    private Menu file_newSubMenu;

    @FXML
    private MenuItem file_new_cProjectMenuItem;

    @FXML
    private MenuItem file_new_javaProjectMenuItem;

    @FXML
    private MenuItem file_new_pythonProjectMenuItem;

    @FXML
    private MenuItem file_openMenuItem;

    @FXML
    private Menu file_saveAsSubMenu;

    @FXML
    private MenuItem file_save_docifyMenuItem;

    @FXML
    private MenuItem file_save_pdfMenuItem;

    @FXML
    private Label infoLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private TextField searchBar;

    @FXML
    public ProgressBar progressBar;















    /**
     * @brief   This method displays and updates the main display view
     *          with the file content when the file is selected from the
     *          tree view or the list view, and call the update file content
     *          list method
     *
     */
    private void compileWebViewDisplay(FileInfoModel fileInfo) {

        searchResultListView.setVisible(false);
        searchResultListView.getItems().clear();
        webViewDisplay.setVisible(true);

        String pageHtmlStyling = htmlContent.toString();
        StringBuilder dynamicHtmlContent = new StringBuilder();



        webViewDisplay.getEngine().loadContent("");

        int id = 0;

        if (fileInfo != null) {
            CFileInfo cFileInfo = (CFileInfo) fileInfo;
            codeArea.clear();
            codeArea.replaceText(0, 0, cFileInfo.getFileContent());
            codeArea.showParagraphAtTop(0);

            primaryStage.setTitle("Docify Studio - " + rootNode.getName() + " - " + ((CFileInfo) fileInfo).getFileName());

            StringBuilder externContent = new StringBuilder();
            String externsContainerId = "externs-container";
            boolean hasExterns = false;

            for (CExtern extern : cFileInfo.getExterns()) {
                hasExterns = true;
                String contentID = "content-"+ (id++);
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
                dynamicHtmlContent.append("<div class='groupCollapse extern' onclick=\"toggleCollapse('").append(externsContainerId).append("')\">Externs</div>");
                dynamicHtmlContent.append("<div id='").append(externsContainerId).append("' style='display: none;'>")
                        .append(externContent)
                        .append("</div>");
            }


            StringBuilder macroContent = new StringBuilder();
            String macrosContainerId = "macros-container";
            boolean hasMacros = false;

            for (CMacro macro : cFileInfo.getMacros()) {
                hasMacros = true;
                String contentID = "content-"+ (id++);
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
                dynamicHtmlContent.append("<div class='groupCollapse macro' onclick=\"toggleCollapse('").append(macrosContainerId).append("')\">Macros</div>");
                dynamicHtmlContent.append("<div id='").append(macrosContainerId).append("' style='display: none;'>")
                        .append(macroContent)
                        .append("</div>");
            }




            StringBuilder staticVarContent = new StringBuilder();
            String staticsContainerId = "statics-container";
            boolean hasStatics = false;

            for (CStaticVar var : cFileInfo.getStaticVars()) {
                hasStatics = true;
                String contentID = "content-"+ (id++);
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
                dynamicHtmlContent.append("<div class='groupCollapse staticVar' onclick=\"toggleCollapse('").append(staticsContainerId).append("')\">Static variables</div>");
                dynamicHtmlContent.append("<div id='").append(staticsContainerId).append("' style='display: none;'>")
                        .append(staticVarContent)
                        .append("</div>");
            }












            StringBuilder enumContent = new StringBuilder();
            String enumsContainerId = "enums-container";
            boolean hasEnums = false;

            for (CEnum en : cFileInfo.getEnums()) {
                hasEnums = true;
                String contentID = "content-"+ (id++);
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
                    if (members != null) enumContent.append("<div class='attr'><b>Member:</b> ").append(members).append("</div>");
                if (en.getEnumType() != null)
                    enumContent.append("<div class='attr'><b>Enum type:</b> ").append(en.getEnumType()).append("</div>");
                if (en.getLineNumber() != null)
                    enumContent.append("<div class='attr'><b>Line number:</b> ").append(en.getLineNumber()).append("</div>");
                enumContent.append("</div>");
            }

            if (hasEnums) {
                dynamicHtmlContent.append("<div class='groupCollapse enum' onclick=\"toggleCollapse('").append(enumsContainerId).append("')\">Enums</div>");
                dynamicHtmlContent.append("<div id='").append(enumsContainerId).append("' style='display: none;'>")
                        .append(enumContent)
                        .append("</div>");
            }












            StringBuilder structContent = new StringBuilder();
            String structsContainerId = "structs-container";
            boolean hasStructs = false;

            for (CStruct st : cFileInfo.getStructs()) {
                hasStructs = true;
                String contentID = "content-"+ (id++);
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
                    if (member != null) structContent.append("<div class='attr'><b>Member:</b> ").append(member).append("</div>");
                if (st.getMembers() != null)
                    structContent.append("<div class='attr'><b>Struct type:</b> ").append(st.getMembers()).append("</div>");
                if (st.getLineNumber() != null)
                    structContent.append("<div class='attr'><b>Line number:</b> ").append(st.getLineNumber()).append("</div>");
                structContent.append("</div>");
            }

            if (hasStructs) {
                dynamicHtmlContent.append("<div class='groupCollapse struct' onclick=\"toggleCollapse('").append(structsContainerId).append("')\">Structs</div>");
                dynamicHtmlContent.append("<div id='").append(structsContainerId).append("' style='display: none;'>")
                        .append(structContent)
                        .append("</div>");
            }









            StringBuilder functionsContent = new StringBuilder();
            String functionsContainerId = "functions-container";
            boolean hasFunctions = false;

            for (CFunction fun : cFileInfo.getFunctions()) {
                hasFunctions = true;
                String contentID = "content-"+ (id++);
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
                    if (params != null) functionsContent.append("<div class='attr'><b>Param:</b> ").append(params).append("</div>");
                if (fun.getReturnType() != null)
                    functionsContent.append("<div class='attr'><b>Return type:</b> ").append(fun.getReturnType()).append("</div>");
                if (fun.getLineNumber() != null)
                    functionsContent.append("<div class='attr'><b>Line number:</b> ").append(fun.getLineNumber()).append("</div>");
                functionsContent.append("</div>");
            }

            if (hasFunctions) {
                dynamicHtmlContent.append("<div class='groupCollapse function' onclick=\"toggleCollapse('").append(functionsContainerId).append("')\">Functions</div>");
                dynamicHtmlContent.append("<div id='").append(functionsContainerId).append("' style='display: none;'>")
                        .append(functionsContent)
                        .append("</div>");
            }
        }
        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });

        String finalPage = pageHtmlStyling.replace("<!-- dynamic HTML content placeholder -->", dynamicHtmlContent.toString());

        webViewDisplay.getEngine().loadContent(finalPage);
        assert fileInfo != null;
        updateFileContentListView(fileInfo);
    }




    private List<SearchResultModel> searchList(String searchWord){
        ObservableList<FileNodeModel> allFiles = updateFilteredListView();
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



    void scrollToLine(String selectedItem) {
        if (selectedItem != null && !selectedItem.isEmpty()) {
            try {
                webViewDisplay.getEngine().executeScript("highlightSearch('" + escapeJavaScriptString(selectedItem) + "')");
            } catch (JSException e) {
                LOGGER.log(Level.SEVERE, "Error executing highlightSearch script", e);
            }
        }
    }

    private String escapeJavaScriptString(String str) {
        return str.replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
    }




    @FXML
    void getFromSearchResult(MouseEvent event){
        SearchResultModel selectedItem = searchResultListView.getSelectionModel().getSelectedItem();

        if (searchResultListView.getSelectionModel().getSelectedItem() != null) {
            webViewDisplay.getEngine().loadContent("");
            compileWebViewDisplay(selectedItem.getParentFileNode().getFileInfo());
        }
        searchResultListView.getItems().clear();
        searchResultListView.setVisible(false);
        webViewDisplay.setVisible(true);

        webViewDisplay.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // Now that the page has loaded, we can highlight the search term
                if (selectedItem != null) scrollToLine(selectedItem.toString());
            }
        });



    }



    @FXML
    void treeViewFileSelection(MouseEvent event) {
        if(explorerTreeView.getSelectionModel().getSelectedItem() != null &&
                explorerTreeView.getSelectionModel().getSelectedItem().isLeaf()){
            compileWebViewDisplay(explorerTreeView.getSelectionModel().getSelectedItem().getValue().getFileInfo());
        }
    }

    @FXML
    void listViewFileSelection(MouseEvent event) {
        if(explorerListView.getSelectionModel().getSelectedItem() != null){
            compileWebViewDisplay(explorerListView.getSelectionModel().getSelectedItem().getFileInfo());
        }
    }

    @FXML
    void fileContentListSelection(MouseEvent event) {
        ItemNameAndProperty selectedItem = fileContentListView.getSelectionModel().getSelectedItem();
        scrollToLine(selectedItem.toString());
    }


    @FXML
    void cProjectMenuItemStart(ActionEvent event) {
        startNew(CProject);
    }

    @FXML
    void javaProjectMenuItemStart(ActionEvent event) {
        popUpAlert(Alert.AlertType.INFORMATION, "Information",
                "Java documentation will be available in the next release");
    }

    @FXML
    void pythonProjectMenuItemStart(ActionEvent event) {
        popUpAlert(Alert.AlertType.INFORMATION, "Information",
                "Python documentation will be available in the next release");
    }

    @FXML
    void isDocumentedFilesCheckbox(MouseEvent event) {
        updateFilteredListView();
    }

    @FXML
    void closeOpenedProject(ActionEvent event) {
        closeRoutine();
    }

    private void closeRoutine(){
        if (rootNode != null) {
            rootNode = null;
            explorerTreeView.setRoot(null);
            explorerListView.getItems().clear();
            items.clear();
            webViewDisplay.getEngine().loadContent("");
            fileContentListView.getItems().clear();
            searchResultListView.getItems().clear();
            searchResultListView.setVisible(false);
            webViewDisplay.setVisible(true);
            primaryStage.setTitle("Docify Studio");
            infoLabel.setText(null);
        }
    }


    @FXML
    void searchFromButton(MouseEvent event){
        searchAndDisplay();
    }

    @FXML
    void searchFromKey(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER){
            searchAndDisplay();
        }
    }

    void searchAndDisplay(){
        String searchKeyword = searchBar.getText();
        if (searchKeyword != null) {

            List<SearchResultModel> result = searchList(searchKeyword);

            if (!result.isEmpty()){
                webViewDisplay.getEngine().loadContent("");
                searchResultListView.getItems().clear();
                fileContentListView.getItems().clear();
                webViewDisplay.setVisible(false);
                searchResultListView.getItems().addAll(result);
                searchResultListView.setVisible(true);
                updateInfoLabel(result.size()+" records found");
            }
            else{
                //mainDisplayTextArea.appendText("No results found!");
                updateInfoLabel("No results found!");
            }
        }
    }

    private void updateInfoLabel(String initialValue){
        infoLabel.setText(initialValue);
        Duration initialDuration = Duration.seconds(3);
        Timeline timeline = new Timeline(
                new KeyFrame(initialDuration, event -> infoLabel.setText(""))
        );
        timeline.play();
    }
    @FXML
    void openDociFile(ActionEvent event) throws MalformedURLException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Docify File");
        File lastSavePath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastSaveConfig()));

        fileChooser.setInitialDirectory(lastSavePath);

        // Set extension filters (optional)
        //directoryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Select Directory", "*"));
        FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All Files", "*.*");

        // Set extension filter for suggested file type
        FileChooser.ExtensionFilter docifyFilter = new FileChooser.ExtensionFilter("Docify File (*.doci)", "*.doci");

        // Add the filters to the file chooser
        fileChooser.getExtensionFilters().addAll(docifyFilter, allFilesFilter);

        // Show the Save File dialog
        File selectedDir = fileChooser.showOpenDialog(null);

        if (selectedDir != null) {
            closeRoutine();
            System.out.println("Selected Directory " + selectedDir.getParent());
            UserConfiguration.saveUserLastSaveConfig(selectedDir.getParent());

            if (selectedDir.getAbsolutePath().endsWith(".doci")) {
                rootNode = new FileNodeModel(null,false,null);
                rootNode = rootNode.load(selectedDir.getAbsolutePath());
                updateTreeView(rootNode);
                updateInfoLabel("File -"+rootNode.getName()+"- opened successfully");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Error opening file");
                alert.showAndWait();
            }
            primaryStage.setTitle("Docify Studio - "+rootNode.getName());
            updateInfoLabel("Project Documentation -"+rootNode.getName()+"- loaded successfully");
        }
    }

    @FXML
    void saveDociFile(ActionEvent event) {
        if (rootNode != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Docify File");
            File lastSavePath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastSaveConfig()));

            fileChooser.setInitialDirectory(lastSavePath);

            FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All Files", "*.*");

            // Set extension filter for suggested file type
            FileChooser.ExtensionFilter docifyFilter = new FileChooser.ExtensionFilter("Docify File (*.doci)", "*.doci");

            // Add the filters to the file chooser
            fileChooser.getExtensionFilters().addAll(docifyFilter, allFilesFilter);

            // Show the Save File dialog
            File selectedDir = fileChooser.showSaveDialog(null);

            if (selectedDir != null) {
                System.out.println("Selected Directory " + selectedDir.getParent());
                UserConfiguration.saveUserLastSaveConfig(selectedDir.getParent());

                if (selectedDir.getAbsolutePath().endsWith(".doci")) {
                    rootNode.save(rootNode, selectedDir.getAbsolutePath());
                } else {
                    rootNode.save(rootNode, selectedDir.getAbsolutePath() + ".doci");
                }
                updateInfoLabel("File saved successfully");
            }
        }else {
            popUpAlert(Alert.AlertType.ERROR, "Error", "No project opened");
        }
    }

    private void popUpAlert(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);

        // Show the alert dialog
        alert.showAndWait();

    }


    /**
     * @brief   This method allows the controller to access the
     *          primary stage from main
     */
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * @brief   This method is triggered when the create new option is clicked
     *          it build the tree view and the list view of the root dir
     *          and prepares the UI and populates the views
     */
    void startNew(String fileType) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Create new project");

        File lastOpenPath = new File(Objects.requireNonNull(UserConfiguration.loadUserLastOpenConfig()));

        if (lastOpenPath.exists() && !lastOpenPath.getAbsolutePath().isEmpty()) {
            directoryChooser.setInitialDirectory(lastOpenPath);
        }

        File selectedDir = directoryChooser.showDialog(new Stage());

        if (selectedDir != null) {

            closeRoutine();

            System.out.println("Selected Directory " + selectedDir.getAbsolutePath());
            UserConfiguration.saveUserLastOpenConfig(selectedDir.getAbsolutePath());

            try {
                rootNode = BuildAndProcessDirectory(selectedDir, fileType);
                assert rootNode != null;
                updateTreeView(rootNode);

            } catch (IOException e){
                throw new RuntimeException(e);
            }
            primaryStage.setTitle("Docify Studio - "+rootNode.getName());
            updateInfoLabel("Project Documentation -"+rootNode.getName()+"- created successfully");
        }
    }


    /**
     * @brief   This method populates and updates the Tree view, and calls
     *          the generateListView method
     */
    private void updateTreeView(FileNodeModel rootFileNode) throws MalformedURLException {
        TreeItem<FileNodeModel> treeItem;
        if (rootFileNode == null){
            treeItem = new TreeItem<>();
        }
        else {
            treeItem = convertToTreeItem(rootFileNode);
        }
        explorerTreeView.setRoot(treeItem);
        assert rootFileNode != null;
        items.clear();
        generateListview(rootFileNode);
        updateListView();
    }

    /**
     * @brief   This method converts FileNodeModel instances to TreeItem model
     *          to be compatible with TreeView model
     */
    private TreeItem<FileNodeModel> convertToTreeItem(FileNodeModel fileNode) throws MalformedURLException {

        TreeItem<FileNodeModel> treeItem = new TreeItem<>(fileNode);
        treeItem.setExpanded(true);
        if (fileNode.isFile()){
            if (fileNode.getName().endsWith(CProject)) {
                treeItem.setGraphic(setIcon("assets/icons/c_header.png")); // path to your file icon
            } else if (fileNode.getName().endsWith(".c") || fileNode.getName().endsWith(".cpp")){
                treeItem.setGraphic(setIcon("assets/icons/c_src.png")); // path to your folder icon
            } else if (fileNode.getName().endsWith(JavaProject)) {
                treeItem.setGraphic(setIcon("assets/icons/java_file.png")); // path to your folder icon
            } else if (fileNode.getName().endsWith(PythonProject)) {
                treeItem.setGraphic(setIcon("assets/icons/py_file.png")); // path to your folder icon
            }
        }
        else{
            treeItem.setGraphic(setIcon("assets/icons/open.png"));
        }
        for(FileNodeModel child : fileNode.getChildren()){
            treeItem.getChildren().add(convertToTreeItem(child));
        }
        return treeItem;
    }

    /**
     * @brief   This method sets and updates the list view
     *          whenever a new project is created
     */
    private void updateListView(){
        explorerListView.getItems().clear();
        updateFilteredListView();
    }

    /**
     * This method generates a list of FileNodeModel
     * from the root node and adds it to the global
     * list container
     */
    private void generateListview(FileNodeModel fileNode){
        if (fileNode.isFile()) {
            items.add(fileNode);
        }
        for (FileNodeModel child : fileNode.getChildren()) {
            generateListview(child);
        }
    }

    /**
     * This method generates a filtered list from the original list
     * and updates the list view based on the filter selection
     */
    private ObservableList<FileNodeModel> updateFilteredListView(){
        ObservableList<FileNodeModel> filteredItems = FXCollections.observableArrayList();
        filteredItems.clear();
        for(FileNodeModel item : items){
            if (item.getFileInfo() != null || !documentedFilesCheckbox.isSelected()){
                filteredItems.add(item);
            }
        }
        explorerListView.getItems().clear();
        explorerListView.getItems().addAll(filteredItems);
        return filteredItems;
    }

    /**
     * This method generates a list of all items names contained
     * in a file, gets triggered whenever a file is selected from
     * the tree view or the list view, and called by update main
     * text display method
     */
    private void updateFileContentListView(FileInfoModel fileInfoModel){
        fileContentListView.getItems().clear();
        ObservableList<ItemNameAndProperty> fileContentList = FXCollections.observableArrayList();
        fileContentList.clear();
        if(fileInfoModel != null){
            fileContentList.addAll(fileInfoModel.getItemNames());
        }
        else {
            updateInfoLabel("File has no documentation!");
            primaryStage.setTitle("Docify Studio - " + rootNode.getName());
        }
        fileContentListView.getItems().addAll(fileContentList);
    }

    private void setIcon(MenuItem menuItem, String iconPath) throws MalformedURLException {
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

    private Node setIcon(String iconPath) throws MalformedURLException {

        var iconFile = new File(iconPath);
        String iconUrl = iconFile.toURI().toURL().toExternalForm();
        ImageView imageView = new ImageView(new Image(iconUrl));
        imageView.setFitHeight(20.0);
        imageView.setFitWidth(20.0);

        return imageView;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (LOAD_ICONS) loadSystemIcons();

        fileContentTab.setContent(codeAreaScrollPane);
        fileDocumentationTab.setContent(webViewDisplay);

        initializeCodeArea();
        initializeFileContentListView();
        initializeExplorerListView();
        loadWebViewStyling();

        progressBar.setVisible(false);
        progressBar.setStyle("-fx-accent: green;");

        infoLabel.setText("");
        versionLabel.setText(VERSION);
        updateInfoLabel("Initialization complete!");
    }

    private void initializeCodeArea(){
        codeArea.setEditable(false);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        try {
            loadCodeAreaStylesheet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });
    }

    private void loadCodeAreaStylesheet() throws IOException {
        File file = new File("src/com/daniel/docify/ui/syntax.css");
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + "src/com/daniel/docify/ui/syntax.css");
        }
        String stylesheet = file.toURI().toURL().toExternalForm();
        codeArea.getStylesheets().add(stylesheet);
    }


    private String readFileToString(String path) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void loadWebViewStyling() {
        try {
            webViewDisplay.getEngine().setJavaScriptEnabled(true);
            String cssContent = readFileToString("com/daniel/docify/ui/stylesheet.css");
            String jsContent = readFileToString("com/daniel/docify/ui/script.js");
            String htmlTemplate = readFileToString("com/daniel/docify/ui/index.html");

            // Insert CSS and JS content into the HTML template
            String finalHtmlContent = htmlTemplate
                    .replace("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">", "<style>" + cssContent + "</style>")
                    .replace("<script src=\"script.js\"></script>", "<script>" + jsContent + "</script>");
            htmlContent.append(finalHtmlContent);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading styles.", e);
        }
    }


    private void initializeFileContentListView(){
        fileContentListView.setCellFactory(new Callback<ListView<ItemNameAndProperty>, ListCell<ItemNameAndProperty>>() {
            @Override
            public ListCell<ItemNameAndProperty> call(ListView<ItemNameAndProperty> listView) {
                return new FileContentItemCell();
            }
        });

    }

    private void initializeExplorerListView(){
        explorerListView.setCellFactory(new Callback<ListView<FileNodeModel>, ListCell<FileNodeModel>>() {
            @Override
            public ListCell<FileNodeModel> call(ListView<FileNodeModel> listView) {
                return new ExplorerItemCell();
            }
        });

    }

    private void loadSystemIcons(){
        try {
            setIcon(file_newSubMenu, "assets/icons/new.png");
            setIcon(file_saveAsSubMenu, "assets/icons/save.png");
            setIcon(file_openMenuItem, "assets/icons/open.png");
            setIcon(file_closeMenuItem, "assets/icons/close.png");
            setIcon(file_new_cProjectMenuItem, "assets/icons/cprog.png");
            setIcon(file_new_javaProjectMenuItem, "assets/icons/javaprog.png");
            setIcon(file_new_pythonProjectMenuItem, "assets/icons/pyprog.png");
            setIcon(file_save_docifyMenuItem, "assets/icons/doci.png");
            setIcon(file_save_pdfMenuItem, "assets/icons/pdf.png");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private record SearchResultModel(String itemName, FileNodeModel fileNodeModel) {
        public FileNodeModel getParentFileNode() {
                return fileNodeModel;
            }
            @Override
            public String toString() {
                return itemName;
            }
        }

    public static class FileContentItemCell extends ListCell<ItemNameAndProperty> {
        private final ImageView imageView = new ImageView();
        private final Text text = new Text();

        @Override
        protected void updateItem(ItemNameAndProperty item, boolean empty) {
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
                cellBox.setSpacing(10); // Set spacing as needed
                setGraphic(cellBox);
            }
        }

        private Image setIconForList(String iconPath) throws FileNotFoundException {
            FileInputStream input = new FileInputStream(iconPath);
            return new Image(input);
        }
    }

    public static class ExplorerItemCell extends ListCell<FileNodeModel> {
        private final ImageView imageView = new ImageView();
        private final Text text = new Text();

        @Override
        protected void updateItem(FileNodeModel node, boolean empty) {
            super.updateItem(node, empty);

            if (empty || node == null) {
                text.setText(null);
                imageView.setImage(null);
                setGraphic(null);
            } else {
                text.setText(node.getName()); // Use your actual getter method for the name

                try {
                    if (node.getName().endsWith(CProject)) {
                        imageView.setImage(setIconForList("assets/icons/c_header.png")); // path to your file icon
                        text.setFill(Color.web("#28725f"));
                    } else if (node.getName().endsWith(".c") || node.getName().endsWith(".cpp")){
                        imageView.setImage(setIconForList("assets/icons/c_src.png")); // path to your folder icon
                        text.setFill(Color.web("#4b245b"));
                    } else if (node.getName().endsWith(JavaProject)) {
                        imageView.setImage(setIconForList("assets/icons/java_file.png")); // path to your folder icon
                        text.setFill(Color.web("#824d00"));
                    } else if (node.getName().endsWith(PythonProject)) {
                    imageView.setImage(setIconForList("assets/icons/py_file.png")); // path to your folder icon
                    text.setFill(Color.web("#62664d"));
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                imageView.setFitHeight(30.0);
                imageView.setFitWidth(30.0);
                HBox cellBox = new HBox(imageView, text);
                cellBox.setSpacing(10); // Set spacing as needed
                setGraphic(cellBox);
            }
        }

        private Image setIconForList(String iconPath) throws FileNotFoundException {
            FileInputStream input = new FileInputStream(iconPath);
            return new Image(input);
        }
    }
}