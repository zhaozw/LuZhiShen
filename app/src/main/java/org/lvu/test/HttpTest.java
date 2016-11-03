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
            url = "https://www.580hu.com/htm/novel1/17238.htm";
        int count = 0, count2 = 0;
        while (true) {
            try {
                //print
                String currentPage, previousPageUrl = "", nextPageUrl = "";
                System.out.println("start resolve url: " + url);
                //start
                Document document = Jsoup.connect(url).validateTLSCertificates(false).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                System.out.println("resolve url finish");
                //<div class="novelContent">
                Element li = document.select("div[class=novelContent]").get(0);
                println(handleString2(li.html()));
              /*  //pagination
                //<div class="pageList">
                Elements pagination = document.select("div[class=pageList]").get(0).children();
                currentPage = pagination.select("strong").get(0).text();
                for (Element tmp : pagination) {
                    if (tmp.text().equals("上一页") && tmp.tagName().equals("a"))
                        previousPageUrl = tmp.attr("abs:href");
                    if (tmp.text().equals("下一页") && tmp.tagName().equals("a"))
                        nextPageUrl = tmp.attr("abs:href");
                }
                printf("currentPage: %s\npreviousPageUrl: %s\nnextPageUrl: %s\n",
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
        return src.replaceAll("<br>","\n").replaceAll("&nbsp;","");
    }

    private static void print(String s){
        System.out.print(s);
    }

    private static void printf(String src, String ...args){
        System.out.printf(src, args);
    }

    private static void println(String s){
        System.out.println(s);
    }
}
