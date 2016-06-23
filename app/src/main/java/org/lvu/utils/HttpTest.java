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
        // getVideoUrlByUrl("http://www.fapple.com/videos/119646");

        String url = "http://0pmp.com/html/11/index2.html";
        System.out.println("Thread start");
        int count = 0;
        while (true) {
            try {
                Document document = Jsoup.connect(url).get();
                Elements elements = document.select("li");
                for (Element tmp : elements)
                    System.out.println(tmp.children().get(1).text() + "\t\t" + tmp.children().get(1).attr("abs:href"));
                Elements next = document.select("div[class]"), next2 = null;
                for (Element tmp : next) {
                    if (tmp.attr("class").equals("page"))
                        next2 = tmp.children();
                }
                if (next2 != null) {
                    for (Element tmp : next2) {
                        if (tmp.text().equals("下一页"))
                            System.out.println(tmp.attr("abs:href"));
                    }
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
        }
                 /*    if (tmp.attr("class").equals("content"))
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

    private static String getVideoUrlByUrl(String url) {
        int count = 0;
        while (true) {
            try {
                Document document = Jsoup.connect(url).get();
                Elements elements = document.select("a[class]");
                for (Element tmp : elements)
                    if (tmp.attr("class").equals("play")) {
                        System.out.println(tmp.attr("abs:href") + "\n\n");
                        return tmp.attr("abs:href");
                    }
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
        return "";
    }
}
