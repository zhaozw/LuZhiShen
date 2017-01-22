package org.lvu.utils;

import org.lvu.Application;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by wuyr on 1/22/17 8:17 PM.
 */

public class M3U8Parser {

    private static ExecutorService mThreadPool;
    private static OkHttpClient mHttpClient;

    static {
        mThreadPool = Executors.newFixedThreadPool(5);
        mHttpClient = Application.getOkHttpClient();
    }

    public static void parse(final String url, final OnParserListener listener) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    try {
                        List<String> result = new ArrayList<>();
                        String baseUrl = getBaseUrl(url);
                        String m3u8File = mHttpClient.newCall(new Request.Builder().url(url).build()).execute().body().string();
                        BufferedReader reader = new BufferedReader(new StringReader(m3u8File));
                        String tmp;
                        while ((tmp = reader.readLine()) != null)
                            if (tmp.contains(".ts"))
                                result.add(baseUrl + tmp);
                        listener.onSuccess(result, m3u8File);
                    } catch (Exception e) {
                        listener.onFailure(e);
                    }
                }
            }
        });
    }

    private static String getBaseUrl(String url) {
        return url.substring(0, url.lastIndexOf("/") + 1);
    }

    public interface OnParserListener {
        void onSuccess(List<String> urls, String m3u8File);

        void onFailure(Exception e);
    }
}
