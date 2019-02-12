package cn.wimetro.unit;



import java.util.Map;

public class UnionpayUnit {

    //private static Logger log = LoggerFactory.getLogger(MyUnit.class);


    public static int bytesToInt(byte[] data) {
        if (data.length == 1) {
            return data[0];
        }
        if (data.length == 2) {
            return (data[0] << 8 & 0x0000FF00) | (data[1] & 0x000000FF);
        }
        if (data.length == 4) {
            return (data[0] << 24 & 0xFF000000) | (data[1] << 16 & 0x00FF0000) | (data[2] << 8 & 0x0000FF00) | (data[3] & 0x000000FF);
        }
        throw new IllegalArgumentException("unsupported array length");
    }

    public static byte[] hex2byte(String hexString) {
        if (hexString.length() % 2 == 0) {
            return hex2byte(hexString.getBytes(), 0, hexString.length() >> 1);
        }
        return hex2byte("0" + hexString);
    }

    private static byte[] hex2byte(byte[] bytes, int offset, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length * 2; ++i) {
            int shift = (i % 2 == 1) ? 0 : 4;
            int index = (i >> 1);
            result[index] = (byte) (result[index] | Character.digit(
                    (char) bytes[(offset + i)], 16) << shift);
        }
        return result;
    }

    public static String removeLastLetter(String s , String L){
        String returnStr = s;
        if(s.length()>0){
            String last = s.substring(s.length()-1,s.length());
            if(L.equalsIgnoreCase(last)){

                s = s.substring(0,s.length()-1);
                returnStr = s;
            }
        }
        return returnStr;
    }

    public  static void printMap(Map map) {
        map.forEach((k, v) -> {
            //log.info(k+"===="+v);
        });
    }

}
