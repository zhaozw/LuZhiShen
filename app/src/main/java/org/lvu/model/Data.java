package org.lvu.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.lvu.Application;
import org.lvu.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wuyr on 5/4/16 2:46 PM.
 */
public class Data {

    private String url, text;
    private SoftReference<Bitmap> bitmap;

    public Data(String url, String src, String text) {
        this(url, text);
        initBitmap(src);
    }

    public Data(String url, String text) {
        this.url = url;
        this.text = text;
    }

    private void initBitmap(String src) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        //options.inTargetDensity = 80;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = new SoftReference<>(BitmapFactory.decodeStream(input, null, options));
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = new SoftReference<>(BitmapFactory.decodeResource(
                    Application.getContext().getResources(), R.drawable.avril, options));
        }
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }

    public Bitmap getBitmap() {
        return bitmap.get();
    }
}
