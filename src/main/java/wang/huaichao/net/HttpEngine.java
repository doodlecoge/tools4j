package wang.huaichao.net;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/1/26.
 */
public class HttpEngine {
    private int readTimeout = 30000;
    private int connTimeout = 30000;
    private String encoding = "utf-8";
    private CookieManager cookieManager;
    private Proxy proxy = null;

    public HttpEngine() {
        cookieManager = new CookieManager();
    }

    public ByteArrayOutputStream getRaw(String url) throws IOException {
        return _exec(Method.GET, url, null);
    }

    public String get(String url) throws IOException {
        ByteArrayOutputStream baos = _exec(Method.GET, url, null);
        return baos.toString();
    }

    public String post(String url, Map<String, String> data) throws IOException {
        StringBuffer sb = new StringBuffer();

        boolean b = true;
        for (String key : data.keySet()) {
            if (b) b = false;
            else sb.append("&");
            sb.append(key).append('=').append(
                    URLEncoder.encode(data.get(key), encoding)
            );
        }

        return post(url, sb.toString());
    }

    public void useProxy(String host, int port) {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

    public void removeProxy() {
        proxy = null;
    }

    public String post(String url, String data) throws IOException {
        ByteArrayOutputStream baos = _exec(Method.POST, url, data);
        return baos.toString();
    }


    private ByteArrayOutputStream _exec(Method method, String url, String data) throws IOException {
        CookieHandler.setDefault(cookieManager);

        URLConnection uconn;
        if (proxy == null) uconn = new URL(url).openConnection();
        else uconn = new URL(url).openConnection(proxy);

        if (!(uconn instanceof HttpURLConnection)) {
            return null;
        }
        HttpURLConnection hconn = (HttpURLConnection) uconn;
        if (method == Method.POST) hconn.setDoOutput(true);
        hconn.setReadTimeout(readTimeout);
        hconn.setConnectTimeout(connTimeout);
        hconn.connect();
        if (method == Method.POST && data != null) {
            OutputStream os = hconn.getOutputStream();
            os.close();
        }
        InputStream is = hconn.getInputStream();
        ByteArrayOutputStream html = _read(is);
        return html;
    }

    private void _write(OutputStream os, String data) throws IOException {
        os.write(data.getBytes());
    }

    private ByteArrayOutputStream _read(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = bis.read()) != -1) {
            baos.write(b);
        }
        return baos;
    }


    /*=======================================================================*/
    /*=======================================================================*/
    public static enum Method {
        GET, POST
    }
}
