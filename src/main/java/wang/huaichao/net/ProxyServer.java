package wang.huaichao.net;


import wang.huaichao.text.Formatter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/1/30.
 */
public class ProxyServer {
    public ProxyServer(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            int n = 0;
            while (true) {
                n++;
                Socket socket = server.accept();
                System.out.println("==============new connection " + n + "===============");
                new Worker(socket, n).start();
//                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class Worker extends Thread {
        private static final int soTimeout = 10000;

        private int id;
        private Socket csocket;
        private Socket ssocket;
        private final int bsize = 2048;
        private final int hsize = bsize / 2;
        private final byte[] buffer = new byte[bsize];

        private static final Pattern ReqPtn = Pattern.compile(
                "^(get|post|head|put|delete|connect) +([a-z]+)://([^ /]+)[^ ]* +(http/.*)$",
                Pattern.CASE_INSENSITIVE
        );

        public Worker(Socket socket, int id) {
            this.csocket = socket;
            this.id = id;


            try {
                this.csocket.setSoTimeout(soTimeout);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }


        private void handleRequest() throws IOException {
            InputStream cis = csocket.getInputStream();
            OutputStream sos = null;
            int off = 0, len, idx, total, pos = 0;
            ByteArrayOutputStream cache = new ByteArrayOutputStream();
            String line;

            total = len = _read(cis, buffer, off, hsize);
            if (len == -1) {
                System.out.println("===-1===");
                return;
            }
            cache.write(buffer, off, len);

            // get first line
            for (idx = 0; idx < total; idx++) {
                if (idx + 3 >= total) {
                    off = (off + hsize) % bsize;
                    len = _read(cis, buffer, off, hsize);
                    if (len == -1) break;
                    total += len;
                    if (sos != null)
                        _write(sos, buffer, off, len);
                    else
                        cache.write(buffer, off, len);
                }
                if (buffer[idx % bsize] != '\r') continue;
                if (sos == null && buffer[(idx + 1) % bsize] == '\n') {
                    line = extractLine(pos, idx - pos);
                    ssocket = _connectServer(line);
                    if (ssocket == null) return;
                    sos = ssocket.getOutputStream();
                    _write(sos, cache.toByteArray(), 0, cache.size());
                }
                if (buffer[(idx + 1) % bsize] == '\n' &&
                        buffer[(idx + 2) % bsize] == '\r' &&
                        buffer[(idx + 3) % bsize] == '\n') {
                    break;
                }
            }
        }

        private void handleResponse() throws IOException {
            if (ssocket == null) return;
            InputStream sis = ssocket.getInputStream();
            OutputStream cos = csocket.getOutputStream();

            int off = 0, pos = 0, idx, len, total;
            String line;
            HttpResponse resp = new HttpResponse();
            boolean firstLine = true;

            total = len = _read(sis, buffer, off, hsize);
            _write(cos, buffer, off, len);

            for (idx = 0; idx < total; idx++) {
                if (idx + 3 >= total) {
                    off = (off + hsize) % bsize;
                    len = _read(sis, buffer, off, hsize);
                    if (len == -1) break;
                    _write(cos, buffer, off, len);
                    total += len;
                }

                if (buffer[idx % bsize] != '\r') continue;
                if (buffer[(idx + 1) % bsize] == '\n') {
                    if (idx == pos) break;
                    line = extractLine(pos, idx - pos);
                    pos = idx + 2;
                    if (firstLine) {
                        resp.addStatusLine(line);
                        firstLine = false;
                    } else {
                        resp.addHeaderLine(line);
                    }
                }
            }

            String length = resp.headers.get("content-length");
            String encoding = resp.headers.get("transfer-encoding");
            // fixme: batch mode no content-length field
            if (length != null) {
                int remain = Integer.valueOf(length) - total + idx + 4;
                while (remain > 0) {
                    len = _read(sis, buffer, 0, bsize);
                    if (len == -1) break;
                    remain -= len;
                    _write(cos, buffer, 0, len);
                }
            } else if (encoding.equalsIgnoreCase("chunked")) {
                idx += 2;
                pos += 2;
                if (idx >= total) {
                    off = (off + hsize) % bsize;
                    len = _read(sis, buffer, off, hsize);
                    if (len == -1) {
                        System.out.println("...-1...");
                        return;
                    }
                    _write(cos, buffer, off, len);
                    total += len;
                }
                int chunkSize;
                for (; idx < total; idx++) {
                    if (idx >= total) {
                        off = (off + hsize) % bsize;
                        len = _read(sis, buffer, off, hsize);
                        if (len == -1) break;
                        _write(cos, buffer, off, len);
                        total += len;
                    }

                    if (buffer[idx % bsize] != '\r') continue;

                    chunkSize = hex2int(extractLine(pos, idx - pos));
                    System.out.println("chunk size: " + chunkSize);
                    if (chunkSize == 0) {
                        if (idx + 1 == total) {
                            off = (off + hsize) % bsize;
                            len = _read(sis, buffer, off, hsize);
                            _write(cos, buffer, off, len);
                        }
                        break;
                    }
                    int remain = idx + chunkSize + 4 - total;
                    System.out.println("remain: " + remain);
                    System.out.println("pos: " + pos);
                    System.out.println("idx: " + idx);
                    while (remain > 0) {
                        off = (off + hsize) % bsize;
                        len = _read(sis, buffer, off, hsize);
                        if (len == -1) {
                            System.out.println("===-1...");
                            break;
                        }
                        _write(cos, buffer, off, len);
                        remain -= len;
                        total += len;
                    }
                    pos += chunkSize + 4;
                    idx += chunkSize + 3;
                }
            }
        }

        private String extractLine(int pos, int len) {
            pos = pos % bsize;
            String line;
            if (pos + len > bsize) {
                line = new String(buffer, pos, bsize - pos);
                line += new String(buffer, 0, len + pos - bsize);
            } else {
                line = new String(buffer, pos, len);
            }
            return line;
        }

        private int hex2int(String hex) {
            char[] chars = hex.toCharArray();
            int r = 0;
            for (char chr : chars) {
                r = r * 16 + char2int(chr);
            }
            return r;
        }

        private int char2int(char c) {
            return c - 'a' >= 0 ? c - 'a' + 10 : c - '0';
        }

        private Socket _connectServer(String reqline) throws IOException {
            Matcher matcher = ReqPtn.matcher(reqline);
            Socket socket = null;
            int port = 80;
            if (matcher.find()) {
                String[] split = matcher.group(3).split(":");
                if (split.length == 2) port = Integer.valueOf(split[1]);
                socket = new Socket(split[0], port);
            }
            return socket;
        }

        private void printBuff(byte[] bytes, int off, int len) {
            String str = Formatter.toAscii(bytes, off, len);
            System.out.println(str);
        }

        private int _read(InputStream is, byte[] buffer, int off, int size)
                throws IOException {
            int len = -1;
            for (int i = 0; i < 3; i++) {
                try {
                    len = is.read(buffer, off, size);
                    if (len > 0) {
                        System.out.println();
                        System.out.println();
                        System.out.println();
                        System.out.println("====== read: " + id + ", " + len);
                        printBuff(buffer, off, len);
                    }
                    return len;
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                }
            }
            return len;
        }

        private void _write(OutputStream os, byte[] buffer, int off, int len)
                throws IOException {
            os.write(buffer, off, len);
            os.flush();

            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("====== write: " + id + ", " + len);
            printBuff(buffer, off, len);
        }

        @Override
        public void run() {
            System.out.println("==================start");
            try {
                handleRequest();
                handleResponse();
                csocket.close();
                if (ssocket != null) ssocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("==================stop: " + id);
        }
    }

    private static final class HttpResponse {
        private static final Pattern statusPtn = Pattern.compile(
                "^http/\\d\\.\\d (\\d+) .+$",
                Pattern.CASE_INSENSITIVE
        );
        private static final Pattern headerPtn = Pattern.compile(
                "^([\\w-]+):(.+)$",
                Pattern.CASE_INSENSITIVE
        );

        private int code;
        private Map<String, String> headers = new HashMap<String, String>();


        public void addStatusLine(String line) {
            // http/1.1 200 ok
            Matcher matcher = statusPtn.matcher(line);
            if (matcher.find()) {
                code = Integer.valueOf(matcher.group(1));
            } else {
                System.err.println(line);
            }
        }

        public void addHeaderLine(String line) {
            Matcher matcher = headerPtn.matcher(line);
            if (matcher.find()) {
                String key = matcher.group(1).trim().toLowerCase();
                String val = matcher.group(2).trim();
                headers.put(key, val);
            } else {
                System.err.println(line);
            }
        }
    }
}
