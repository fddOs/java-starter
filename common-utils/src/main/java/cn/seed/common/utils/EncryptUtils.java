package cn.seed.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密工具类
 *
 * @author 26046
 */
public class EncryptUtils {


    /**
     * sha256
     *
     * @param strText
     * @return
     */
    public static String sha256(String strText) {
        return encrypt(strText, "SHA-256");
    }

    /**
     * 加密方法
     *
     * @param strText 需要加密的字符串
     * @param strType 加密类型
     * @return
     */
    public static String encrypt(String strText, String strType) {
        if (strText == null || "".equals(strText)) {
            return "";
        }

        try {
            MessageDigest md = MessageDigest.getInstance(strType);
            md.update(strText.getBytes());
            byte[] strBuffer = md.digest();

            StringBuffer strHexString = new StringBuffer();
            for (int i = 0; i < strBuffer.length; i++) {
                String hex = Integer.toHexString(0xff & strBuffer[i]);
                if (hex.length() == 1) {
                    strHexString.append('0');
                }
                strHexString.append(hex);
            }
            return strHexString.toString();
        } catch (NoSuchAlgorithmException e) {
            LoggerUtils.error(EncryptUtils.class, new Object[]{strText, strType}, e);
        }

        return "";
    }

    public static String HMACSHA256(String data, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            return byte2hex(mac.doFinal(data.getBytes())).toLowerCase();
        } catch (Exception e) {
            LoggerUtils.error(EncryptUtils.class, new Object[]{data, key}, e);
        }
        return null;
    }

    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

}
