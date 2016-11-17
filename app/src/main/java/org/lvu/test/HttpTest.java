import org.jsoup.HttpStatusException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by wuyr on 6/16/16 7:40 PM.
 */
public class HttpTest {

    private static final String
            REASON_SERVER_404 = "无法找到资源。(服务器404)\t(向右滑动清除)",
            REASON_CONNECT_SERVER_FAILURE = "连接服务器失败，请检查网络后重试。\t(向右滑动清除)",
            REASON_INTERNET_NO_GOOD = "网络不给力，请重试。\t(向右滑动清除)";

    public static void main(String[] args) throws Exception{
        /*final String url;
        if (args.length != 0)
            url = args[0];
        else
            url = "http://fv3333.com/html/part/9.html";

        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "", currentPage = "", previousPageUrl = "";
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                //<div class="content">
                Elements li = document.select("div[class=content]").get(0).children();
                Elements li2 = li.select("img[src]");
                for (Element tmp : li2) {
                    result.add(new Data("", tmp.attr("abs:src"), "", 4));
                    listener.onSuccess(result, nextPageUrl);
                    result = new ArrayList<>();
                }
                listener.onSuccess(null, nextPageUrl);
            }
        });*/
    }

    // TODO: 8/18/16 less than 50k no need to compress
    private static String handleString2(String src) {
        return src.replaceAll("<br>", "\n").replaceAll("&nbsp;", "").replaceAll("<p>", "").replaceAll("</p>", "");
    }

    private static HttpRequestCallbackListener listener = new HttpRequestCallbackListener() {
        @Override
        public void onSuccess(List<Data> data, String args) {
            if (data != null)
                for (Data tmp : data)
                    printf("%s\t%s\n", tmp.getSrc(), tmp.getText());
            else
                println("args: " + args + "\n");
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

        public String getUrl() {
            return url;
        }

        public String getText() {
            return text;
        }

        public String getSrc() {
            return src;
        }
    }

}