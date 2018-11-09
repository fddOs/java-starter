package cn.ehai.javadb;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = JavaDbApplication.class)
public class JavaDbApplication {
}
