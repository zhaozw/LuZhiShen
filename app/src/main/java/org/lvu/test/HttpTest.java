
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
            url = "http://www.laifudao.com/wangwen/index_1.htm";
        int count = 0;
        while (true) {
            try {
                // TODO: 7/28/16 查看全部内容 >>
                //<strong class="reader-more"><a href="/wangwen/31195.htm">查看全部内容 >></a></strong>
                //<section class="article-content">
                //<div class="mobile-pagenavi">
                String currentPage = "", previousPageUrl = "", nextPageUrl = "";
                System.out.println("start resolve url: " + url);
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                System.out.println("resolve url finish");
                Elements elements = document.select("section[class=article-content]");
                Elements readMore;
                for (Element tmp : elements) {
                    System.out.println(handleString6(tmp.text()) + "\n\n");
                    if (!(readMore = tmp.children().select("strong[class=reader-more]")).isEmpty()) {
                        System.out.println(readMore(readMore.get(0).child(0).attr("abs:href")));
                    }
                }
                Elements page = document.select("div[class=mobile-pagenavi]").get(0).children();
                /*
                  <a href="/wangwen/index_127.htm" class="mnext">上一页</a>
                  <span class="mpages">128/1728</span>
                  <a href="/wangwen/index_129.htm" class="mprev">下一页</a>
*/
                for (Element tmp : page) {
                    if (tmp.tagName().equals("span"))
                        currentPage = handleString7(tmp.text());
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

    private static String readMore(String url) {
        int count = 0;
        String result = "";
        while (true) {
            try {
                Document document = Jsoup.connect(url).timeout(4000)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").get();
                Elements elements = document.select("section[class=article-content]");
                StringBuilder sb = new StringBuilder();
                for (Element tmp : elements)
                    sb.append(handleString6(tmp.text())).append("\n\n");
                result += sb.toString();
                try {
                    Element n = document.select("div[class=post-pagenavi]").get(0).children().get(0);
                    if (n.text().equals("下页"))
                        result += readMore(n.attr("abs:href"));
                } catch (Exception ignored) {
                }
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
        return result;
    }

    private static String handleString6(String src) {
        if (src.substring(0,2).equals("　　")) {
            src = src.substring(2, src.length());
        }
        return src.replaceAll("\\s+", "\n");
    }

    private static String handleString7(String src) {
        return src.split("/")[0];
    }
}
