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
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by wuyr on 4/7/16 1:24 PM.
 */
public class HttpUtil {

    public static final String REASON_NO_INTERNET_CONNECT = "无可用网络。\t(向右滑动清除)",
            REASON_SERVER_404 = "无法找到资源。(服务器404)\t(向右滑动清除)",
            REASON_CONNECT_SERVER_FAILURE = "连接服务器失败，请检查网络后重试。\t(向右滑动清除)",
            REASON_INTERNET_NO_GOOD = "网络不给力，请重试。\t(向右滑动清除)";
    private static ExecutorService mThreadPool;
    private static OkHttpClient mHttpClient;

    static {
        mThreadPool = Executors.newFixedThreadPool(7);
        mHttpClient = Application.getOkHttpClient();
    }

    public static void getVideoListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String baseUrl = url.contains(".com") ? "" : getWebBaseUri();
                String nextPageUrl = "";
                int currentPage, totalPages;
                Document document = getDocument(baseUrl + url);
                //<ul onmouseover="this.className='row1'" onmouseout="this.className='row'"
                Elements ul = document.select("ul[onmouseover=this.className='row1']");
                for (Element tmp : ul) {
                    Element a = tmp.select("a[title]").get(0);
                    String[] imgAndUrl = getVideoUrlAndVideoImg(a.attr("abs:href"));
                    result.add(new Data(imgAndUrl[0], handleSpacesUrl(imgAndUrl[1]), a.attr("title"), tmp.child(0).lastElementSibling().text()));
                }
                Element page = document.select("div[class=pages]").get(0).child(0);
                Elements pages = page.children();
                totalPages = toInt(page.ownText().split("/")[1].replaceAll("页", ""));
                currentPage = Integer.parseInt(pages.select("span").get(0).text());
                for (Element tmp : pages) {
                    if (tmp.nodeName().equals("a") && tmp.text().equals("下一页")) {
                        nextPageUrl = baseUrl + tmp.attr("href");
                        break;
                    }
                }
                if (currentPage == totalPages)
                    nextPageUrl = "";
                result.get(0).setCurrentPage(currentPage);
                result.get(0).setTotalPages(totalPages);
                listener.onSuccess(result, nextPageUrl);
            }
        });
    }

    public static void getPicturesListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String baseUrl = url.contains(".com") ? "" : getWebBaseUri();
                String nextPageUrl = "";
                int currentPage, totalPages;
                Document document = getDocument(baseUrl + url);
                Elements ul = document.select("ul[onmouseover=this.className='row1']");
                for (Element tmp : ul) {
                    Element a = tmp.select("a[title]").get(0);
                    String url = a.attr("abs:href");
                    result.add(new Data(url, handleSpacesUrl(getFirstPic(url)), a.attr("title"), tmp.child(0).lastElementSibling().text()));
                }
                Element page = document.select("div[class=pages]").get(0).child(0);
                Elements pages = page.children();
                totalPages = toInt(page.ownText().split("/")[1].replaceAll("页", ""));
                currentPage = Integer.parseInt(pages.select("span").get(0).text());
                for (Element tmp : pages) {
                    if (tmp.nodeName().equals("a") && tmp.text().equals("下一页")) {
                        nextPageUrl = baseUrl + tmp.attr("href");
                        break;
                    }
                }
                if (currentPage == totalPages)
                    nextPageUrl = "";
                result.get(0).setCurrentPage(currentPage);
                result.get(0).setTotalPages(totalPages);
                listener.onSuccess(result, nextPageUrl);
            }
        });
    }

    public static void getNovelListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String baseUrl = url.contains(".com") ? "" : getWebBaseUri();
                String nextPageUrl = "";
                int currentPage, totalPages;
                Document document = getDocument(baseUrl + url);
                //<ul onmouseover="this.className='row1'" onmouseout="this.className='row'"
                Elements ul = document.select("ul[onmouseover=this.className='row1']");
                for (Element tmp : ul) {
                    Element a = tmp.select("a[title]").get(0);
                    result.add(new Data(a.attr("abs:href"), a.attr("title"), tmp.child(0).lastElementSibling().text()));
                }
                Element page = document.select("div[class=pages]").get(0).child(0);
                Elements pages = page.children();
                totalPages = toInt(page.ownText().split("/")[1].replaceAll("页", ""));
                currentPage = Integer.parseInt(pages.select("span").get(0).text());
                for (Element tmp : pages) {
                    if (tmp.nodeName().equals("a") && tmp.text().equals("下一页")) {
                        nextPageUrl = baseUrl + tmp.attr("href");
                        break;
                    }
                }
                if (currentPage == totalPages)
                    nextPageUrl = "";
                result.get(0).setCurrentPage(currentPage);
                result.get(0).setTotalPages(totalPages);
                listener.onSuccess(result, nextPageUrl);
            }
        });
    }

    private static String getWebBaseUri() {
        int count = 0, count2 = 0;
        while (true) {
            try {
                return getDocument("http://52avzy.com").select("a").get(0).attr("abs:href");
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

    public static void getComicsListAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage = 0, totalPages = 0;
                Document document = getDocument2(url);
                Elements li = document.select("ul[class=piclist listcon").get(0).children();
                for (Element tmp : li) {
                    Element a = tmp.child(0);
                    result.add(new Data(a.attr("abs:href"), a.child(2).attr("xSrc"), handleString(a.child(0).text()), ""));
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
                            new Data(readMore.get(0).child(0).attr("abs:href"), handleString3(tmp.text()), "") :
                            new Data(null, handleString3(tmp.text()), ""));
                }
                Elements times = document.select("time");
                Iterator<Element> iterator = times.iterator();
                while (iterator.hasNext()) {
                    Element tmp = iterator.next();
                    if (tmp.hasClass("time"))
                        iterator.remove();
                }
                for (int i = 0; i < times.size(); i++)
                    result.get(i).setDate(times.get(i).text());
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

    public static void getGifList(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage = 0, totalPages = 0;
                Document document = getDocument2(url);
                Elements items = document.select("div[class=lovefou]").get(0).children();
                for (Element li : items) {
                    Element tmp = li.children().get(0);
                    result.add(new Data(getGifUrl(tmp.attr("abs:href")), tmp.child(0).attr("src"), handleString4(tmp.child(0).attr("alt")), tmp.lastElementSibling().text()));
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

    private static Document getDocument2(String url) throws IOException {
        return Jsoup.connect(url).timeout(8000).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
    }

    public static void getPicturesAsync(final String url, final HttpRequestCallbackListener listener) {
        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage = 0, totalPages = 0;
                Document document = Jsoup.parse(getDocument(url).select("textarea[id=nr]").get(0).text());
                Elements img = document.select("img[src]");
                for (Element tmp : img)
                    result.add(new Data(handleSpacesUrl(tmp.attr("src"))));

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
                Document document = getDocument(url);
                String content = document.select("textarea[id=nr]").get(0).text();
                if (content.isEmpty())
                    listener.onFailure(new Exception("novel content is empty!"), REASON_SERVER_404);
                else
                    listener.onSuccess(null, "\n" + handleString2(content));
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

    public static byte[] getGifFile(String url) throws Exception {
        return mHttpClient.newCall(new Request.Builder().url(url).build()).execute().body().bytes();
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

    private static Document getDocument(String url) throws Exception {
        return Jsoup.parse(mHttpClient.newCall(new Request.Builder().url(url).build()).execute().body().string(), url.substring(0, url.indexOf(".com") + 4));
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

    private static String handleString(String src) {
        return src.replaceAll("邪恶漫画", "");
    }

    private static String handleString2(String content) {
        return content.replaceAll("<div>", "").replaceAll("</div>", "").replaceAll("<br>", "\n");
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

    private static String handleSpacesUrl(String src) {
        String url = null;
        try {
            url = URLEncoder.encode(src, "utf-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (url != null)
            url = url.replaceAll("%3A", ":").replaceAll("%2F", "/");
        return url == null ? src : url;
    }

    private static String getFirstPic(String url) {
        String result = "";
        try {
            result = Jsoup.parse(getDocument(url).select("textarea[id=nr]").get(0).text()).select("img[src]").get(0).attr("abs:src");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String[] getVideoUrlAndVideoImg(String url) {
        String[] result = new String[2];
        try {
            Document document = getDocument(url);
            String s = document.select("td").last().html();
            result[0] = s.substring(s.lastIndexOf("&gt;http://") + 4, s.lastIndexOf(".m3u8&lt") + 5);
            result[1] = document.select("ul[class=xqimg]").get(0).select("img").get(0).attr("src");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    private static int toInt(String src) {
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(src);
        try {
            return Integer.parseInt(m.replaceAll("").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean isNetWorkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) Application.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
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