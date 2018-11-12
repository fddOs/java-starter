package cn.ehai.javarpc.feign;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import feign.Feign;
import feign.Request.Options;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.opentracing.TracingClient;
import feign.slf4j.Slf4jLogger;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenxl
 * @description: 注入feign相关的配置项
 * @time 2018/08/27
 **/
@Configuration
@EnableConfigurationProperties(FeignProperties.class)
public class FeignConfig {
    @Autowired
    private FeignProperties feignProperties;

    @Bean
    public Feign.Builder create(okhttp3.OkHttpClient okHttpClient, Tracer tracer) {
        TracingClient tracingClient = new TracingClient(new OkHttpClient(okHttpClient), tracer);
        return Feign.builder()
                .client(tracingClient)
                .errorDecoder(new ErrorExceptionDecoder())
                .encoder(new JacksonEncoder(new ObjectMapper().setSerializationInclusion(JsonInclude.Include
                        .NON_NULL).configure(SerializationFeature.INDENT_OUTPUT, false)))
                .decoder(new JacksonDecoder())
                .logger(new Slf4jLogger())
                .logLevel(feign.Logger.Level.FULL)
                .options(new Options(feignProperties.getConnectTimeoutMillis(), feignProperties.getReadTimeoutMillis
                        ()));
    }
}
