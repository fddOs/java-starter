package cn.seed.db;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@EnableAutoConfiguration
@Configuration
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
@ComponentScan(basePackageClasses = JavaDbApplication.class)
public class JavaDbApplication {
}
