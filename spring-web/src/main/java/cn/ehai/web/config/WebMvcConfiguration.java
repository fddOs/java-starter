package cn.ehai.web.config;

import cn.ehai.common.core.*;
import cn.ehai.web.jwt.JwtFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.web.servlet.resource.VersionResourceResolver;

/**
 * Spring MVC 配置
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        //
        FastJsonConfig config = new FastJsonConfig();
        // 保留空的字段
        config.setSerializerFeatures(SerializerFeature.WriteMapNullValue,
                // String null -> ""
                SerializerFeature.WriteNullStringAsEmpty,
                // Number null -> 0
                SerializerFeature.WriteNullNumberAsZero,
                // boolean null
                SerializerFeature.WriteNullBooleanAsFalse,
                // ->false
                SerializerFeature.WriteDateUseDateFormat);
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(Charset.forName("UTF-8"));
        converters.add(converter);
    }

    // 解决跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if ("true".equals(ApolloBaseConfig.get("web.cross-domain", "false"))) {
            registry.addMapping("/**")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    //放行哪些原始域(头部信息)
                    .allowedHeaders("*")
                    .allowedOrigins("*");
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }

    /**
     * @param builder
     * @return ObjectMapper
     * @Description:描述 : xssObjectMapper
     * @exception:
     * @author: 徐正顺
     * @time:2017年11月9日 下午4:44:54
     */
    @Bean
    public ObjectMapper xssObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 解析器
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // 注册xss解析器
        SimpleModule xssModule = new SimpleModule("XssStringJsonSerializer");
        xssModule.addSerializer(new XssStringJsonSerializer());
        objectMapper.registerModule(xssModule);
        // 返回
        return objectMapper;
    }

    /**
     * 忽略请求url的大小写
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        pathMatcher.setCaseSensitive(false);
        configurer.setPathMatcher(pathMatcher);
    }

    @Bean(name = "verificationReqFilter")
    public FilterRegistrationBean verificationReqFilterRegister() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new VerificationReqFilter());
        registration.addUrlPatterns("/**");
        registration.setOrder(Integer.MIN_VALUE);
        registration.setEnabled(Boolean.valueOf(ApolloBaseConfig.get("web.sign.enable", "false")));
        return registration;
    }

    @Bean(name = "verificationResFilter")
    public FilterRegistrationBean VerificationResFilterRegister() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new VerificationResFilter());
        registration.addUrlPatterns("/**");
        registration.setOrder(100);
        registration.setEnabled(Boolean.valueOf(ApolloBaseConfig.get("web.sign.enable", "false")));
        return registration;
    }

    @Bean(name = "jwtFilter")
    public FilterRegistrationBean jwtFilterRegister() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new JwtFilter());
        registration.addUrlPatterns("/**");
        registration.setOrder(1);
        registration.setEnabled(Boolean.valueOf(ApolloBaseConfig.get("web.jwt.enable", "false")));
        return registration;
    }
}
