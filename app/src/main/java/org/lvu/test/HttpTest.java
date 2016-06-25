
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
        // getVideoUrlByUrl("http://www.fapple.com/videos/119646");
        String url = "http://www.52kkm.org/xieemanhua/5311.html";
        System.out.println("Thread start");
        int count = 0;
        while (true) {
            try {
                System.out.println(Jsoup.parse(new URL(url), 8000).select("img[alt]").get(0).attr("abs:src"));
                //TODO: 一次9个item
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
    }

    private static String handlerString(String src) {
        return src.replaceAll("邪恶漫画", "");
    }
}
