package cn.seed.common.utils;

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

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
        return  StringUtils.replace(uuid,"-","",-1);
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
