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
            url = "http://55ex.com/htm/2016/10/29/t02/364979.html";
        int count = 0, count2 = 0;
        while (true) {
            try {
                //print
                String currentPage, previousPageUrl = "", nextPageUrl = "";
                System.out.println("start resolve url: " + url);
                //start
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000).get();
                System.out.println("resolve url finish");
                //<div id="view2" class="mtop">
                Element li = document.select("div[class=mtop]").get(0);
                System.out.println(handleString2(li.html()));
                System.out.println(document.select("font[color]").get(0).html().isEmpty());
                //handle
                /*//pagination
                //<div class="right">
                Elements pagination = document.select("div[class=right]").get(0).children().get(1).children();
                currentPage = pagination.select("font").get(0).text();
                for (Element tmp : pagination) {
                    if (tmp.text().equals("上一页") && tmp.tagName().equals("a"))
                        previousPageUrl = tmp.attr("abs:href");
                    if (tmp.text().equals("下一页") && tmp.tagName().equals("a"))
                        nextPageUrl = tmp.attr("abs:href");
                }
                System.out.printf("currentPage: %s\npreviousPageUrl: %s\nnextPageUrl: %s\n",
                        currentPage, previousPageUrl, nextPageUrl);*/
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
    private static String handleString2(String src){
        return src.replaceAll("<p>","\n").replaceAll("</p>","\n").replaceAll("&quot;","").replaceAll("&nbsp;","");
    }
}
