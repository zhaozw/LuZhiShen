package org.lvu.models;

/**
 * Created by wuyr on 5/4/16 2:46 PM.
 */
public class Data {

    private String url, src, text, date, nextPageUrl;
    private int currentPage, totalPages;
    private boolean isFavorites;

    public Data(String url, String src, String text, String date) {
        this.url = url;
        this.src = src;
        this.text = text;
        this.date = date;
    }

    public Data(String url, String text, String date) {
        this(url, "", text, date);
    }

    public Data(String src) {
        this("", src, "", "");
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

    public String getDate() {
        return date == null ? "" : date;
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

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isFavorites() {
        return isFavorites;
    }

    public void setFavorites(boolean favorites) {
        isFavorites = favorites;
    }
}
