package wang.huaichao.net;

import wang.huaichao.text.Formatter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

        ByteArrayOutputStream baos = eng.getRaw("http://www.baidu.com/");
        String s = Formatter.toAscii(baos.toByteArray(), baos.size());
        System.out.println(s);
    }
}
