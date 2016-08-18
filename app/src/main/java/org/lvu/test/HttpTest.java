import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by wuyr on 6/16/16 7:40 PM.
 */
public class HttpTest {
    public static void main(String[] args) {
        String url;
        if (args.length != 0)
            url = args[0];
        else
            url = "http://554hu.com/Html/100/";
        int count = 0, count2 = 0;
        while (true) {
            try {
                String currentPage, previousPageUrl = "", nextPageUrl = "";
                System.out.println("start resolve url: " + url);
                Document document = Jsoup.connect(url).timeout(4000).get();
                System.out.println(document.html());
                System.out.println("resolve url finish");
                Elements li = document.select("ul").get(0).children();
                for (Element tmp : li) {
                    System.out.printf("%s\t%s\t%s\n", getJapanVideoUrlByUrl(tmp.child(0).attr("abs:href")),
                            tmp.select("img").get(0).attr("src"), tmp.select("h3").text());
                }
                Elements pagination = document.select("div[class=pagination]").get(0).children();
                currentPage = pagination.select("span").text();
                for (Element tmp : pagination) {
                    if (tmp.text().equals("上一页") && tmp.tagName().equals("a"))
                        previousPageUrl = tmp.attr("abs:href");
                    if (tmp.text().equals("下一页") && tmp.tagName().equals("a"))
                        nextPageUrl = tmp.attr("abs:href");
                }
                System.out.printf("currentPage: %s\npreviousPageUrl: %s\nnextPageUrl: %s\n",
                        currentPage, previousPageUrl, nextPageUrl);
                break;
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
    }

    // TODO: 8/18/16 less than 50k no need to compress

    private static String getJapanVideoUrlByUrl(String url) {
        int count = 0, count2 = 0;
        while (true) {
            try {
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                /*
                <script type="text/javascript"
                 */
                System.out.println(document.select("a[title=在线播放]").get(0).attr("abs:href"));
                return getJapanVideoUrlByUrl2(document.select("a[title=在线播放]").get(0).attr("abs:href"));
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

    private static String getJapanVideoUrlByUrl2(String url) {
        int count = 0, count2 = 0;
        while (true) {
            try {
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                /*
                <script type="text/javascript"
                 */
                return handleString8(document.select("script[type=text/javascript]").get(9).html());
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

    private static String handleString8(String src) {
        src = new StringBuilder(src).reverse().toString();
        return "http://v2.14mp4.com" + new StringBuilder(
                src.substring(src.indexOf("8u3m."), src.indexOf("-eivom/") + 7)).reverse().toString();
    }
}
