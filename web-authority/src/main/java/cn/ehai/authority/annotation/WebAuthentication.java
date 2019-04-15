package cn.ehai.authority.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限验证注解
 *
 * @author xianglong.chen
 * @description WebAuthentication
 * @time 2019/2/21 上午9:56
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebAuthentication {

    /**
     *
     * 系统编码
     */
    String systemCode();

    /**
     *
     * 模块id
     */
    String[] moduleIds();
}
