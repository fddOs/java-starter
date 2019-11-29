package cn.seed.authority.config;

import cn.seed.authority.interceptor.AuthenticationInterceptor;
import cn.seed.authority.service.WebAuthority;
import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.ConfigCenterWrapper;
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

    private WebAuthority webAuthority;

    public AuthorityConfiguration(WebAuthority webAuthority) {
        Objects.requireNonNull(webAuthority);
        this.webAuthority = webAuthority;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        boolean enableFunctionAuthVerify = ApolloBaseConfig.getEnableFunctionAuthVerify();
        String userEHiAuthority = ConfigCenterWrapper.get("userEHiAuthority", "false");
        if (enableFunctionAuthVerify||"true".equals(userEHiAuthority)) {
            registry.addInterceptor(new AuthenticationInterceptor(webAuthority));
        }
    }

}
