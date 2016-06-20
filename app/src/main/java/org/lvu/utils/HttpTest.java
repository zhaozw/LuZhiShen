import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wuyr on 6/16/16 7:40 PM.
 */
public class HttpTest {
    public static void main(String[] args) {
        String url = "http://a5408977.gotoip55.com/?s=vod-show-id-39.html";
        System.out.println("Thread start");
        int count = 0;
        while (true) {
            try {
                Document document = Jsoup.connect(url).get();
                Elements links = document.select("p[class]"),
                        src = document.select("img[data-original]");
                Elements link = new Elements();
                for (Element tmp : links)
                    if (tmp.attr("class").equals("content"))
                        link.add(tmp);
                System.out.println("link.size:" + link.size() + "\tsrc.size:" + src.size());
                for (int i = 0; i < link.size(); i++) {
                    System.out.println(handlerString(src.get(i).attr("alt")) + "\t"
                            + src.get(i).attr("abs:data-original") + "\t"
                            + handlerString2(link.get(i).text()));
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
                e.printStackTrace();
            }
        }/*
        int count = 0;
        while (true) {
            try {
                Document document = Jsoup.parse(new URL(url), 8000);
                Elements links = document.select("source");
                for (Element element : links) {
                    if (element.attr("type").equals("video/mp4"))
                        System.out.println(element.attr("abs:src"));
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
            }
        }*/
    }

    private static String handlerString2(String text) {
        int pos = text.indexOf("'", 19);
        return text.substring(19, pos);
    }

    private static String handlerString(String src) {
        return src.replaceAll("点击播放", "");
    }
}
