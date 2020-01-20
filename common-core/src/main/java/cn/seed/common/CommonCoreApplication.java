package cn.seed.common;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@ConfigurationPropertiesScan
@ComponentScan(basePackageClasses = CommonCoreApplication.class)
@EnableMBeanExport(registration = RegistrationPolicy.REPLACE_EXISTING)
public class CommonCoreApplication {
}
