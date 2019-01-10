package cn.ehai.web;

import brave.internal.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.logging.Level;

/**
 *
 * @description
 * @author xianglong.chen
 * @time 2019/1/9 14:06
 */
@Configuration
@ComponentScan(basePackageClasses = JavaWebApplication.class)
public class JavaWebApplication {

    private static Logger logger = LoggerFactory.getLogger(JavaWebApplication.class);

    {
        try {
            initBraveLoggerLevel();
        } catch (Exception e) {
            logger.error("BraceTracer 日志级别初始化异常", e);
        }
    }

    /**
     * 初始化 BraceTracer 日志的级别
     *
     * @param
     * @return void
     * @author xianglong.chen
     * @time 2019/1/9 14:01
     */
    private static void initBraveLoggerLevel() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Field field = Class.forName("brave.internal.Platform").getDeclaredField("logger");
        field.setAccessible(true);
        java.util.logging.Logger logger = (java.util.logging.Logger) field.get(Platform.get());
        logger.setLevel(Level.WARNING);
    }
}
