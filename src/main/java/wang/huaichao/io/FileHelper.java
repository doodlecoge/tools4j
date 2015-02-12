package wang.huaichao.io;

import wang.huaichao.text.Formatter;

import java.io.*;
import java.nio.Buffer;

/**
 * Created by Administrator on 2015/1/30.
 */
public class FileHelper {
    public static ByteArrayOutputStream Read(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bis = new BufferedInputStream(fis);
        int b;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while ((b = bis.read()) != -1) {
                baos.write(b);
            }
            return baos;
        } finally {
            bis.close();
        }
    }

    public static void printFileContent(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        int size = 1024, len, idx = 0;
        byte[] buffer = new byte[size];
        while (true) {
            len = fis.read(buffer, 0, size);
            if (len == -1) break;
            String str = Formatter.toHexString(buffer, len);
            System.out.println(str);
        }
    }

    public static void write(String file, ByteArrayOutputStream baos)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(baos.toByteArray());
        fos.close();
    }

    public static void write(String file, String data)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data.getBytes());
        fos.close();
    }
}
