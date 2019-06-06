package com.johnhao.wechatapp.adapter;

public class TextContent {

    private String targetText;
    private String replaceText;

    public TextContent(String target, String replace){
        this.targetText = target;
        this.replaceText = replace;
    }

    public String getTargetText(){
        return targetText;
    }

    public String getReplaceText(){
        return replaceText;
    }

}
