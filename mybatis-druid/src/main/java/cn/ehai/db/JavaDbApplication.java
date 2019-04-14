package cn.ehai.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = JavaDbApplication.class)
public class JavaDbApplication {
}
