package com.daniel.docify.component;

import java.util.List;

public abstract class Enumeration extends Component{
    private String EnumType;
    private String documentation;
    private List<String> members;

    public String getEnumType() {
        return EnumType;
    }

    public void setEnumType(String enumType) {
        EnumType = enumType;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
