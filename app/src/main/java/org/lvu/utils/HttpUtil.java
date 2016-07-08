package org.lvu.utils;

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
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPage = "";
                Document document = Jsoup.parse(new URL(url), 6000);
                Elements links = document.select("li"),
                        src = document.select("img[src]"),
                        link = links.select("a[href][title][target]");
                for (int i = 0; i < link.size(); i++) {
                    String videoUrl = getChinaVideoUrlByUrl(link.get(i).attr("abs:href"));
                    if (!videoUrl.isEmpty()) {
                        result.add(new Data(videoUrl,
                                src.get(i).attr("abs:src"),
                                link.get(i).attr("title")));
                        listener.onSuccess(result, nextPage);
                        result = new ArrayList<>();
                    }
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
                listener.onSuccess(null, nextPage);
            }
        });
    }

    public static void getEuropeVideoListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPage = "";
                Document document = Jsoup.parse(new URL(url), 6000);
                Elements src = document.select("img[src]"), links = new Elements(), texts = document.select("h2"),
                        nextPageTmp = document.select("link[rel]");
                for (Element tmp : texts) {
                    links.add(tmp.parent());
                }
                src.remove(0);
                for (int i = 0; i < links.size(); i++) {
                    result.add(new Data(links.get(i).attr("abs:href"), src.get(i).attr("abs:src"), texts.get(i).text()));
                    listener.onSuccess(result, nextPage);
                    result = new ArrayList<>();
                }
                for (Element tmp : nextPageTmp)
                    if (tmp.attr("rel").equals("next"))
                        nextPage = tmp.attr("abs:href");
                listener.onSuccess(null, nextPage);
            }
        });
    }

    public static void getJapanVideoListAsync(final int currentSize, final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                Document document = Jsoup.parse(new URL(url), 6000);
                Elements links = document.select("p[class]"),
                        src = document.select("img[data-original]");
                Elements link = new Elements();
                for (Element tmp : links)
                    if (tmp.attr("class").equals("content"))
                        link.add(tmp);
                int flag = result.size();
                for (int i = currentSize; i < link.size(); i++) {
                    if (flag < 10) {
                        result.add(new Data(handlerString2(link.get(i).text()),
                                src.get(i).attr("abs:data-original"), handlerString(src.get(i).attr("alt"))));
                        listener.onSuccess(result, "");
                        result = new ArrayList<>();
                        flag ++;
                    } else break;
                }
                listener.onSuccess(null, "");
            }
        });
    }

    public static void getPicturesListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPage = "";
                Document document = Jsoup.parse(new URL(url), 6000);
                Elements elements = document.select("li");
                for (Element tmp : elements) {
                    result.add(new Data(tmp.children().get(1).attr("abs:href"), tmp.children().get(1).text()));
                    listener.onSuccess(result, nextPage);
                    result = new ArrayList<>();
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
                listener.onSuccess(null, nextPage);
            }
        });
    }

    public static void getComicsListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPage = "";
                Document document = Jsoup.parse(new URL(url), 6000);
                Elements elements = document.select("ul[class]"),
                        li = new Elements(), a, img, next = document.select("a");
                for (Element tmp : elements)
                    if (tmp.attr("class").equals("piclist listcon"))
                        li = tmp.children();
                a = li.select("a");
                img = li.select("img");

                for (int i = 0; i < a.size(); i++) {
                    result.add(new Data(a.get(i).attr("abs:href"),
                            img.get(i).attr("abs:xSrc"), handlerString3(a.get(i).attr("title"))));
                    listener.onSuccess(result, nextPage);
                    result = new ArrayList<>();
                }

                for (Element tmp : next)
                    if (tmp.text().equals("下一页"))
                        nextPage = tmp.attr("abs:href");
                listener.onSuccess(null, nextPage);
            }
        });
    }

    public static void getNovelListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPage = "";
                Document document = Jsoup.parse(new URL(url), 6000);
                Elements elements = document.select("ul"), data = new Elements();
                for (Element tmp : elements)
                    if (tmp.children().size() == 2)
                        data.add(tmp.children().get(0).children().get(1));
                for (Element tmp : data) {
                    result.add(new Data(tmp.attr("abs:href"), tmp.text()));
                    listener.onSuccess(result, nextPage);
                    result = new ArrayList<>();
                }

                Elements next = document.select("div[id]"), next2 = null;
                for (Element tmp : next) {
                    if (tmp.attr("id").equals("page"))
                        next2 = tmp.children();
                }
                if (next2 != null)
                    nextPage = next2.get(next2.size() - 3).attr("abs:href");
                listener.onSuccess(null, nextPage);
            }
        });
    }

    public static void getPicturesAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPage = "";
                Document document = Jsoup.parse(new URL(url), 6000);
                Elements elements = document.select("img");
                elements.remove(elements.size() - 1);
                for (Element tmp : elements) {
                    result.add(new Data("", tmp.attr("abs:src"), ""));
                    listener.onSuccess(result, nextPage);
                    result = new ArrayList<>();
                }
                listener.onSuccess(null, nextPage);
            }
        });
    }

    public static void getNovelContentAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                Document document = Jsoup.parse(new URL(url), 6000);
                Elements elements = document.select("div[id]");
                String content = "";
                for (Element tmp : elements)
                    if (tmp.attr("id").equals("view2"))
                        content = tmp.html();
                if (content.isEmpty())
                    listener.onFailure(new Exception("novel content is empty!"));
                else
                    listener.onSuccess(null, handlerNovelContent(content));
            }
        });
    }

    public static void getComicsContentAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                result.add(new Data("", Jsoup.parse(new URL(url), 6000).select("img[alt]").get(0).attr("abs:src"), ""));
                listener.onSuccess(result, "");
            }
        });
    }

    private static void runOnBackground(final HttpRequestCallbackListener listener, final BackgroundLogic backgroundLogic) {
        new Thread() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        backgroundLogic.run();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e instanceof ConnectException)// TODO: 6/24/16 check internet can use?
                            continue;
                        if (e instanceof SocketException ||
                                e instanceof UnknownHostException ||
                                e instanceof SocketTimeoutException) {
                            count++;
                            if (count > 5) {
                                listener.onFailure(e);
                                break;
                            }
                            continue;
                        }
                        listener.onFailure(e);
                        break;
                    }
                }
            }
        }.start();
    }

    private static String handlerString(String src) {
        return src.replaceAll("点击播放", "");
    }

    private static String handlerString2(String text) {
        int pos = text.indexOf("'", 19);
        return text.substring(19, pos);
    }

    private static String handlerString3(String src) {
        return src.replaceAll("邪恶漫画", "");
    }

    private static String handlerNovelContent(String src) {
        return src.replaceAll("<br>", "\n").replaceAll("<p>", "").replaceAll("</p>", "\n")
                .replaceAll("&nbsp;", "").replaceAll("<b>", "").replaceAll("</b>", "");
    }

    private static String getChinaVideoUrlByUrl(String url) {
        int count = 0;
        while (true) {
            try {
                Document document = Jsoup.parse(new URL(url), 6000);
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

    public static String getEuropeVideoUrlByUrl(String url) {
        int count = 0;
        while (true) {
            try {
                Document document = Jsoup.parse(new URL(url), 6000);
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

    private interface BackgroundLogic {
        void run() throws Exception;
    }
}