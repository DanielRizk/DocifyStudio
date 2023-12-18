package com.daniel.docify.component;

import java.util.List;

public abstract class Method extends Component{
    private List<String> params;
    private String returnType;
    private String documentation;

    public void setParams(List<String> params) {
        this.params = params;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public List<String> getParams() {
        return params;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getDocumentation() {
        return documentation;
    }
}
