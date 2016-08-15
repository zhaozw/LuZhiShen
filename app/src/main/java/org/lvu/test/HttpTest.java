
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
            url = "http://www.lovefou.com/dongtaitu/";
        int count = 0, count2 = 0;
        while (true) {
            try {
                String currentPage, previousPageUrl = "", nextPageUrl = "";
                System.out.println("start resolve url: " + url);
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                System.out.println("resolve url finish");
                Elements items = document.select("div[class=lovefou]").get(0).children();
                for (Element li : items) {
                    Element tmp = li.children().get(0);
                    System.out.printf("%s\t%s\t%s\n", tmp.attr("abs:href"),
                            tmp.child(0).attr("src"), tmp.child(0).attr("alt"));
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
}
