package cn.ehai.web.config;

import cn.ehai.common.core.ApolloBaseConfig;
import cn.ehai.common.core.Result;
import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ResultGenerator;
import cn.ehai.common.utils.SignUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import org.springframework.web.servlet.resource.VersionResourceResolver;

/**
 * Spring MVC 配置
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    private String signPlat= "dev";

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
//		registry.addMapping("/**");
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                //放行哪些原始域(头部信息)
                .allowedHeaders("*")
                .allowedOrigins("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if(!signPlat.equalsIgnoreCase(ApolloBaseConfig.getPlatForm())){
            //签名拦截器
            registry.addInterceptor(new RequestSignInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/druid/*")
                .excludePathPatterns("/heartbeat")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/v2/api-docs/**");
        }
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



}
