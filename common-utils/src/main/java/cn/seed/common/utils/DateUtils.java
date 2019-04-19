package cn.seed.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description:时间处理工具类
 * @author:方典典
 * @time:2018/8/6 15:51
 */
public class DateUtils {
    /**
     * @Description:获取当天零点日期
     * @params:[]
     * @return:java.util.Date
     * @exception: ParseException
     * @author: 方典典
     * @time:2018/8/6 15:52
     */
    public static Date getCurrentFirstTime() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(Calendar.getInstance().getTime());
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.parse(currentDate + " 00:00:00");
    }

    /**
     * @Description:获取当天24点日期
     * @params:[]
     * @return:java.util.Date
     * @exception:ParseException
     * @author: 方典典
     * @time:2018/8/6 15:52
     */
    public static Date getCurrentLastTime() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(Calendar.getInstance().getTime());
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.parse(currentDate + " 23:59:59");
    }
}
