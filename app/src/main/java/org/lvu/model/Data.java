package org.lvu.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.utils.BitmapUtil;

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

    public Data(String url, String src, String text, int scale) {
        this(url, text);
        initBitmap(src, scale);
    }

    public Data(String url, String src, String text) {
        this(url, text);
        initBitmap(src, 0);
    }

    public Data(String url, String text) {
        this.url = url;
        this.text = text;
    }

    private void initBitmap(String src, int scale) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            if (scale != 0) {
                Bitmap tmp = BitmapFactory.decodeStream(input);
                bitmap = BitmapUtil.createScaleBitmap(
                        tmp, tmp.getWidth() / scale, tmp.getHeight() / scale);
            } else bitmap = BitmapFactory.decodeStream(input);
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
