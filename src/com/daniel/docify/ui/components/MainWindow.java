package com.daniel.docify.ui.components;

import com.daniel.docify.component.Clang.*;
import com.daniel.docify.model.FileInfoModel;
import com.daniel.docify.model.fileInfo.CFileInfo;
import com.daniel.docify.ui.Controller;
import com.daniel.docify.ui.utils.ControllerUtils;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.util.logging.Level;

public class MainWindow extends ControllerUtils {

    public StringBuilder htmlContent = new StringBuilder();
    public final WebView documentationView = new WebView();
    public MainWindow(Controller controller) {
        super(controller);
    }

    /**
     * @brief   This method displays and updates the main display view
     *          with the file content when the file is selected from the
     *          tree view or the list view, and call the update file content
     *          list method
     *
     */
    public void compileWebViewDisplay(FileInfoModel fileInfo) {

        controller.searchResultListView.setVisible(false);
        controller.searchResultListView.getItems().clear();
        documentationView.setVisible(true);

        String pageHtmlStyling = htmlContent.toString();
        StringBuilder dynamicHtmlContent = new StringBuilder();



        documentationView.getEngine().loadContent("");

        int id = 0;

        if (fileInfo != null) {
            CFileInfo cFileInfo = (CFileInfo) fileInfo;
            controller.fileRawCode.codeView.clear();
            controller.fileRawCode.codeView.replaceText(0, 0, cFileInfo.getFileContent());
            controller.fileRawCode.codeView.showParagraphAtTop(0);

            controller.primaryStage.setTitle("Docify Studio - " + Controller.rootNode.getName() + " - " + ((CFileInfo) fileInfo).getFileName());

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
        controller.fileRawCode.codeView.textProperty().addListener((obs, oldText, newText) -> {
            controller.fileRawCode.codeView.setStyleSpans(0, controller.fileRawCode.computeHighlighting(newText));
        });

        String finalPage = pageHtmlStyling.replace("<!-- dynamic HTML content placeholder -->", dynamicHtmlContent.toString());

        documentationView.getEngine().loadContent(finalPage);
        assert fileInfo != null;
        controller.docContent.updateFileContentListView(fileInfo);
    }

    public void loadWebViewStyling() {
        try {
            documentationView.getEngine().setJavaScriptEnabled(true);
            String cssContent = readFileToString("com/daniel/docify/ui/stylesheet.css");
            String jsContent = readFileToString("com/daniel/docify/ui/script.js");
            String htmlTemplate = readFileToString("com/daniel/docify/ui/index.html");

            // Insert CSS and JS content into the HTML template
            String finalHtmlContent = htmlTemplate
                    .replace("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">", "<style>" + cssContent + "</style>")
                    .replace("<script src=\"script.js\"></script>", "<script>" + jsContent + "</script>");
            htmlContent.append(finalHtmlContent);
        } catch (IOException e) {
            Controller.LOGGER.log(Level.SEVERE, "Error loading styles.", e);
        }
    }

}
