import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by wuyr on 6/16/16 7:40 PM.
 */
public class HttpTest {

    private static final String
            REASON_SERVER_404 = "无法找到资源。(服务器404)\t(向右滑动清除)",
            REASON_CONNECT_SERVER_FAILURE = "连接服务器失败，请检查网络后重试。\t(向右滑动清除)",
            REASON_INTERNET_NO_GOOD = "网络不给力，请重试。\t(向右滑动清除)";

    public static void main(final String[] args) throws Exception {
        final String url;
        if (args.length != 0)
            url = args[0];
        else
            url = "http://www.55caiji.com/vodlist/5.html";

        Document document = getDocument("http://www.55caiji.com/vod/1904.html");
        //div class="vod ok"<div class="gqlist"
        String list = Jsoup.parse(document.select("td").last().text()).select("div[class=gqlist]").get(0).text();
        println(list);
        List<String> links = new ArrayList<>();
        int count = 2;
        for (int i = 1; i <= count; i++) {
            links.add(list.substring(list.indexOf("http"), list.indexOf(".m3u8") + 5));
            list = list.substring(list.indexOf("集") + 1);
        }
        println("\n");
        for (String tmp : links)
            println(tmp);
        /*runOnBackground(listener, new BackgroundLogic() {
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
        });*/
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

    private static String handleString4(String src) {
        return src.replaceAll("邪恶", "").replaceAll("动态", "").
                replaceAll("gif", "").replaceAll("搞笑", "").
                replaceAll("妹子有图有声", "").replaceAll("图片", "").replaceAll("图", "");
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

    private static String handleString3(String src) {
        if (src.substring(0, 2).equals("　　"))
            src = src.substring(2, src.length());
        return src.replaceAll("\\s+", "\n");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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

    private static String getWebBaseUri() {
        try {
            return getDocument("http://52avzy.com").select("a").get(0).attr("abs:href");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).ignoreContentType(true).timeout(4000).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
    }

    private static HttpRequestCallbackListener listener = new HttpRequestCallbackListener() {
        @Override
        public void onSuccess(List<Data> data, String args) {
            println(args);
            if (data != null) {
                println("currentPage: " + data.get(0).getCurrentPage());
                println("totalPages: " + data.get(0).getTotalPages());
                println("nextPageUrl: " + args);
                for (Data tmp : data)
                    printf("%s\t%s\t%s\t%s\n", tmp.getUrl(), tmp.getSrc(), tmp.getText(), tmp.getDate());
            }
        }

        @Override
        public void onFailure(Exception e, String reason) {
            println(reason);
            e.printStackTrace();
        }
    };

    private static void print(Object s) {
        System.out.print(String.valueOf(s));
    }

    private static void printf(String src, Object... args) {
        System.out.printf(src, args);
    }

    private static void println(Object s) {
        System.out.println(String.valueOf(s));
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
                            count2++;
                            if (count2 > 3) {
                                listener.onFailure(e, REASON_INTERNET_NO_GOOD);
                                break;
                            }
                            continue;
                        }
                        if (e instanceof SocketException ||
                                e instanceof UnknownHostException ||
                                e instanceof SocketTimeoutException) {
                            count++;
                            if (count > 3) {
                                String reason = REASON_CONNECT_SERVER_FAILURE;
                                if (e instanceof SocketTimeoutException)
                                    reason = REASON_INTERNET_NO_GOOD;
                                listener.onFailure(e, reason);
                                break;
                            }
                            continue;
                        }
                        listener.onFailure(e, e instanceof HttpStatusException ? REASON_SERVER_404 : REASON_CONNECT_SERVER_FAILURE);
                        break;
                    }
                }
            }
        }.start();
    }

    interface HttpRequestCallbackListener {

        void onSuccess(List<Data> data, String args);

        void onFailure(Exception e, String reason);
    }

    private interface BackgroundLogic {
        void run() throws Exception;
    }

    static class Data {

        private String url, src, text, date, nextPageUrl;
        private int currentPage, totalPages;
        private boolean isFavorites;

        Data(String url, String src, String text, String date) {
            this.url = url;
            this.src = src;
            this.text = text;
            this.date = date;
        }

        Data(String url, String text, String date) {
            this(url, "", text, date);
        }

        String getUrl() {
            return url == null ? "" : url;
        }

        String getSrc() {
            return src == null ? "" : src;
        }

        String getText() {
            return text == null ? "" : text;
        }

        String getDate() {
            return date == null ? "" : date;
        }

        String getNextPageUrl() {
            return nextPageUrl == null ? "" : nextPageUrl;
        }

        void setNextPageUrl(String nextPageUrl) {
            this.nextPageUrl = nextPageUrl;
        }

        int getCurrentPage() {
            return currentPage;
        }

        void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        int getTotalPages() {
            return totalPages;
        }

        void setDate(String date) {
            this.date = date;
        }

        void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        boolean isFavorites() {
            return isFavorites;
        }

        void setFavorites(boolean favorites) {
            isFavorites = favorites;
        }
    }

}