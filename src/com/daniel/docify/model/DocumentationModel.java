package com.daniel.docify.model;

import java.io.Serializable;
import java.util.List;

/**
 * @brief   This class have the Documentation structure and provides getters and setters
 *          for documentation parameters
 */
public class DocumentationModel implements Serializable {

    private String brief;
    private List<String> params;
    private String Return;
    private String note;

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public void setReturn(String Return) {
        this.Return = Return;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBrief() {
        return brief;
    }

    public List<String> getParams() {
        return params;
    }

    public String getReturn() {
        return Return;
    }

    public String getNotes() {
        return note;
    }

}
