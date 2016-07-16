
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by wuyr on 6/16/16 7:40 PM.
 */
public class HttpTest {
    public static void main(String[] args) {
        String url = args[0];
        System.out.println("connecting " + url);
        int count = 0;
        while (true) {
            try {
                //script 5
                Document document = Jsoup.connect(url).timeout(6000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                String script = document.select("script").get(5).html();
                System.out.println(script);
                System.out.println(getChinaVideoPlayerUrl() + handleString5(script));
                /*String currentPage, nextPageUrl, previousPageUrl;
                Document document = Jsoup.connect(url).timeout(6000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                Elements li = document.select("ul").last().children();
                for (Element tmp : li) {
                    Elements items = tmp.child(0).children();
                    System.out.printf("%s\t%s\t%s\n", items.get(1).select("a").attr("abs:href"),
                            items.select("img").get(0).attr("abs:src"),
                            items.get(1).child(0).text());
                }
                Elements page = document.select("div[id]").last().children();
                currentPage = page.select("span").get(0).text();
                previousPageUrl = page.get(1).tagName().equals("em") ? "" : page.get(1).attr("abs:href");
                nextPageUrl = page.get(page.size() - 4).tagName().equals("em") ? "" :
                        page.get(page.size() - 4).attr("abs:href");
                System.out.printf("currentPage: %s\nnextPageUrl: %s\npreviousPageUrl: %s\n",
                        currentPage, nextPageUrl, previousPageUrl);*/
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
    }

    private static String handleString5(String src) {
        return src.substring(src.indexOf("l+\'") + 3, src.indexOf('\'',src.indexOf("l+\'") + 4));
    }

    private static String getChinaVideoPlayerUrl() {
        int count = 0, count2 = 0;
        while (true) {
            try {
                Document document = Jsoup.connect("https://www.vmfh.info/g/playerurl/geturl.php").timeout(6000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                String html = document.body().html();
                return html.substring(14, html.length() - 2);
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof ConnectException) {
                    count2++;
                    if (count2 > 5)
                        break;
                    continue;
                }
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
}
