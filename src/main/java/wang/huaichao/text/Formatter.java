package wang.huaichao.text;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2015/2/2.
 */
public class Formatter {
    private static String hexvals = "0123456789ABCDEF";

    public static String toHexString(byte[] bytes, int len) {
        len = Math.min(len, bytes.length);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            if (i != 0) {
                if (i % 32 == 0) {
                    sb.append("\n");
                } else if (i % 8 == 0) {
                    sb.append("  ");
                }
            }
            sb.append(byte2hex(bytes[i]) + ' ');
        }
        return sb.toString();
    }

    public static String toAscii(byte[] bytes, int len) {
        len = Math.min(len, bytes.length);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            if (i != 0) {
                if (i % 32 == 0) {
                    sb.append("\n");
                } else if (i % 8 == 0) {
                    sb.append("  ");
                }
            }
            if (bytes[i] > 31 && bytes[i] < 127) {
                sb.append((char) bytes[i] + "  ");
            } else {
                sb.append(byte2hex(bytes[i]) + " ");
            }
        }
        return sb.toString();
    }

    public static String toAscii(byte[] bytes, int off, int len) {
        len = Math.min(len + off, bytes.length) - off;
        StringBuffer sb = new StringBuffer();
        for (int i = off; i < len; i++) {
            if (i != 0) {
                if (i % 32 == 0) {
                    sb.append("\n");
                } else if (i % 8 == 0) {
                    sb.append("  ");
                }
            }
            if (bytes[i] > 31 && bytes[i] < 127) {
                sb.append((char) bytes[i] + "  ");
            } else {
                sb.append(byte2hex(bytes[i]) + " ");
            }
        }
        return sb.toString();
    }

    public static String byte2hex(byte b) {
        return hexvals.charAt(b >>> 4 & 0x0f) + "" + hexvals.charAt(b & 0x0f);
    }
}
