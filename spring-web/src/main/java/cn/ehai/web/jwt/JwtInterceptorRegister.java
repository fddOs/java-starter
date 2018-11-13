package cn.ehai.web.jwt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring MVC 配置
 */
@Configuration
@ConditionalOnProperty(value = "jwt.enabled", havingValue = "true", matchIfMissing = true)
public class JwtInterceptorRegister extends WebMvcConfigurerAdapter {

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtHandlerInterceptorAdapter()).excludePathPatterns("/druid/**")
                .excludePathPatterns("/swagger-resources/**").excludePathPatterns(
                "/v2/**").excludePathPatterns("/login");
    }

}
