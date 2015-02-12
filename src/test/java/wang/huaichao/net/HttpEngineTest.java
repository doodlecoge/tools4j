package wang.huaichao.net;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.org.mozilla.javascript.internal.json.JsonParser;
import wang.huaichao.io.FileHelper;
import wang.huaichao.text.Formatter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Administrator on 2015/2/2.
 */
public class HttpEngineTest {
    private static final class KvPair {
        public String key;
        public String val;

        public KvPair(String key, String val) {
            this.key = key;
            this.val = val;
        }
    }

    public static void main(String[] args) throws IOException {
        HttpEngine eng = new HttpEngine();
        String surl = "http://www.cilook.net/book/0/47/";
        String dir = "e:\\tmp\\";

//        String s = eng.getRaw(surl).toString("gbk");
//        System.out.println(s);

        Document category = Jsoup.parse(new File("E:\\workspace\\java\\tools4j\\a.html"), "gbk");
        Elements links = category.select("dd > a");

        String html;

        LinkedList<KvPair> urltexts = new LinkedList<KvPair>();

        for (Element link : links) {
            urltexts.add(new KvPair(link.attr("href").trim(), link.text().trim()));
        }


        String xxx = "<!doctype html><html><head>";
        xxx += "<meta name=\"viewport\" content=\"width=device-width, user-scalable=no\">";
        xxx += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />";
        xxx += "<style type=\"text/css\">";
        xxx += "body{line-height:30px;font-family:\"微软雅黑\"}";
        xxx += "</style>";
        xxx += "</head>";
        xxx += "<body>";

        for (int i = 0; i < urltexts.size(); i++) {
            KvPair urltext = urltexts.get(i);
            xxx += "<a href=\"" + urltext.val + ".html\">" + urltext.val + "</a><br/>";
        }
        xxx += "</body>";
        xxx += "</html>";
        FileHelper.write(dir + "index.html", xxx);

        for (int i = 0; i < urltexts.size(); i++) {
            KvPair urltext = urltexts.get(i);
            if (new File(dir + urltext.val + ".html").exists()) continue;

            System.out.println("downloading " + urltext.key + ", " + urltext.val);

            html = eng.getRaw(surl + urltext.key).toString("gbk");
            Document d = Jsoup.parse(html);
            Elements content = d.select("#content");

            if (content.size() > 0) {
                String cont = content.get(0).html();
                String t = "<!doctype html><html><head>";
                t += "<meta name=\"viewport\" content=\"width=device-width, user-scalable=no\">";
                t += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />";
                t += "<style type=\"text/css\">";
                t += "body{line-height:30px;font-family:\"微软雅黑\"}";
                t += "</style>";
                t += "</head>";
                t += "<body><h2>" + urltext.val + "</h2><hr/>";
                if (i > 0)
                    t += "<a href=\"" + urltexts.get(i - 1).val + ".html\">上一页：" + urltexts.get(i - 1).val + "</a><br/>";
                if (i < urltexts.size() - 1)
                    t += "<a href=\"" + urltexts.get(i + 1).val + ".html\">下一页：" + urltexts.get(i + 1).val + "</a>";
                t += "<hr/>";
                t += cont;
                t += "<hr/>";
                if (i > 0)
                    t += "<a href=\"" + urltexts.get(i - 1).val + ".html\">上一页：" + urltexts.get(i - 1).val + "</a><br/>";
                if (i < urltexts.size() - 1)
                    t += "<a href=\"" + urltexts.get(i + 1).val + ".html\">下一页：" + urltexts.get(i + 1).val + "</a>";
                t += "</body>";
                t += "</html>";
                FileHelper.write(dir + urltext.val + ".html", t);
            } else {
                System.out.println(urltext.key + "," + urltext.val);
            }
        }
    }

    public static void testProxy() throws IOException {
        HttpEngine eng = new HttpEngine();
        eng.useProxy("127.0.0.1", 8899);
//        String html = eng.get("http://www.baidu.com");
//        System.out.print(">>>");
//        System.out.println(html);

        Map<String, String> headers = new HashMap<String, String>();

        headers.put("Host", "www.csdn.net");
        headers.put("Proxy-Connection", "keep-alive");
        headers.put("Cache-Control", "max-age=0");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("User-Agent", "Mozilla/5.0(WindowsNT6.1;WOW64)AppleWebKit/537.36(KHTML,likeGecko)Chrome/40.0.2214.93Safari/537.36");
        headers.put("Accept-Encoding", "gzip,deflate,sdch");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8");


        ByteArrayOutputStream baos = eng.getRaw("http://www.baidu.com/");

        String s = Formatter.toAscii(baos.toByteArray(), baos.size());
        System.out.println(s);
    }
}
