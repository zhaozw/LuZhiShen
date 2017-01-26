package org.lvu.models;

/**
 * Created by wuyr on 1/25/17 8:09 PM.
 */

public class Row extends Data {
    private String title;
    private String jsonUrl;
    private String img;
    private String imgs;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
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
