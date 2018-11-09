package cn.ehai.javaweb.config;

import cn.ehai.javautils.core.ApolloBaseConfig;
import cn.ehai.javautils.core.Result;
import cn.ehai.javautils.core.ResultCode;
import cn.ehai.javautils.core.ResultGenerator;
import cn.ehai.javautils.utils.SignUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Spring MVC 配置
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(WebMvcConfiguration.class);

    // 使用阿里 FastJson 作为JSON MessageConverter
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        //
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.WriteMapNullValue, // 保留空的字段
                SerializerFeature.WriteNullStringAsEmpty, // String null -> ""
                SerializerFeature.WriteNullNumberAsZero, // Number null -> 0
                SerializerFeature.WriteNullBooleanAsFalse, // boolean null
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

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!"dev".equals(ApolloBaseConfig.getPlatForm())) { // 开发环境忽略签名认证
            registry.addInterceptor(new HandlerInterceptorAdapter() {
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                        throws Exception {
                    // 验证签名
                    EhiHttpServletRequestWrapper ehiHttpServletRequestWrapper = new EhiHttpServletRequestWrapper(
                            request);
                    if (validateSign(ehiHttpServletRequestWrapper)) {
                        return true;
                    } else {
                        logger.warn("签名认证失败，请求接口：{}，请求IP：{}，请求参数：{}", request.getRequestURI(), getIpAddress(request),
                                JSON.toJSONString(request.getParameterMap()));
                        responseResult(response, ResultGenerator.genFailResult(ResultCode.UNAUTHORIZED, "签名认证失败"));
                        return false;
                    }
                }
            }).excludePathPatterns("/druid/*").excludePathPatterns("/heart");
        }
    }

    private void responseResult(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * 一个简单的签名认证，规则： 1. 将请求参数按ascii码排序 2. 拼接为a=value&b=value...这样的字符串（不包含sign）
     * 3. 混合密钥（secret）进行md5获得签名，与请求的签名进行比较
     */
    private boolean validateSign(EhiHttpServletRequestWrapper request) {
        String requestSign = request.getHeader("Content-MD5");// 获得请求签名，如sign=19e907700db7ad91318424a97c54ed57

        BufferedReader br;
        String bodyStr = "";
        try {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                bodyStr += str;
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        String query = request.getQueryString();
        if (StringUtils.isEmpty(requestSign) && StringUtils.isEmpty(query) && StringUtils.isEmpty(bodyStr)) {
            return false;
        }
        String sign = SignUtils.sign(query, bodyStr);
        logger.info(sign);
        logger.info(requestSign);
        return StringUtils.equals(sign.toLowerCase(), requestSign.toLowerCase());// 比较
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，那么取第一个ip为客户端ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }

        return ip;
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
//    @Primary
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
