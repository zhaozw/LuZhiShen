import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by wuyr on 1/24/17 10:09 PM.
 */

public class HttpTest2 {
    public static void main(String[] args) throws Exception {
        println("start request...");
        HttpURLConnection connection = (HttpURLConnection) new URL("http://j1.44hdb1.com:22345/vodlist/rihannvyou/1.json").openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String tmp;
        StringBuilder builder = new StringBuilder();
        while ((tmp = reader.readLine()) != null) {
            builder.append(tmp);
            println(tmp);
        }
        String json = builder.toString();
        Gson gson = new Gson();
        NewData newData = gson.fromJson(json, new TypeToken<NewData>() {
        }.getType());
        println("---------------------");
        println("---------------------");
        println("---------------------");
        println(newData.toString());
    }

    private static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str.toUpperCase();
    }

    private static void println(String s) {
        System.out.println(s);
    }

    private static class NewData {

        private String url, src, text, nextPageUrl;
        String date;

        private String html;
        private int currentPage, totalPages;
        private boolean isFavorites;

        public NewData(String url, String src, String text, String date) {
            this.url = url;
            this.src = src;
            this.text = text;
            this.date = date;
        }

        public NewData(String url, String text, String date) {
            this(url, "", text, date);
        }

        public NewData(String src) {
            this("", src, "", "");
        }

        public NewData() {
        }

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }

        public String getUrl() {
            return url == null ? "" : url;
        }

        public String getSrc() {
            return src == null ? "" : src;
        }

        public String getText() {
            return text == null ? "" : text;
        }

        public String getDate() {
            return date == null ? "" : date;
        }

        public String getNextPageUrl() {
            return nextPageUrl == null ? "" : nextPageUrl;
        }

        public void setNextPageUrl(String nextPageUrl) {
            this.nextPageUrl = nextPageUrl;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public boolean isFavorites() {
            return isFavorites;
        }

        public void setFavorites(boolean favorites) {
            isFavorites = favorites;
        }

        private String name;
        private String maxIndex;
        private List<Row> rows;

        public List<Row> getRows() {
            return rows;
        }

        public void setRows(List<Row> rows) {
            this.rows = rows;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMaxIndex() {
            return maxIndex;
        }

        public void setMaxIndex(String maxIndex) {
            this.maxIndex = maxIndex;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("name = ").append(name).append("\n").append("maxIndex = ").append(maxIndex).append("\n");
            for (Row tmp : rows)
                result.append("-\n").append(tmp.toString()).append("\n");
            for (Vod tmp : rows_m3u8)
                result.append("-\n").append(tmp.toString()).append("\n");
            return result.toString();
        }

        private List<Vod> rows_m3u8;

        public List<Vod> getRows_m3u8() {
            return rows_m3u8;
        }

        public void setRows_m3u8(List<Vod> rows_m3u8) {
            this.rows_m3u8 = rows_m3u8;
        }

        private static class Vod extends NewData {

            private String vod;

            public String getVod() {
                return vod;
            }

            public void setVod(String vod) {
                this.vod = vod;
            }

            @Override
            public String toString() {
                return "vod: " + vod;
            }
        }
    }

    private static class Row {

        String title;
        String date;
        String jsonUrl;
        String img;

        @Override
        public String toString() {
            return "title:" + title + "\ndate:" +
                    date + "\njsonUrl:" + jsonUrl + "\nimg:" + img + "\n";
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getJsonUrl() {
            return jsonUrl;
        }

        public void setJsonUrl(String jsonUrl) {
            this.jsonUrl = jsonUrl;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}
