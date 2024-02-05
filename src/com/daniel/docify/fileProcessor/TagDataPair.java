package com.daniel.docify.fileProcessor;

public class TagDataPair{
    private final int tag;
    private int IData;
    private String SData;
    private boolean BData;

    public TagDataPair(int tag, String SData){
        this.tag = tag;
        this.SData = SData;
    }

    public TagDataPair(int tag, int IData){
        this.tag = tag;
        this.IData = IData;
    }

    public TagDataPair(int tag, boolean BData){
        this.tag = tag;
        this.BData = BData;
    }

    public int tag() {
        return tag;
    }

    public int IData() {
        return IData;
    }

    public String SData() {
        return SData;
    }

    public boolean BData() {
        return BData;
    }
}

