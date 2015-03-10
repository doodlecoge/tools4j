package wang.huaichao.text;

/**
 * Created by Administrator on 2015/2/4.
 */
public class Converter {
    public static int hex2int(byte[] hexes) {
        int n = 0;
        for (byte hex : hexes) {
            n = n * 16 + hex2int(hex);
        }
        return n;
    }

    public static int hex2int(byte b) {
        return b - 'a' >= 0 ? b - 'a' + 10 : b - 'A' >= 0 ? b - 'A' : b - '0';
    }

    public static byte[] hexStringToByteArray(String hexStr) {
        hexStr = hexStr.toLowerCase();
        int len = hexStr.length() / 2;
        byte[] bytes = new byte[len];
        int val;
        char c;
        for (int i = 0; i < len; i++) {
            c = hexStr.charAt(i * 2);
            val = c >= 'a' ? c - 'a' : c - '0';
            val *= 16;
            c = hexStr.charAt(i * 2 + 1);
            val += c >= 'a' ? c - 'a' : c - '0';
            bytes[i] = (byte) val;
        }
        return bytes;
    }
}
