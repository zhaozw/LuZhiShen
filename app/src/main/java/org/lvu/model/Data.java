package org.lvu.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.lvu.Application;
import org.lvu.R;
import org.lvu.utils.BitmapUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by wuyr on 5/4/16 2:46 PM.
 */
public class Data {

    private String url, text;
    private Bitmap bitmap;

    public Data(String url, String src, String text, int scale, boolean isHttps) {
        this(url, text);
        initBitmap(src, scale, isHttps);
    }

    public Data(String url, String src, String text, boolean isHttps) {
        this(url, text);
        initBitmap(src, 0, isHttps);
    }

    public Data(String url, String src, String text, int scale) {
        this(url, text);
        initBitmap(src, scale, false);
    }

    public Data(String url, String src, String text) {
        this(url, text);
        initBitmap(src, 0, false);
    }

    public Data(String url, String text) {
        this.url = url;
        this.text = text;
    }

    private void initBitmap(String src, int scale, boolean isHttps) {
        try {
            URL url = new URL(src);
            if (isHttps) {
                trustAllHosts();
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setHostnameVerifier(DO_NOT_VERIFY);
                connection.setDoInput(true);
                connection.connect();
                initBitmap(scale, connection.getInputStream());
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                initBitmap(scale, connection.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(
                    Application.getContext().getResources(), R.drawable.avril);
        }
    }

    private void initBitmap(int scale, InputStream input) {
        if (scale != 0) {
            Bitmap tmp = BitmapFactory.decodeStream(input);
            bitmap = BitmapUtil.createScaleBitmap(
                    tmp, tmp.getWidth() / scale, tmp.getHeight() / scale);
        } else bitmap = BitmapFactory.decodeStream(input);
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

    private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @SuppressLint("BadHostnameVerifier")
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
