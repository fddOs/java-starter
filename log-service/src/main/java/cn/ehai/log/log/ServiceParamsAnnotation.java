package cn.ehai.log.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description:用于描述业务类型参数是否从方法里面取的注解
 * @author:方典典
 * @time:2017年12月18日 下午4:11:56
 */
@Target(ElementType.METHOD)
@Retention(RUNTIME)
@Documented
public @interface ServiceParamsAnnotation {
    int userIdIndex();

    int referIdIndex();
}