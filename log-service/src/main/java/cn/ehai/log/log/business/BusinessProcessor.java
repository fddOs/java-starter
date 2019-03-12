package cn.ehai.log.log.business;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * 获取自定义注解需要存储的表名
 *
 * @author lixiao
 * @date 2019-03-09 21:08
 */
@Component
public class BusinessProcessor implements BeanPostProcessor {

    @Override public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException {
        return bean;
    }

    @Override public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
        Class classes = bean.getClass();
       Method[] methods =  classes.getDeclaredMethods();
        for (Method method :methods){
            //如果方法添加了注解，获取注解里面需要保存的表名
            BusinessLog businessLog = AnnotationUtils.findAnnotation(method,BusinessLog.class);
            if(businessLog==null){
                continue;
            }
            String[] strings = businessLog.oprTableName().split(",");
            if(strings!=null&&strings.length>0){
                BusinessTableUtils.addBusiTables(strings);
            }
        }
        return bean;
    }
}
