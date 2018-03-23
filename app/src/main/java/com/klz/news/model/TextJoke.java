package com.klz.news.model;

import com.klz.news.util.clearContentUtil;

/**
 * Created on 2016/9/28.
 */

public class TextJoke {

    /**
     * id : 5850aee56e368ed797bc1f0c
     * title : 有个记性不好的老婆真
     * text : 有个记性不好的老婆真是让人头疼：早上把口红蹭我衣服上，然后刚吃完午饭就揍我了。
     * type : 1
     * ct : 2016-12-14 10:31:01.384
     */

    private String id;
    private String title;
    private String text;
    private int type;
    private String ct;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = clearContentUtil.htmlClearType(text);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }
}
