package cn.seed.common.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static cn.seed.common.utils.ProjectInfoUtils.APOLLO_REFRESH_SCOPE_MAP;

/**
 * 扫描ApolloRefreshScope注解
 *
 * @author 方典典
 * @time 2019/12/13 18:01
 */
@Component
public class ApolloRefreshScopeProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        Class classes = bean.getClass();
        ApolloRefreshScope apolloRefreshScope = AnnotationUtils.findAnnotation(classes, ApolloRefreshScope.class);
        if (Objects.nonNull(apolloRefreshScope)) {
            List paramsList = new ArrayList<>();
            paramsList.addAll(Arrays.asList(apolloRefreshScope.paramNames()));
            Class paramsClass = apolloRefreshScope.paramsClass();
            if (Objects.nonNull(paramsClass)) {
                for (Field field : paramsClass.getDeclaredFields()) {
                    paramsList.add(field.getName());
                }
            }
            APOLLO_REFRESH_SCOPE_MAP.put(classes.getName(), paramsList);
        }
        return bean;
    }
}