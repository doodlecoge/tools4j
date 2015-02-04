package wang.huaichao.text;

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
        return toAscii(bytes, 0, len);
    }

    public static String toAscii(byte[] bytes, int off, int len) {
        int size = bytes.length, j = 0;
        byte b;
        StringBuffer sb = new StringBuffer();
        for (int i = off; i < off + len; i++, j++) {
            if (j != 0) {
                if (j % 32 == 0) {
                    sb.append("\n");
                } else if (j % 8 == 0) {
                    sb.append("  ");
                }
            }

            if (j % 32 == 0) {
                sb.append(prepend(Integer.toHexString(j), 8, '0') + "   ");
            }

            b = bytes[i % size];
            if (b > 31 && b < 127) {
                sb.append((char) b + "  ");
            } else {
                sb.append(byte2hex(b) + " ");
            }
        }
        return sb.toString();
    }

    public static String prepend(String tag, int len, char pad) {
        int n = len - tag.length();
        if (n < 1) return tag;
        for (int i = 0; i < n; i++) tag = pad + tag;
        return tag;
    }

    public static String append(String tag, int len, char pad) {
        int n = len - tag.length();
        if (n < 1) return tag;
        for (int i = 0; i < n; i++) tag += pad;
        return tag;
    }

    public static String byte2hex(byte b) {
        return hexvals.charAt(b >>> 4 & 0x0f) + "" + hexvals.charAt(b & 0x0f);
    }
}
