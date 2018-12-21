package cn.ehai.log;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = JavaLogApplication.class)
@MapperScan(basePackages = "cn.ehai.log.dao")
public class JavaLogApplication {

}
