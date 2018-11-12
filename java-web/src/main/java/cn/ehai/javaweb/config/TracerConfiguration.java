package cn.ehai.javaweb.config;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Tracer;
import io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracerConfiguration extends TracerAutoConfiguration {

    @Override
    @Bean
    public Tracer getTracer() {
        return BraveTracer.create(Tracing.newBuilder().build());
    }
}
