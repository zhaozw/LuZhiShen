package org.lvu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lvu.Application;
import org.lvu.models.Data;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wuyr on 4/7/16 1:24 PM.
 */
public class HttpUtil {

    public static final String REASON_NO_INTERNET_CONNECT = "无可用网络。\t(向右滑动清除)",
            REASON_SERVER_404 = "无法找到资源。(服务器404)\t(向右滑动清除)",
            REASON_CONNECT_SERVER_FAILURE = "连接服务器失败，请检查网络后重试。\t(向右滑动清除)",
            REASON_INTERNET_NO_GOOD = "网络不给力，请重试。\t(向右滑动清除)";
    private static ExecutorService mThreadPool;

    static {
        mThreadPool = Executors.newFixedThreadPool(3);
    }

    public static void getChinaVideoListAsync(final int page, final HttpRequestCallbackListener listener) {
        mThreadPool.execute(new Thread() {
            @Override
            public void run() {
                getVideoListAsync(getChinaVideoUrl(page), listener);
            }
        });
    }

    public static void getEuropeVideoListAsync(final int page, final HttpRequestCallbackListener listener) {
        mThreadPool.execute(new Thread() {
            @Override
            public void run() {
                getVideoListAsync(getEuropeVideoUrl(page), listener);
            }
        });
    }

    public static void getJapanVideoListAsync(final int page, final HttpRequestCallbackListener listener) {
        mThreadPool.execute(new Thread() {
            @Override
            public void run() {
                getVideoListAsync(getJapanVideoUrl(page), listener);
            }
        });
    }

    private static void getVideoListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage, totalPages;
                Document document = getDocument(url);
                //<ul class="mainlist clearfix">
                Elements div = document.select("ul[class=mainlist clearfix]").get(0).select("div");

