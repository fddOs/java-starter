package cn.seed.authority.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能权限校验
 *
 * @author 方典典
 * @time 2019/11/27 17:02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FunctionAuthVerify {

    /**
     * 系统编码
     */
    String systemCode() default "";

    /**
     * 模块id
     */
    String[] moduleIds();
}
