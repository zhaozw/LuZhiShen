package org.lvu.models;

import java.util.List;

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

    private String name;
    private String maxIndex;
    private List<Row> rows;

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(String maxIndex) {
        this.maxIndex = maxIndex;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("name = ").append(name).append("\n").append("maxIndex = ").append(maxIndex).append("\n");
        for (Row tmp : rows)
            result.append("-\n").append(tmp.toString()).append("\n");
        return result.toString();
    }
}
