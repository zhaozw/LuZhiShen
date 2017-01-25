package org.lvu.models;

/**
 * Created by wuyr on 1/25/17 8:09 PM.
 */

public class Row {
    private String title;
    private String date;
    private String jsonUrl;
    private String img;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJsonUrl() {
        return jsonUrl;
    }

    public void setJsonUrl(String jsonUrl) {
        this.jsonUrl = jsonUrl;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "title:" + title + "\ndate:" +
                date + "\njsonUrl:" + jsonUrl + "\nimg:" + img + "\n";
    }
}
