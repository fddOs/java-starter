package cn.ehai.authority.config;

import cn.ehai.authority.http.api.AuthApi;
import cn.ehai.authority.interceptor.AuthenticationInterceptor;
import cn.ehai.common.core.ApolloBaseConfig;
import feign.Feign;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Objects;

/**
 * @author xianglong.chen
 * @time 2019/2/21 上午9:46
 */
@Configuration
@DependsOn(value = "apolloBaseConfig")
public class AuthorityConfiguration extends WebMvcConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Feign.Builder builder;

    public AuthorityConfiguration(Feign.Builder builder) {
        Objects.requireNonNull(builder);
        this.builder = builder;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String authUrl = ApolloBaseConfig.getCommonConfig("authUrl", null);
        if (StringUtils.isEmpty(authUrl)) {
            throw new IllegalArgumentException("authUrl参数获取失败，请在ApolloCommonConfig中检查该配置项");
        }
        String userEHiAuthority = ApolloBaseConfig.get("userEHiAuthority", "false");
        if ("true".equals(userEHiAuthority)) {
            AuthApi authApi = builder.target(AuthApi.class, authUrl);
            registry.addInterceptor(new AuthenticationInterceptor(authApi));
        } else {
            logger.warn("当前项目未读取到userEHiAuthority配置项，权限验证功能未启用！");
        }
    }

}
