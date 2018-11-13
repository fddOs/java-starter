package cn.ehai.log;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = ElkApplication.class)
public class ElkApplication {
}
