package org.lvu.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lvu.Application;
import org.lvu.model.Data;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyr on 4/7/16 1:24 PM.
 */
public class HttpUtil {

    public static final String REASON_NO_INTERNET_CONNECT = "无可用网络。\t(向右滑动清除)",
            REASON_SERVER_404 = "无法找到资源。(服务器404)\t(向右滑动清除)",
            REASON_CONNECT_SERVER_FAILURE = "连接服务器失败，请检查网络后重试。\t(向右滑动清除)",
            REASON_INTERNET_NO_GOOD = "网络不给力，请重试。\t(向右滑动清除)";

    // TODO: 7/17/16 if nextPageUrl == null show is last page if previousPageUrl == null show is first page
    public static void getChinaVideoListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String currentPage = "", nextPageUrl = "", previousPageUrl = "";
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                Elements li = document.select("ul[class=list]").get(0).children();
                for (Element tmp : li) {
                    if (!tmp.tagName().equals("li"))
                        break;
                    result.add(new Data(/*video url*/tmp.child(0).attr("abs:href"),
                                        /*img url*/tmp.child(0).child(0).attr("src"),
                                        /*title*/tmp.child(0).child(0).attr("alt"), true));
                    listener.onSuccess(result, nextPageUrl);
                    result = new ArrayList<>();
                }
                //pagination
                Elements pagination = document.select("div[class=pagebar]").get(0).children();
                for (Element tmp : pagination) {
                    if (tmp.tagName().equals("em") && tmp.hasClass("current"))
                        currentPage = tmp.text();
                    if (tmp.text().equals("上一页") && tmp.tagName().equals("a"))
                        previousPageUrl = tmp.attr("abs:href");
                    if (tmp.text().equals("下一页") && tmp.tagName().equals("a")) {
                        nextPageUrl = tmp.attr("abs:href");
                    }
                }
                listener.onSuccess(null, nextPageUrl);
            }
        });
    }

    public static void getEuropeVideoListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                Elements src = document.select("img[src]"), links = new Elements(), texts = document.select("h2"),
                        nextPageTmp = document.select("link[rel]");
                for (Element tmp : texts) {
                    links.add(tmp.parent());
                }
                src.remove(0);
                for (int i = 0; i < links.size(); i++) {
                    result.add(new Data(links.get(i).attr("abs:href"), src.get(i).attr("abs:src"), texts.get(i).text()));
                    listener.onSuccess(result, nextPageUrl);
                    result = new ArrayList<>();
                }
                for (Element tmp : nextPageTmp)
                    if (tmp.attr("rel").equals("next"))
                        nextPageUrl = tmp.attr("abs:href");
                listener.onSuccess(null, nextPageUrl);
            }
        });
    }

    public static void getJapanVideoListAsync(final String url, final HttpRequestCallbackListener listener) {
        getChinaVideoListAsync(url, listener);
    }

    public static void getPicturesListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String currentPage = "", previousPageUrl = "", nextPageUrl = "";
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000).get();
                Elements li = document.select("div[class=list]").get(0).children().get(1).children();
                for (Element tmp : li) {
                    result.add(new Data(tmp.child(1).attr("abs:href"),tmp.child(1).text()));
                    listener.onSuccess(result,nextPageUrl);
                    result = new ArrayList<>();
                }
                //pagination
                Elements pagination = document.select("div[class=page]").get(0).children();
                for (Element tmp : pagination) {
                    if (tmp.tagName().equals("em") && tmp.hasClass("current"))
                        currentPage = tmp.text();
                    if (tmp.text().equals("上一页") && tmp.tagName().equals("a"))
                        previousPageUrl = tmp.attr("abs:href");
                    if (tmp.text().equals("下一页") && tmp.tagName().equals("a"))
                        nextPageUrl = tmp.attr("abs:href");
                }
                listener.onSuccess(null, nextPageUrl);
            }
        });
    }

    public static void getNovelListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String currentPage, previousPageUrl = "", nextPageUrl = "";
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000).get();
                Elements li = document.select("div[class=typelist]").get(0).children();
                for (Element tmp : li) {
                    Element a = tmp.select("a").get(0);
                    result.add(new Data(a.attr("abs:href"),a.text()));
                    listener.onSuccess(result,nextPageUrl);
                    result = new ArrayList<>();
                }
                //pagination
                Elements pagination = document.select("div[class=right]").get(0).children().get(1).children();
                currentPage = pagination.select("font").get(0).text();
                for (Element tmp : pagination) {
                    if (tmp.text().equals("上一页") && tmp.tagName().equals("a"))
                        previousPageUrl = tmp.attr("abs:href");
                    if (tmp.text().equals("下一页") && tmp.tagName().equals("a"))
                        nextPageUrl = tmp.attr("abs:href");
                }
                listener.onSuccess(null, nextPageUrl);
            }
        });
    }

    public static void getComicsListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                Elements li = document.select("ul[class=piclist listcon]").get(0).children(), a, img, next = document.select("a");
                a = li.select("a");
                img = li.select("img");

                for (int i = 0; i < a.size(); i++) {
                    result.add(new Data(a.get(i).attr("abs:href"),
                            img.get(i).attr("abs:xSrc"), handleString(a.get(i).attr("title"))));
                    listener.onSuccess(result, nextPageUrl);
                    result = new ArrayList<>();
                }

                for (Element tmp : next)
                    if (tmp.text().equals("下一页"))
                        nextPageUrl = tmp.attr("abs:href");
                listener.onSuccess(null, nextPageUrl);
            }
        });
    }

    public static void getPicturesAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000).get();
                Elements li = document.select("div[class=center margintop border clear main]").get(0).children().select("img");
                li.remove(0);
                li.remove(li.size() - 1);
                for (Element tmp : li) {
                    LogUtil.print(tmp.attr("abs:src"));
                    result.add(new Data("", tmp.attr("abs:src"), "", 4));
                    listener.onSuccess(result, nextPageUrl);
                    result = new ArrayList<>();
                }
                listener.onSuccess(null, nextPageUrl);
            }
        });
    }

    public static void getNovelContentAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000).get();

                String content;
                try {
                    content = handleNovelContent(document.select("font[color]").get(1).html());
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                    content = handleNovelContent2(document.select("div[class=mtop]").get(0).html());
                }
                if (content.isEmpty())
                    listener.onFailure(new Exception("novel content is empty!"), REASON_SERVER_404);
                else
                    listener.onSuccess(null, "\n" + content);
            }
        });
    }

    public static void getComicsContentAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                result.add(new Data("", Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                        .get().select("img[alt]").get(0).attr("abs:src"), ""));
                listener.onSuccess(result, "");
            }
        });
    }

    public static void getJokeListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String currentPage = "", previousPageUrl = "", nextPageUrl = "";
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                Elements elements = document.select("section[class=article-content]");
                Elements readMore;
                for (Element tmp : elements) {
                    result.add(!(readMore = tmp.children().select("strong[class=reader-more]")).isEmpty() ?
                            new Data(readMore.get(0).child(0).attr("abs:href"), handleString3(tmp.text())) :
                            new Data(null, handleString3(tmp.text())));
                    listener.onSuccess(result, "");
                    result = new ArrayList<>();
                }
                Elements page = document.select("div[class=mobile-pagenavi]").get(0).children();
                for (Element tmp : page) {
                    if (tmp.tagName().equals("span"))
                        currentPage = handleString4(tmp.text());
                    else if (tmp.attr("class").equals("mnext"))
                        previousPageUrl = tmp.attr("abs:href");
                    else if ((tmp.attr("class").equals("mprev")))
                        nextPageUrl = tmp.attr("abs:href");
                }
                System.out.printf("currentPage: %s\npreviousPageUrl: %s\nnextPageUrl: %s\n",
                        currentPage, previousPageUrl, nextPageUrl);
                listener.onSuccess(null, nextPageUrl);
            }
        });
    }

    public static void readMoreJokeAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                listener.onSuccess(null, readMoreJoke(url));
            }
        });
    }

    private static String readMoreJoke(String url) throws Exception {
        StringBuilder result = new StringBuilder();
        Document document = Jsoup.connect(url).timeout(4000)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
        Elements elements = document.select("section[class=article-content]");
        for (Element tmp : elements)
            result.append(handleString3(tmp.text()));
        try {
            Element n = document.select("div[class=post-pagenavi]").get(0).children().get(0);
            if (n.text().equals("下页"))
                result.append(readMoreJoke(n.attr("abs:href")));
        } catch (Exception ignored) {
        }
        return result.toString();
    }

    public static void getGifList(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String currentPage, previousPageUrl = "", nextPageUrl = "";
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                Elements items = document.select("div[class=lovefou]").get(0).children();
                for (Element li : items) {
                    Element tmp = li.children().get(0);
                    result.add(new Data(getGifUrl(tmp.attr("abs:href")), tmp.child(0).attr("src"), tmp.child(0).attr("alt")));
                    listener.onSuccess(result, "");
                    result = new ArrayList<>();
                }
                Elements pagination = document.select("div[class=pagination]").get(0).child(0).children();
                for (Element tmp : pagination) {
                    if (tmp.tagName().equals("a")) {
                        if (tmp.text().equals("上一页"))
                            previousPageUrl = tmp.attr("abs:href");
                        if (tmp.text().equals("下一页"))
                            nextPageUrl = tmp.attr("abs:href");
                    }
                }
                currentPage = pagination.select("span[class=thisclass]").text();
                System.out.printf("currentPage: %s\npreviousPageUrl: %s\nnextPageUrl: %s\n",
                        currentPage, previousPageUrl, nextPageUrl);
                listener.onSuccess(null, nextPageUrl);
            }
        });
    }

    private static String getGifUrl(String url) {
        int count = 0, count2 = 0;
        while (true) {
            try {
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                return document.select("div[class=dongtai]").get(0).select("img").get(0).attr("src");
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof ConnectException) {
                    count2++;
                    if (count2 > 3)
                        break;
                    continue;
                }
                if (e instanceof SocketException ||
                        e instanceof UnknownHostException ||
                        e instanceof SocketTimeoutException) {
                    count++;
                    if (count > 3)
                        break;
                    continue;
                }
                break;
            }
        }
        return "";
    }

    private static void runOnBackground(final HttpRequestCallbackListener listener, final BackgroundLogic backgroundLogic) {
        new Thread() {
            @Override
            public void run() {
                int count = 0, count2 = 0;
                while (true) {
                    try {
                        backgroundLogic.run();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e instanceof ConnectException) {
                            if (!isNetWorkAvailable()) {
                                listener.onFailure(e, REASON_NO_INTERNET_CONNECT);
                                break;
                            } else {
                                count2++;
                                if (count2 > 3) {
                                    listener.onFailure(e, REASON_INTERNET_NO_GOOD);
                                    break;
                                }
                                continue;
                            }
                        }
                        if (e instanceof SocketException ||
                                e instanceof UnknownHostException ||
                                e instanceof SocketTimeoutException) {
                            count++;
                            if (count > 3) {
                                String reason = REASON_CONNECT_SERVER_FAILURE;
                                if (e instanceof SocketTimeoutException)
                                    reason = REASON_INTERNET_NO_GOOD;
                                if (!isNetWorkAvailable())
                                    reason = REASON_NO_INTERNET_CONNECT;
                                listener.onFailure(e, reason);
                                break;
                            }
                            continue;
                        }
                        listener.onFailure(e, e instanceof HttpStatusException ? REASON_SERVER_404 :
                                !isNetWorkAvailable() ? REASON_NO_INTERNET_CONNECT : REASON_CONNECT_SERVER_FAILURE);
                        break;
                    }
                }
            }
        }.start();
    }

    private static String handleString(String src) {
        return src.replaceAll("邪恶漫画", "");
    }

    private static String handleString3(String src) {
        if (src.substring(0, 2).equals("　　"))
            src = src.substring(2, src.length());
        return src.replaceAll("\\s+", "\n");
    }

    private static String handleString4(String src) {
        return src.split("/")[0];
    }

    private static String handleString5(String src) {
        return src.substring(32, src.indexOf(".mp4\";") + 4);
    }

    private static String handleNovelContent(String src) {
        return src.replaceAll("<br>","\n");
    }

    private static String handleNovelContent2(String src){
        return src.replaceAll("<p>","\n").replaceAll("</p>","\n").replaceAll("&quot;","").replaceAll("&nbsp;","");
    }

    public static void getChinaVideoUrlByUrl(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
               listener.onSuccess(null, handleString5(document.select("script").get(6).html()));
            }
        });
    }

    public static void getEuropeVideoUrlByUrl(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                listener.onSuccess(null, document.select("a[class=play]").get(0).attr("href"));
            }
        });
    }

    public static void getJapanVideoUrlByUrl(String url, final HttpRequestCallbackListener listener) {
        getChinaVideoUrlByUrl(url, listener);
    }

    private static boolean isNetWorkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) Application.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public interface HttpRequestCallbackListener {

        void onSuccess(List<Data> data, String args);

        void onFailure(Exception e, String reason);
    }

    private interface BackgroundLogic {
        void run() throws Exception;
    }
}