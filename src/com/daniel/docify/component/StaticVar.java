package com.daniel.docify.component;

public abstract class StaticVar extends Component{
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
