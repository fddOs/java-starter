package cn.ehai.javautils.utils;

import java.util.UUID;

/**
 * @author juncheng
 */
public class UuidUtils {

    /**
     * 生成随机 UUID
     *
     * @return 随机不含横线的 UUID 字符串
     */
    public static String getRandomUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8)
                + uuid.substring(9, 13)
                + uuid.substring(14, 18)
                + uuid.substring(19, 23)
                + uuid.substring(24);
    }

    /**
     * 返回指定位数的盐
     *
     * @return
     */
    public static String genSoft(int digit) {
        return getRandomUUID().substring(0, digit).toUpperCase();
    }

}
