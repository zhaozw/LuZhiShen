import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wuyr on 6/16/16 7:40 PM.
 */
public class HttpTest {

    private static final String
            REASON_SERVER_404 = "无法找到资源。(服务器404)\t(向右滑动清除)",
            REASON_CONNECT_SERVER_FAILURE = "连接服务器失败，请检查网络后重试。\t(向右滑动清除)",
            REASON_INTERNET_NO_GOOD = "网络不给力，请重试。\t(向右滑动清除)";

    public static void main(String[] args) throws Exception {
        final String url;
        if (args.length != 0)
            url = args[0];
        else
            url = "http://www.lovefou.com/dongtaitu/list_4.html";

        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage = 0, totalPages = 0;
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                Elements items = document.select("div[class=lovefou]").get(0).children();
                for (Element li : items) {
                    Element tmp = li.children().get(0);
                    result.add(new Data(getGifUrl(tmp.attr("abs:href")), tmp.child(0).attr("src"), tmp.child(0).attr("alt")));
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

    private static HttpRequestCallbackListener listener = new HttpRequestCallbackListener() {
        @Override
        public void onSuccess(List<Data> data, String args) {
            if (data != null) {
                println("currentPage: " + data.get(0).getCurrentPage());
                println("totalPages: " + data.get(0).getTotalPages());
                println("nextPageUrl: " + args);
                for (Data tmp : data)
                    printf("%s\t%s\t%s\n", tmp.getUrl(), tmp.getSrc(), tmp.getText());
            }
        }

        @Override
        public void onFailure(Exception e, String reason) {
            println(reason);
            e.printStackTrace();
        }
    };

    private static void print(String s) {
        System.out.print(s);
    }

    private static void printf(String src, String... args) {
        System.out.printf(src, (Object[]) args);
    }

    private static void println(String s) {
        System.out.println(s);
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

        private String url, text, src;
        private int currentPage;
        private int totalPages;

        public Data(String url, String src, String text) {
            this.url = url;
            this.text = text;
            this.src = src;
        }

        public Data(String url, String text) {
            this(url, text, "");
        }

        public Data(String url, String src, String text, int scale) {
            this(url, src, text);
        }

        String getUrl() {
            return url;
        }

        String getText() {
            return text;
        }

        String getSrc() {
            return src;
        }

        void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }

}