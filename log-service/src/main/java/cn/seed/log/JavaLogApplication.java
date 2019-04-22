package cn.seed.log;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@ComponentScan(basePackageClasses = JavaLogApplication.class)
@EnableAsync
public class JavaLogApplication {

}
