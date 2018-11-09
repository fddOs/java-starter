package cn.ehai.javautils;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = UtilsApplication.class)
public class UtilsApplication {
}
