package cn.seed.redis;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan
@ComponentScan(basePackageClasses = RedisApplication.class)
 public class RedisApplication {


}
