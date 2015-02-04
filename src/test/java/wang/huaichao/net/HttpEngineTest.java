package wang.huaichao.net;

import wang.huaichao.text.Formatter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/2/2.
 */
public class HttpEngineTest {
    public static void main(String[] args) throws IOException {
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
