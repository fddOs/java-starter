package cn.seed.common.core;

import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * ApolloRefreshScope
 * @author:方典典
 * @time:2019/12/13 17:44
 */
@RefreshScope
public @interface ApolloRefreshScope {
    String[] paramNames();
    Class paramsClass();
}
