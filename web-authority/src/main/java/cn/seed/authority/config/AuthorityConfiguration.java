package cn.seed.authority.config;

import cn.seed.authority.interceptor.AuthenticationInterceptor;
import cn.seed.authority.service.AuthService;
import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.ConfigCenterWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xianglong.chen
 * @time 2019/2/21 上午9:46
 */
@Configuration
@DependsOn(value = "apolloBaseConfig")
public class AuthorityConfiguration implements WebMvcConfigurer {

    @Autowired
    private AuthService authService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        boolean enableFunctionAuthVerify = ApolloBaseConfig.getEnableFunctionAuthVerify();
        String userEHiAuthority = ConfigCenterWrapper.get("userEHiAuthority", "false");
        if (enableFunctionAuthVerify||"true".equals(userEHiAuthority)) {
            registry.addInterceptor(new AuthenticationInterceptor(authService));
        }
    }

}
