package cn.ehai.javaweb;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = JavaWebApplication.class)
public class JavaWebApplication {
}
