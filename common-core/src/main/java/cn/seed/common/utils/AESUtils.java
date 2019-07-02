package cn.seed.common.utils;

import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密
 *
 * @author 26046
 */
public class AESUtils {
    public static final String ENCRYPT_FLAG = "_encrypt";
    public static final String IV_STRING = ProjectInfoUtils.PROJECT_AES_OFFSET;
    public static final String KEY = ProjectInfoUtils.PROJECT_AES_KEY;
    public static final String CHARSET = "UTF-8";

    private AESUtils() {
    }

    public static String aesEncryptString(String content) {
        return aesEncryptString(content, KEY);
    }

    /**
     * 加密
     *
     * @param content
     * @param key     16位密钥
     * @return
     */
    public static String aesEncryptString(String content, String key) {
        if (null == content) {
            return content;
        }
        byte[] encryptedBytes;
        try {
            byte[] contentBytes = content.getBytes(CHARSET);
            byte[] keyBytes = key.getBytes(CHARSET);
            encryptedBytes = aesEncryptBytes(contentBytes, keyBytes);
        } catch (Exception e) {
            LoggerUtils.error(AESUtils.class, new Object[]{content, key}, e);
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "加密失败！", e);
        }
        Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(encryptedBytes);
    }

    public static String aesDecryptString(String content) {
        if (null == content) {
            return content;
        }
        return aesDecryptString(content, KEY);
    }

    /**
     * 解密
     *
     * @param content
     * @param key     16位密钥
     * @return
     */
    public static String aesDecryptString(String content, String key) {
        Decoder decoder = Base64.getDecoder();
        try {
            byte[] encryptedBytes = decoder.decode(content);
            byte[] keyBytes = key.getBytes(CHARSET);
            byte[] decryptedBytes = aesDecryptBytes(encryptedBytes, keyBytes);
            return new String(decryptedBytes, CHARSET);
        } catch (Exception e) {
            LoggerUtils.error(AESUtils.class, new Object[]{content, key}, e);
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "解密失败！", e);
        }

    }

    private static byte[] aesEncryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        return cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE);
    }

    private static byte[] aesDecryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        return cipherOperation(contentBytes, keyBytes, Cipher.DECRYPT_MODE);
    }

    private static byte[] cipherOperation(byte[] contentBytes, byte[] keyBytes, int mode)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        byte[] initParam = IV_STRING.getBytes(CHARSET);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

        Cipher cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
        cipher.init(mode, secretKey, ivParameterSpec);

        return cipher.doFinal(contentBytes);
    }

}
