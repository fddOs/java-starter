package cn.seed.common.core;

import org.springframework.util.StringUtils;

/**
 * 参数异常检查
 * throw ServiceException
 *
 * @author lixiao
 * @date 2019-04-06 11:25
 */
public class ServiceExpUtils {

    /**
     * 对象是否为null 如果参数为null 抛出ServiceException
     *
     * @param object  传入对象
     * @param message 提示的message
     * @return void
     * @author lixiao
     * @date 2019-04-06 11:28
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throwExption(ResultCode.DATA_ERROR, message);
        }
    }

    /**
     * 是否抛出自定义异常
     *
     * @param check   如为true 抛出异常
     * @param message 异常信息
     * @return boolean
     * @author lixiao
     * @date 2019-04-06 11:35
     */
    public static void check(boolean check, String message) {
        if (check) {
            throwExption(ResultCode.DATA_ERROR, message);
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 验证的字符串
     * @param msg 提示信息
     * @return void
     * @author lixiao
     * @date 2019-04-06 11:46
     */
    public static void stringIsEmpty(String str, String msg) {
        if (StringUtils.isEmpty(str)) {
            throwExption(ResultCode.DATA_ERROR, msg);
        }
    }


    /**
     * 直接抛出异常
     * 直接抛异常请不要使用此方法, 请直接使用 {@code throw new ServiceException(code, msg)}
     * 因为你调用此方法抛异常会造成两个问题：
     * 1. idea 检测不出你这行代码是抛异常的
     * 2. 各种代码检测工具也会出问题
     *
     * @param code 错误的类型码
     * @param msg  错误的提示
     * @author lixiao
     * @date 2019-04-09 20:49
     * @deprecated 此方法被废弃, 之后若干版本将移除, 请用到的地方尽快更改
     */
    @Deprecated
    public static void throwExp(ResultCode code, String msg) throws ServiceException {
        throw new ServiceException(code, msg);
    }

    /**
     * 抛出异常
     *
     * @param code 错误的类型码
     * @param msg  错误的提示
     * @throws ServiceException java 统一的业务错误异常类
     */
    private static void throwExption(ResultCode code, String msg) throws ServiceException {
        throw new ServiceException(code, msg);
    }

}
