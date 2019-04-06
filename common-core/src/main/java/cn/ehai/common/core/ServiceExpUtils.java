package cn.ehai.common.core;

import org.springframework.util.StringUtils;

/**
 * 参数异常检查
 * throw ServiceException
 * @author lixiao
 * @date 2019-04-06 11:25
 */
public class ServiceExpUtils {

    /**
     * 对象是否为null 如果参数为null 抛出ServiceException
     * @param object 传入对象
     * @param message 提示的message
     * @return void
     * @author lixiao
     * @date 2019-04-06 11:28
     */
    public static void notNull(Object object,String message){
        if(object==null){
            throwExp(ResultCode.DATA_ERROR,message);
        }
    }

    /**
     * 是否抛出自定义异常
     * @param check  如为true 抛出异常
     * @param message 异常信息
     * @return boolean
     * @author lixiao
     * @date 2019-04-06 11:35
     */
    public static void check(boolean check,String message){
        if(check){
            throwExp(ResultCode.DATA_ERROR,message);
        }
    }
    /**
     * 判断字符串是否为空
     * @param str 验证的字符串
     * @param msg 提示信息
     * @return void
     * @author lixiao
     * @date 2019-04-06 11:46
     */
    public static void stringIsEmpty(String str,String msg){
        if(StringUtils.isEmpty(str)){
            throwExp(ResultCode.DATA_ERROR,msg);
        }
    }


    private static void throwExp(ResultCode code,String msg){
        throw new ServiceException(code,msg);
    }
}
