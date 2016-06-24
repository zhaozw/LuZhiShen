package org.lvu.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.lvu.Application;
import org.lvu.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wuyr on 5/4/16 2:46 PM.
 */
public class Data {

    private String url, text;
    private Bitmap bitmap;

    public Data(String url, String src, String text) {
        this(url, text);
        initBitmap(src);
    }

    public Data(String url, String text) {
        this.url = url;
        this.text = text;
    }

    private void initBitmap(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(
                    Application.getContext().getResources(), R.drawable.avril);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
