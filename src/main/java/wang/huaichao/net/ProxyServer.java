package wang.huaichao.net;


import wang.huaichao.text.Formatter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/1/30.
 */
public class ProxyServer {
    public ProxyServer(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket socket = server.accept();
                System.out.println("==============new connection===============");
                new Worker(socket).start();
//                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class Worker extends Thread {
        private static final int soTimeout = 10000;
        private Socket csocket;
        private static final Pattern ReqPtn = Pattern.compile(
                "^(get|post) +https?://([^ /]+)[^ ]* +(http/.*)$",
                Pattern.CASE_INSENSITIVE
        );

        public Worker(Socket socket) {
            this.csocket = socket;

            try {
                this.csocket.setSoTimeout(soTimeout);
            } catch (SocketException e) {
            }
        }

        private ByteArrayOutputStream readHttpGetRequest(InputStream is)
                throws IOException {
            int size = 1024, hsize = size / 2, total, idx, len, off = 0;
            byte[] buffer = new byte[size];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            total = len = _read(is, buffer, off, hsize);
            if (len == -1) return baos;
            baos.write(buffer, off, len);

            for (idx = 0; idx < total; idx++) {
                if (idx + 3 >= total) {
                    off = (off + hsize) % size;
                    len = _read(is, buffer, off, hsize);
                    if (len == -1) break;
                    baos.write(buffer, off, len);
                    total += len;
                }
                if (buffer[idx % size] != '\r') continue;
                if (buffer[(idx + 1) % size] == '\n' &&
                        buffer[(idx + 2) % size] == '\r' &&
                        buffer[(idx + 3) % size] == '\n') {
                    break;
                }
            }
            return baos;
        }


        public void pip(InputStream is, OutputStream os) throws IOException {
            int total = 0, len, size = 1024, hsize = size / 2, off = 0, idx = 0;
            byte[] last4b = {0, 0, 0, 0};
            byte[] buffer = new byte[size];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // get headers
            total = len = _read(is, buffer, off, hsize);
            if (len == -1) return;
            os.write(buffer, off, len);

            System.out.println("======================================");
            printBuff(buffer, off, len);

            for (idx = 0; idx < total; idx++) {
                // fill next half-buffer if data not sufficient for analyzing
                if (idx + 3 >= total) {
                    baos.write(buffer, off, len);
                    off = (off + hsize) % size;
                    len = _read(is, buffer, off, hsize);
                    total += len;
                    os.write(buffer, off, len);
                    System.out.println("======================================");
                    printBuff(buffer, off, len);
                }

                if (buffer[idx % size] != '\r') continue;

                if (buffer[(idx + 1) % size] == '\n' &&
                        buffer[(idx + 2) % size] == '\r' &&
                        buffer[(idx + 3) % size] == '\n') {
                    baos.write(buffer, off,
                            (idx + 3) % hsize - off % hsize + 1
                    );
                    break;
                } else idx += 3;
            }

            String header = baos.toString();
            Pattern ptn = Pattern.compile(
                    "content-length: (\\d+)", Pattern.CASE_INSENSITIVE
            );
            Matcher mat = ptn.matcher(header);

            if (mat.find()) {
                int left = Integer.valueOf(mat.group(1)) - total + 4 + idx;
                while (left > 0) {
                    len = _read(is, buffer, 0, size);
                    if (len == -1) continue;
                    os.write(buffer, 0, len);
                    left -= len;
                }
            } else {
                // consume rest of traffic payload
                while (true) {
                    try {
                        len = _read(is, buffer, 0, size);
                        if (len == -1) break;
                        os.write(buffer, 0, len);

                        System.out.println(len);
                        System.out.println("=================================");
                        printBuff(buffer, 0, len);

                        if (len < 4) {
                            for (int i = 0; i < 4 - len; i++) {
                                last4b[i] = last4b[i + len];
                            }
                            for (int i = 0; i < len; i++) {
                                last4b[4 - len + i] = buffer[i];
                            }
                        } else {
                            for (int i = 0; i < 4; i++) {
                                last4b[i] = buffer[len - 4 + i];
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (last4b[0] == '\r' && last4b[1] == '\n' &&
                                last4b[2] == '\r' && last4b[3] == '\n') {
                            break;
                        }
                    }
                }
            }
        }


        private void printBuff(byte[] bytes, int off, int len) {
            String str = Formatter.toAscii(bytes, off, len);
            System.out.println(str);
        }

        private int _read(InputStream is, byte[] buffer, int off, int size) throws IOException {
            int len = -1;
            for (int i = 0; i < 3; i++) {
                try {
                    return is.read(buffer, off, size);
                } catch (SocketTimeoutException e) {
                }
            }
            return len;
        }

        private void _close(InputStream is) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            ByteArrayOutputStream baos = null;
            InputStream cis = null;
            try {
                cis = this.csocket.getInputStream();
                baos = readHttpGetRequest(cis);
            } catch (IOException e) {
                _close(cis);
                return;
            }

            System.out.println();
            System.out.println();
            System.out.println("======================================req");
            printBuff(baos.toByteArray(), 0, baos.size());

            String line = baos.toString().split("\r\n")[0];
            Matcher mat = ReqPtn.matcher(line);
            String host;
            int port = 80;

            if (mat.find()) {
                String[] hostport = mat.group(2).split(":");
                host = hostport[0];
                if (hostport.length == 2)
                    port = Integer.valueOf(hostport[1]);
            } else {
                return;
            }

            try {
                Socket socket = new Socket(host, port);
                socket.setSoTimeout(soTimeout);
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                os.write(baos.toByteArray());
                pip(is, this.csocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
