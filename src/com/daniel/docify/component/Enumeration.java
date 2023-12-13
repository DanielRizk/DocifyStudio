package com.daniel.docify.component;

import java.util.List;

public abstract class Enumeration extends Component{
    private String EnumType;
    private List<String> members;

    public String getEnumType() {
        return EnumType;
    }

    public void setEnumType(String enumType) {
        EnumType = enumType;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
