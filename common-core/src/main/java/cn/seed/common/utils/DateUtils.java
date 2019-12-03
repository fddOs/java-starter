package cn.seed.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Description:时间处理工具类
 * @author:方典典
 * @time:2018/8/6 15:51
 */
public class DateUtils {
    /**
     * 获取当天零点日期
     *
     * @return:java.util.Date
     * @author: 方典典
     * @time:2018/8/6 15:52
     */
    public static Date getCurrentFirstTime() {
        return Date.from(LocalDateTime.parse(LocalDateTime.now().atZone(ZoneId.systemDefault()).format
                (DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00")), DateTimeFormatter.ofPattern("yyyy-MM-dd " +
                "HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当天24点日期
     *
     * @return:java.util.Date
     * @author: 方典典
     * @time:2018/8/6 15:52
     */
    public static Date getCurrentLastTime() {
        return Date.from(LocalDateTime.parse(LocalDateTime.now().atZone(ZoneId.systemDefault()).format
                (DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59")), DateTimeFormatter.ofPattern("yyyy-MM-dd " +
                "HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant());
    }
}
