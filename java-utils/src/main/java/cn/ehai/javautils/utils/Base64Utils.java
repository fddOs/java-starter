package cn.ehai.javautils.utils;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Base64Utils {

    public static byte[] decryptBASE64(String base64String) {
        int pad = 4 - base64String.length() % 4;
        StringBuilder endStr = new StringBuilder();
        while (pad > 0 && pad < 4) {
            endStr.append('=');
            pad --;
        }
        return Base64.getDecoder().decode(base64String + endStr.toString());
    }

    public static String encryptBASE64(byte[] byteArray) {
        return new String(Base64.getEncoder().encode(byteArray), UTF_8);
    }

    public static String encryptBASE64(String input) {
        return encryptBASE64(input.getBytes());
    }

}
