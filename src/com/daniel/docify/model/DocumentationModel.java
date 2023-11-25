package com.daniel.docify.model;

import java.util.List;

public class DocumentationModel {

    private String functionBrief;
    private List<String> functionParams;
    private String Return;
    private String note;

    public DocumentationModel() {

    }

    public void setFunctionBrief(String functionBrief) {
        this.functionBrief = functionBrief;
    }

    public void setFunctionParams(List<String> functionParams) {
        this.functionParams = functionParams;
    }

    public void setReturn(String Return) {
        this.Return = Return;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFunctionBrief() {
        return functionBrief;
    }

    public List<String> getFunctionParams() {
        return functionParams;
    }

    public String getReturn() {
        return Return;
    }

    public String getNotes() {
        return note;
    }

}