                for (Element tmp : div) {
                    result.add(new Data(tmp.child(1).child(0).attr("abs:href"),
                            tmp.child(0).child(0).child(0).attr("abs:src"), tmp.child(1).child(0).text()));
                }
                // /<div class='pagebox' id='_function_code_page'>
                Element pageBox = document.select("div[class=pagebox").get(0);
                Elements pageBoxes = pageBox.children();
                String[] page = handleString7(pageBox.ownText()).split("/");
                for (Element tmp : pageBoxes) {
                    if (tmp.nodeName().equals("a") && tmp.text().equals("下一页"))
                        nextPageUrl = tmp.attr("abs:href");
                }
                currentPage = Integer.parseInt(page[0]);
                totalPages = Integer.parseInt(page[1]);
                if (currentPage == totalPages)
                    nextPageUrl = "";
                result.get(0).setCurrentPage(currentPage);
                result.get(0).setTotalPages(totalPages);
                listener.onSuccess(result, nextPageUrl);
            }
        });
    }

    public static void getVideoUrlByUrl(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                String html = getDocument(url).select("div[class=mahua]").get(0).select("script").html();
                listener.onSuccess(null, getVideoBaseUri(url) + html.substring(html.lastIndexOf("l+'") + 3, html.lastIndexOf(".mp4'];") + 4));
            }
        });
    }

    private static String getVideoBaseUri(String baseUrl) throws Exception {
        Document document = getDocument(getBaseUrlByUrl(baseUrl) + "/g/playerurl/geturl.php");
        String html = document.html();
        return html.substring(html.indexOf("\"") + 1, html.lastIndexOf("\""));
    }

    private static String getBaseUrlByUrl(String url) {
        return url.substring(0, url.indexOf(".com") + 4);
    }

    private static String getChinaVideoUrl(int page) {
        return getWebBaseUri() + String.format(Locale.getDefault(), "/sj/vod-show-id-1-p-%d.html", page);
    }

    private static String getJapanVideoUrl(int page) {
        return getWebBaseUri() + String.format(Locale.getDefault(), "/sj/vod-show-id-2-p-%d.html", page);
    }

    private static String getEuropeVideoUrl(int page) {
        return getWebBaseUri() + String.format(Locale.getDefault(), "/sj/vod-show-id-3-p-%d.html", page);
    }

    private static String getWebBaseUri() {
        int count = 0, count2 = 0;
        while (true) {
            try {
                return Jsoup.connect("http://www.xiaoming000.com/so/wz.php").timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get().baseUri();
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof ConnectException) {
                    count2++;
                    if (count2 > 4)
                        break;
                    continue;
                }
                if (e instanceof SocketException ||
                        e instanceof UnknownHostException ||
                        e instanceof SocketTimeoutException) {
                    count++;
                    if (count > 4)
                        break;
                    continue;
                }
                break;
            }
        }
        return "";
    }

    public static void getPicturesListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage, totalPages;
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                //class="box list channel"
                Elements li = document.select("div[class=box list channel]").get(0).child(0).children();
                li.remove(0);
                for (Element tmp : li) {
                    result.add(new Data(tmp.child(0).attr("abs:href"), tmp.child(0).ownText()));
                }
                //<div class="pagination">
                Elements pagination = document.select("div[class=pagination").get(0).children();
                currentPage = Integer.parseInt(pagination.select("strong").get(0).text());
                String lastPageUrl = "";
                for (Element tmp : pagination) {
                    if (tmp.text().equals("下一页") && tmp.tagName().equals("a"))
                        nextPageUrl = tmp.attr("abs:href");
                    else if (tmp.text().equals("尾页") && tmp.tagName().equals("a"))
                        lastPageUrl = tmp.attr("abs:href");
                }
                totalPages = handleString6(lastPageUrl);
                if (currentPage == totalPages)
                    nextPageUrl = "";
                result.get(0).setCurrentPage(currentPage);
                result.get(0).setTotalPages(totalPages);
                listener.onSuccess(result, nextPageUrl);
            }
        });
    }

    public static void getNovelListAsync(final String url, final HttpRequestCallbackListener listener) {
        getPicturesListAsync(url, listener);
    }

    public static void getComicsListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage = 0, totalPages = 0;
                Document document = getDocument(url);
                Elements li = document.select("ul[class=piclist listcon").get(0).children();
                for (Element tmp : li) {
                    Element a = tmp.child(0);
                    result.add(new Data(a.attr("abs:href"), a.child(2).attr("xSrc"), handleString(a.child(0).text())));
                }
                Elements pageList = document.select("ul[class=pagelist]").get(0).children();
                try {
                    currentPage = Integer.parseInt(pageList.select("li[class=thisclass]").get(0).text());
                    totalPages = Integer.parseInt(pageList.select("span[class=pageinfo").get(0).children().get(0).text());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                Elements nextPageUrlList = pageList.select("a");
                for (Element tmp : nextPageUrlList)
                    if (tmp.text().equals("下一页")) {
                        nextPageUrl = tmp.attr("abs:href");
                        break;
                    }
                if (currentPage == totalPages)
                    nextPageUrl = "";
                result.get(0).setCurrentPage(currentPage);
                result.get(0).setTotalPages(totalPages);
                listener.onSuccess(result, nextPageUrl);
            }
        });
    }

    public static void getPicturesAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage = 0, totalPages = 0;
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                //<div class="content">
                Elements li = document.select("div[class=content]").get(0).children();
                Elements li2 = li.select("img[src]");
                for (Element tmp : li2)
                    result.add(new Data("", tmp.attr("abs:src"), ""));

                result.get(0).setCurrentPage(currentPage);
                result.get(0).setTotalPages(totalPages);
                listener.onSuccess(result, nextPageUrl);
            }
        });
    }

    public static void getNovelContentAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();

                String content = handleString2(document.select("div[class=content]").get(0).html());
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
                listener.onSuccess(ImageLoader.getInstance().loadImageSync(getDocument(url).select("img[alt]").get(0).attr("abs:src"), new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.NONE).build()));
            }
        });
    }

    public static void getJokeListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage = 0, totalPages = 0;
                Document document = getDocument(url);
                Elements elements = document.select("section[class=article-content]");
                Elements readMore;
                for (Element tmp : elements) {
                    result.add(!(readMore = tmp.children().select("strong[class=reader-more]")).isEmpty() ?
                            new Data(readMore.get(0).child(0).attr("abs:href"), handleString3(tmp.text())) :
                            new Data(null, handleString3(tmp.text())));
                }
                Elements page = document.select("div[class=mobile-pagenavi]").get(0).children();
                for (Element tmp : page) {
                    if (tmp.tagName().equals("span")) {
                        String[] pageInfo = tmp.text().split("/");
                        currentPage = Integer.parseInt(pageInfo[0]);
                        totalPages = Integer.parseInt(pageInfo[1]);
                    } else if ((tmp.attr("class").equals("mprev")))
                        nextPageUrl = tmp.attr("abs:href");
                }
                if (currentPage == totalPages)
                    nextPageUrl = "";
                result.get(0).setCurrentPage(currentPage);
                result.get(0).setTotalPages(totalPages);
                listener.onSuccess(result, nextPageUrl);
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
        Document document = getDocument(url);
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
                String nextPageUrl = "";
                int currentPage = 0, totalPages = 0;
                Document document = getDocument(url);
                Elements items = document.select("div[class=lovefou]").get(0).children();
                for (Element li : items) {
                    Element tmp = li.children().get(0);
                    result.add(new Data(getGifUrl(tmp.attr("abs:href")), tmp.child(0).attr("src"), handleString4(tmp.child(0).attr("alt"))));
                }
                Elements pagination = document.select("div[class=pagination]").get(0).child(0).children();
                for (Element tmp : pagination)
                    if (tmp.tagName().equals("a") && tmp.text().equals("下一页")) {
                        nextPageUrl = tmp.attr("abs:href");
                        break;
                    }
                try {
                    currentPage = Integer.parseInt(pagination.select("span[class=thisclass]").text());
                    //<span class="pageinfo">共94页1692条</span>
                    String tmp = pagination.select("span[class=pageinfo").text();
                    tmp = tmp.substring(1, tmp.indexOf("页"));
                    totalPages = Integer.parseInt(tmp);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (currentPage == totalPages)
                    nextPageUrl = "";
                result.get(0).setCurrentPage(currentPage);
                result.get(0).setTotalPages(totalPages);
                listener.onSuccess(result, nextPageUrl);
            }
        });
    }

    private static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).ignoreContentType(true).timeout(4000).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
    }

    private static String getGifUrl(String url) {
        int count = 0, count2 = 0;
        while (true) {
            try {
                return getDocument(url).select("div[class=dongtai]").get(0).select("img").get(0).attr("src");
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
        mThreadPool.execute(new Thread() {
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
        });
    }

    private static String handleString(String src) {
        return src.replaceAll("邪恶漫画", "");
    }

    private static String handleString2(String src) {
        return src.replaceAll("<br>", "\n").replaceAll("&nbsp;", "").replaceAll("<p>", "").replaceAll("</p>", "");
    }

    private static String handleString3(String src) {
        if (src.substring(0, 2).equals("　　"))
            src = src.substring(2, src.length());
        return src.replaceAll("\\s+", "\n");
    }

    private static String handleString4(String src) {
        return src.replaceAll("邪恶", "").replaceAll("动态", "").
                replaceAll("gif", "").replaceAll("搞笑", "").
                replaceAll("妹子有图有声", "").replaceAll("图片", "").replaceAll("图", "");
    }

    private static int handleString6(String src) {
        return Integer.parseInt(src.substring(src.indexOf("_") + 1, src.indexOf(".html")));
    }

    private static String handleString7(String src) {
        return src.substring(src.indexOf("当前:") + 3, src.indexOf("页"));
    }

    private static boolean isNetWorkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) Application.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public abstract static class HttpRequestCallbackListener {

        public void onSuccess(List<Data> data, String args) {
        }

        public void onSuccess(Bitmap bitmap) {
        }

        public void onFailure(Exception e, String reason) {
        }
    }

    private interface BackgroundLogic {
        void run() throws Exception;
    }
}