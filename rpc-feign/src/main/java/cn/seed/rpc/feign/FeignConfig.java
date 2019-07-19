package cn.seed.rpc.feign;

import cn.seed.common.utils.ProjectInfoUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import feign.Feign;
import feign.Request.Options;
import feign.Retryer;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.opentracing.TracingClient;
import feign.slf4j.Slf4jLogger;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author chenxl
 * @description: 注入feign相关的配置项
 * @time 2018/08/27
 **/
@Configuration
public class FeignConfig {
    @Autowired
    private FeignRequestInterceptor feignRequestInterceptor;

    @Bean("builder")
    public Feign.Builder create(okhttp3.OkHttpClient okHttpClient, Tracer tracer) {
        TracingClient tracingClient = new TracingClient(new OkHttpClient(okHttpClient), tracer);
        return Feign.builder().requestInterceptor(feignRequestInterceptor)
                .client(tracingClient).retryer(Retryer.NEVER_RETRY)
                .errorDecoder(new ErrorExceptionDecoder())
                .encoder(new FormEncoder(new JacksonEncoder(new ObjectMapper().setSerializationInclusion(JsonInclude.Include
                        .NON_NULL).configure(SerializationFeature.INDENT_OUTPUT, false))))
                .decoder(new JacksonDecoder(createObjectMapper()))
                .logger(new Slf4jLogger())
                .logLevel(feign.Logger.Level.FULL)
                .options(new Options(ProjectInfoUtils.PROJECT_FEIGN_CONNECT_TIMEOUT_MILLIS, ProjectInfoUtils
                        .PROJECT_FEIGN_READ_TIMEOUT_MILLIS));
    }

    /**
     * 设置 Jackson 的时区以及日期的格式
     *
     * @param
     * @return com.fasterxml.jackson.databind.ObjectMapper
     * @author xianglong.chen
     * @time 2019/1/7 10:46
     */
    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .setTimeZone(TimeZone.getTimeZone("GMT+8:00"))
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
