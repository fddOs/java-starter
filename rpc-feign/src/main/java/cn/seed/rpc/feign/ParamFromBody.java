package cn.seed.rpc.feign;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ParamFromBody
 *
 * @author:方典典
 * @time:2019/8/1 17:54
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ParamFromBody {

}
