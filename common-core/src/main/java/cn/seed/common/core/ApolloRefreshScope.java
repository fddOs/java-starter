package cn.seed.common.core;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ApolloRefreshScope
 *
 * @author:方典典
 * @time:2019/12/13 17:44
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Scope("refresh")
@RefreshScope
public @interface ApolloRefreshScope {
    /**
     * 参数名称
     */
    String[] paramNames() default {};

    /**
     * 配置类名
     */
    Class<?>[] paramClasses() default {};

    /**
     * ScopeProxyMode
     */
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;
}
