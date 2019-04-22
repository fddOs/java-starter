package cn.seed.common.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * 初始化工具类
 *
 * @author 26046
 */
public class BeanInitUtils {

    /**
     * 初始化
     *
     * @param o
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static <T> T init(Class<T> o) {
        T obj;
        try {
            obj = o.newInstance();
        } catch (Exception e1) {
            LoggerUtils.error(BeanInitUtils.class, new Object[]{o}, e1);
            return (T) new Object();
        }
        Field[] fs = o.getDeclaredFields();

        for (Field f : fs) {
            f.setAccessible(true);
            initField(obj, f);
        }
        return obj;
    }

    /**
     * 将null附一个初始值
     *
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static <T> T desNull(T t) {

        Field[] fs = t.getClass().getDeclaredFields();
        for (Field f : fs) {
            f.setAccessible(true);
            try {
                if (f.get(t) != null) {
                    continue;
                }
            } catch (IllegalAccessException e) {
                LoggerUtils.error(BeanInitUtils.class, new Object[]{t}, e);
                continue;
            }
            initField(t, f);

        }
        return t;
    }

    private static void initField(Object obj, Field f) {
        String type = f.getGenericType().toString();
        try {
            if ("class java.lang.String".equals(type)) {
                f.set(obj, "");
            } else if ("class java.lang.Long".equals(type)) {
                f.set(obj, 0L);
            } else if ("class java.lang.Integer".equals(type)) {
                f.set(obj, 0);
            } else if ("class java.math.BigDecimal".equals(type)) {
                f.set(obj, BigDecimal.ZERO);
            } else if ("class java.util.Date".equals(type)) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(1970, 0, 1, 0, 0, 0);
                f.set(obj, calendar.getTime());
            } else if ("class java.lang.Boolean".equals(type)) {
                f.set(obj, false);
            } else if ("class java.lang.Float".equals(type)) {
                f.set(obj, 0f);
            } else if ("class java.lang.Double".equals(type)) {
                f.set(obj, 0.0);
            } else if ("class java.lang.Short".equals(type)) {
                f.set(obj, (short) 0);
            } else if ("class java.lang.Byte".equals(type)) {
                f.set(obj, (byte) 0);
            } else {
                LoggerUtils.error(BeanInitUtils.class, type);
            }
        } catch (Exception e) {
            LoggerUtils.error(BeanInitUtils.class, new Object[]{obj, f}, e);
        }
    }

}
