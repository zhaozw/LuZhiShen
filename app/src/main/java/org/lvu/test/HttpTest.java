
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
        String url = "http://www.laifudao.com/wangwen/index_1728.htm";
        int count = 0;
        while (true) {
            try {
                // TODO: 7/28/16 查看全部内容 >>

                //<section class="article-content">
                //<div class="mobile-pagenavi">
                String currentPage = "", previousPageUrl = "", nextPageUrl = "";
                System.out.println("start resolve url: " + url);
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                System.out.println("resolve url finish");
                Elements elements = document.select("section[class=article-content]");
                for (Element tmp : elements)
                    System.out.println(handleString(tmp.text()) + "\n\n");
                Elements page = document.select("div[class=mobile-pagenavi]").get(0).children();
                /*
                  <a href="/wangwen/index_127.htm" class="mnext">上一页</a>
                  <span class="mpages">128/1728</span>
                  <a href="/wangwen/index_129.htm" class="mprev">下一页</a>
                 */
                for (Element tmp : page) {
                    if (tmp.tagName().equals("span"))
                        currentPage = handleString2(tmp.text());
                    else if (tmp.attr("class").equals("mnext"))
                        previousPageUrl = tmp.attr("abs:href");
                    else if ((tmp.attr("class").equals("mprev")))
                        nextPageUrl = tmp.attr("abs:href");
                }
                System.out.printf("currentPage: %s\npreviousPageUrl: %s\nnextPageUrl: %s\n",
                        currentPage, previousPageUrl, nextPageUrl);
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

    private static String handleString(String src) {
        return src.replaceAll("\\s+", "\n");
    }

    private static String handleString2(String src) {
        return src.split("/")[0];
    }
}
