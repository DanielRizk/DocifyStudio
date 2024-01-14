package com.daniel.docify.ui.components;

import com.daniel.docify.component.Clang.*;
import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.model.fileInfo.CFileInfo;
import com.daniel.docify.ui.Controller;
import com.daniel.docify.ui.utils.ControllerUtils;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.util.logging.Level;

/**
 * This class represents the main window of the software, and it is responsible for displaying the
 * documentation of the file and also the raw code.
 */
public class MainWindow extends ControllerUtils {

    private final StringBuilder htmlContent = new StringBuilder();
    private final WebView documentationView = new WebView();
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

        controller.getSearchResultListView().setVisible(false);
        controller.getSearchResultListView().getItems().clear();
        documentationView.getEngine().loadContent("");
        documentationView.setVisible(true);

        String pageHtmlStyling = htmlContent.toString();
        StringBuilder dynamicHtmlContent = new StringBuilder();

        int id = 0; /* unique id for each collapsible box */

        if (fileInfo != null) {
            CFileInfo cFileInfo = (CFileInfo) fileInfo;
            controller.fileRawCode.getCodeView().clear();
            controller.fileRawCode.getCodeView().replaceText(0, 0, cFileInfo.getFileContent());
            controller.fileRawCode.getCodeView().showParagraphAtTop(0);

            controller.getPrimaryStage().setTitle("Docify Studio - " + controller.menuActions.getFileFormatModel().getRootNode().getName() + " - " + ((CFileInfo) fileInfo).getFileName());

            if (fileInfo.getHtmlContent() == null){

                StringBuilder externContent = new StringBuilder();
                String externsContainerId = "externs-container";
                boolean hasExterns = false;

                for (CExtern extern : cFileInfo.getExterns()) {
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
                        if (params != null)
                            functionsContent.append("<div class='attr'><b>Param:</b> ").append(params).append("</div>");
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

                if (dynamicHtmlContent.isEmpty()){
                    dynamicHtmlContent.append("<div class='noContentFound'><b>No documentation was detected for this file<b></div>");
                }

                String finalPage = pageHtmlStyling.replace("<!-- dynamic HTML content placeholder -->", dynamicHtmlContent.toString());
                fileInfo.setHtmlContent(finalPage);
            }


            documentationView.getEngine().loadContent(fileInfo.getHtmlContent());
            controller.docContent.updateFileContentListView(fileInfo);
        }else {
            controller.getPrimaryStage().setTitle("Docify Studio - " + controller.menuActions.getFileFormatModel().getRootNode().getName());
            controller.fileRawCode.getCodeView().clear();
            controller.getFileContentListView().getItems().clear();
            updateInfoLabel("File has no documentation!");
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

}
