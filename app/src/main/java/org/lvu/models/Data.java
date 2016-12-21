package org.lvu.models;

import java.io.Serializable;

/**
 * Created by wuyr on 5/4/16 2:46 PM.
 */
public class Data implements Serializable {

    private String url, src, text, nextPageUrl;
    private int currentPage, totalPages;

    public Data(String url, String src, String text) {
        this.url = url;
        this.src = src;
        this.text = text;
    }

    public Data(String url, String text) {
        this(url, "", text);
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public String getSrc() {
        return src == null ? "" : src;
    }

    public String getText() {
        return text == null ? "" : text;
    }

    public String getNextPageUrl() {
        return nextPageUrl == null ? "" : nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
