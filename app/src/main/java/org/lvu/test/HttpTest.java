import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.println;

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
            url = "http://www.52kkm.org/xieemanhua/";

        runOnBackground(listener, new BackgroundLogic() {
            @Override
            public void run() throws Exception {
                List<Data> result = new ArrayList<>();
                String nextPageUrl = "";
                int currentPage = 0, totalPages = 0;
                /*Document document = getDocument(url);
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
                result.get(0).setTotalPages(totalPages);*/
                FileInputStream fis = null;
                try {
                    Gson gson = new Gson();
                    String json;
                    fis = new FileInputStream("./jsonTest");
                    BufferedReader br = new BufferedReader(new FileReader("./jsonTest"));
                    String tmp;
                    StringBuilder sb = new StringBuilder();
                    while ((tmp = br.readLine()) != null)
                        sb.append(tmp);
                    json = sb.toString();
                    println(json);
                    result = gson.fromJson(json, new TypeToken<ArrayList<Data>>() {
                    }.getType());
                    listener.onSuccess(result, nextPageUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null)
                        fis.close();
                }
            }
        });
    }

    private static String getVideoBaseUri(String baseUrl) throws Exception {
        Document document = getDocument(getBaseUrlByUrl(baseUrl) + "/g/playerurl/geturl.php");
        String html = document.html();
        return html.substring(html.indexOf("\"") + 1, html.lastIndexOf("\""));
    }

    private static String handleString(String src) {
        return src.replaceAll("邪恶漫画", "");
    }

    private static String handleString7(String src) {
        return src.substring(src.indexOf("当前:") + 3, src.indexOf("页"));
    }

    private static String getChinaVideoUrl() {
        return getWebBaseUri() + "/sj/vod-show-id-1-p-1.html";
    }

    private static String getEuropeVideoUrl() {
        return getWebBaseUri() + "/sj/vod-show-id-3-p-1.html";
    }

    private static String getJapanVideoUrl() {
        return getWebBaseUri() + "/sj/vod-show-id-2-p-1.html";
    }

    private static String getWebBaseUri() {
        int count = 0, count2 = 0;
        while (true) {
            try {
                return Jsoup.connect("http://www.9527shequ.com/so/wz.php").timeout(4000)
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

    private static String getBaseUrlByUrl(String url) {
        return url.substring(0, url.indexOf(".com") + 4);
    }

    private static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).timeout(4000).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
    }

    private static String getPlayerUrl() {
        try {
            Document document = Jsoup.connect(getWebBaseUri() + "/g/playerurl/geturl.php").timeout(4000)
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
                /*Gson gson = new Gson();
                String json = gson.toJson(data);
                println(json);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream("./jsonTest");
                    fos.write(json.getBytes());
                    fos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (fos != null)
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
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