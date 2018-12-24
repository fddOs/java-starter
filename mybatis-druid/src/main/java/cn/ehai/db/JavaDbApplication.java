package cn.ehai.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@EnableAutoConfiguration
@Configuration
@MapperScan(basePackages = "cn.ehai.**.dao")
@ComponentScan(basePackageClasses = JavaDbApplication.class)
public class JavaDbApplication {
}
