import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
            url = "http://www.9527shequ.com/so/wz.php";

        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage, totalPages;
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
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

    private static String handleString7(String src) {
        return src.substring(src.indexOf("当前:") + 3, src.indexOf("页"));
    }

    private static String getChinaVideoUrl() {
        return getVideoBaseUri() + "/sj/vod-show-id-1-p-1.html";
    }

    private static String getEuropeVideoUrl() {
        return getVideoBaseUri() + "/sj/vod-show-id-3-p-1.html";
    }

    private static String getJapanVideoUrl() {
        return getVideoBaseUri() + "/sj/vod-show-id-2-p-1.html";
    }

    private static String getVideoBaseUri() {
        try {
            return Jsoup.connect("http://www.9527shequ.com/so/wz.php").timeout(4000)
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get().baseUri();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getPlayerUrl() {
        try {
            Document document =  Jsoup.connect(getVideoBaseUri() + "/g/playerurl/geturl.php").timeout(4000)
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();

        } catch (IOException e) {
            e.printStackTrace();
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

    private static void printf(String src, Object... args) {
        System.out.printf(src, args);
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