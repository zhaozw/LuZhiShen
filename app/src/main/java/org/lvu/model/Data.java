package org.lvu.model;

import java.io.Serializable;

/**
 * Created by wuyr on 5/4/16 2:46 PM.
 */
public class Data implements Serializable {

    private String url, src, text;

    public Data(String url, String src, String text) {
        this.url = url;
        this.src = src;
        this.text = text;
    }

    public Data(String url, String text) {
        this(url, "", text);
    }

    public String getUrl() {
        return url;
    }

    public String getSrc() {
        return src;
    }

    public String getText() {
        return text;
    }
}
