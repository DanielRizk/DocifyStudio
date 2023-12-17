package com.daniel.docify.component;

public abstract class Macro extends Component {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
