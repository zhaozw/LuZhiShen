package org.lvu.utils;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lvu.model.Data;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 4/7/16 1:24 PM.
 */
public class HttpUtil {

    public static void getChinaVideoListAsync(final String url, final HttpRequestCallbackListener listener) {
        new Thread() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        List<Data> result = new ArrayList<>();
                        String nextPage = "";
                        Document document = Jsoup.parse(new URL(url), 8000);
                        Elements links = document.select("li"),
                                src = document.select("img[src]"),
                                link = links.select("a[href][title][target]");
                        for (int i = 0; i < link.size(); i++) {
                            String videoUrl = getChinaVideoUrlByUrl(link.get(i).attr("abs:href"));
                            if (!videoUrl.isEmpty())
                                result.add(new Data(videoUrl,
                                        src.get(i).attr("abs:src"),
                                        link.get(i).attr("title")));
                        }
                        Elements next = document.select("div[class]"), next2 = null;
                        for (Element tmp : next) {
                            if (tmp.attr("class").equals("page"))
                                next2 = tmp.children();
                        }
                        if (next2 != null) {
                            for (Element tmp : next2) {
                                if (tmp.text().equals("下一页"))
                                    nextPage = tmp.attr("abs:href");
                            }
                        }
                        listener.onSuccess(result, nextPage);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e instanceof ConnectException)
                            continue;
                        if (e instanceof SocketException ||
                                e instanceof UnknownHostException ||
                                e instanceof SocketTimeoutException) {
                            count++;
                            if (count > 5)
                                break;
                            continue;
                        }
                        listener.onFailure(e);
                    }
                }
            }
        }.start();
    }

    public static void getJapanVideoListAsync(final int currentSize, final String url, final HttpRequestCallbackListener listener) {
        new Thread() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        List<Data> result = new ArrayList<>();
                        Document document = Jsoup.connect(url).get();
                        Elements links = document.select("p[class]"),
                                src = document.select("img[data-original]");
                        Elements link = new Elements();
                        for (Element tmp : links)
                            if (tmp.attr("class").equals("content"))
                                link.add(tmp);
                        for (int i = currentSize; i < link.size(); i++) {
                            if (result.size() < 10)
                                result.add(new Data(handlerString2(link.get(i).text()), src.get(i).attr("abs:data-original"), handlerString(src.get(i).attr("alt"))));
                            else break;
                        }
                        listener.onSuccess(result, "");
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e instanceof ConnectException)
                            continue;
                        if (e instanceof SocketException ||
                                e instanceof UnknownHostException ||
                                e instanceof SocketTimeoutException) {
                            count++;
                            if (count > 5)
                                break;
                            continue;
                        }
                        listener.onFailure(e);
                    }
                }
            }
        }.start();
    }

    public static void getEuropeVideoListAsync(final String url, final HttpRequestCallbackListener listener) {
        new Thread() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        List<Data> result = new ArrayList<>();
                        String nextPage = "";
                        Document document = Jsoup.connect(url).get();
                        Elements src = document.select("img[src]"), links = new Elements(), texts = document.select("h2"),
                                nextPageTmp = document.select("link[rel]");
                        for (Element tmp : texts) {
                            links.add(tmp.parent());
                        }
                        for (Element tmp : nextPageTmp)
                            if (tmp.attr("rel").equals("next"))
                                nextPage = tmp.attr("abs:href");
                        src.remove(0);
                        for (int i = 0; i < links.size(); i++)
                            result.add(new Data(links.get(i).attr("abs:href"), src.get(i).attr("abs:src"), texts.get(i).text()));

                        listener.onSuccess(result, nextPage);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e instanceof ConnectException)
                            continue;
                        if (e instanceof SocketException ||
                                e instanceof UnknownHostException ||
                                e instanceof SocketTimeoutException) {
                            count++;
                            if (count > 5)
                                break;
                            continue;
                        }
                        listener.onFailure(e);
                    }
                }
            }
        }.start();
    }

    private static String handlerString2(String text) {
        int pos = text.indexOf("'", 19);
        return text.substring(19, pos);
    }

    private static String handlerString(String src) {
        return src.replaceAll("点击播放", "");
    }

    private static String getChinaVideoUrlByUrl(String url) {
        int count = 0;
        while (true) {
            try {
                Document document = Jsoup.parse(new URL(url), 8000);
                Elements links = document.select("source");
                for (Element element : links) {
                    if (element.attr("type").equals("video/mp4"))
                        return element.attr("abs:src");
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof ConnectException)
                    continue;
                if (e instanceof HttpStatusException)
                    return "";
                if (e instanceof SocketException ||
                        e instanceof UnknownHostException ||
                        e instanceof SocketTimeoutException) {
                    count++;
                    if (count > 5)
                        break;
                    continue;
                }
            }
        }
        return "";
    }

    public static String getEuropeVideoUrlByUrl(String url) {
        int count = 0;
        while (true) {
            try {
                Document document = Jsoup.connect(url).get();
                Elements elements = document.select("a[class]");
                for (Element tmp : elements)
                    if (tmp.attr("class").equals("play"))
                        return tmp.attr("href");
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof ConnectException)
                    continue;
                if (e instanceof SocketException ||
                        e instanceof UnknownHostException ||
                        e instanceof SocketTimeoutException) {
                    count++;
                    if (count > 5)
                        break;
                    continue;
                }
                break;
            }
        }
        return "";
    }

    public interface HttpRequestCallbackListener {

        void onSuccess(List<Data> data, String nextPage);

        void onFailure(Exception e);
    }
}