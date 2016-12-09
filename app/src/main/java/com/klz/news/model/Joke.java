package com.klz.news.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.klz.news.clearContentUtil;

/**
 * Created on 2016/9/28.
 */

public class Joke {

    /**
     * ct : 2015-08-13 13:10:26.149
     * text : 新人发帖求过…… 媳妇最近怀孕了…天天这也不想吃那也不想吃…有一天发脾气要我给他做想吃的，结果做了好多还是没有想吃的…最后着急了大喊:再做不出我想吃的我就去大街上要饭……我想说:你吃什么自己都不知道我怎么做啊…唉…想想男人女人都不容易啊…
     * title : 媳妇儿有了…
     * type : 1
     */
    @JSONField(name = "ct")
    private String ct;
    @JSONField(name = "text")
    private String text;
    @JSONField(name = "title")
    private String title;
    @JSONField(name = "type")
    private int type;

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = clearContentUtil.htmlClearType(text);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
