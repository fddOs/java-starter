package cn.ehai.common.utils;

import com.alibaba.fastjson.JSON;

/**
 * @Description:JsonUtils
 * @author:方典典
 * @time:2019/3/19 16:00
 */
public class JsonUtils {
    /**
     * JSON解析
     *
     * @param text
     * @return java.lang.Object
     * @author 方典典
     * @time 2019/3/19 16:06
     */
    public static Object parse(String text) {
        try {
            return JSON.parse(text);
        } catch (Exception e) {
            LoggerUtils.error(JsonUtils.class, new Object[]{text}, e);
            return text;
        }
    }
}
