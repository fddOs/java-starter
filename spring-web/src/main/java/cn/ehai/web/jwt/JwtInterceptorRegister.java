package cn.ehai.web.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring MVC 配置
 */
@Configuration
@ConditionalOnProperty(value = "jwt.enabled", havingValue = "true")
public class JwtInterceptorRegister extends WebMvcConfigurerAdapter {

    @Autowired
    JwtProjectProperty jwtProjectProperty;
    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration jwtInterceptorRegistration =  registry.addInterceptor(new JwtHandlerInterceptorAdapter())
            .addPathPatterns("/**").excludePathPatterns("/druid/**")
                .excludePathPatterns("/swagger-resources/**").excludePathPatterns(
                "/v2/**").excludePathPatterns("/login").excludePathPatterns("/heartbeat");
        jwtProjectProperty.getUrl().stream()
            .forEach(url->{jwtInterceptorRegistration.excludePathPatterns(url);});
    }

}
