package cn.ehai.web.config;

import cn.ehai.common.core.*;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

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

}
