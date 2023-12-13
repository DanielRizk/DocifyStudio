package com.daniel.docify.component;

import java.util.List;

public abstract class Struct extends Component{
    private String structType;
    private List<String> members;

    public String getStructType() {
        return structType;
    }

    public void setStructType(String structType) {
        this.structType = structType;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
