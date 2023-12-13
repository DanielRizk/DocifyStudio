package com.daniel.docify.component;

import java.util.List;

public abstract class Method extends Component{
    private List<String> params;
    private String returnVal;
    private String documentation;

    public void setParams(List<String> params) {
        this.params = params;
    }

    public void setReturnVal(String returnVal) {
        this.returnVal = returnVal;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public List<String> getParams() {
        return params;
    }

    public String getReturnVal() {
        return returnVal;
    }

    public String getDocumentation() {
        return documentation;
    }
}
