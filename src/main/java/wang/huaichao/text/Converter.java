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


}
