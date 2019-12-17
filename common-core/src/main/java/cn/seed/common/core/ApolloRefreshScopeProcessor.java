package cn.seed.common.core;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        Class classes = AopUtils.getTargetClass(bean);
        ApolloRefreshScope apolloRefreshScope = AnnotationUtils.findAnnotation(classes, ApolloRefreshScope.class);
        addApolloRefreshInfo(apolloRefreshScope, beanName);
        Method[] methods = classes.getDeclaredMethods();
        for (Method method : methods) {
            Bean beanAnnotation = AnnotationUtils.findAnnotation(method, Bean.class);
            if (Objects.nonNull(beanAnnotation)) {
                addApolloRefreshInfo(AnnotationUtils.findAnnotation(method, ApolloRefreshScope.class), method.getName
                        ());
            }
        }
        return bean;
    }

    private void addApolloRefreshInfo(ApolloRefreshScope apolloRefreshScope, String className) {
        if (Objects.isNull(apolloRefreshScope)) {
            return;
        }
        List paramsList = APOLLO_REFRESH_SCOPE_MAP.get(className);
        paramsList = Objects.isNull(paramsList) ? new ArrayList<>() : paramsList;
        paramsList.addAll(Arrays.asList(apolloRefreshScope.paramNames()));
        Class[] paramClasses = apolloRefreshScope.paramClasses();
        for (Class paramClass : paramClasses) {
            for (Field field : paramClass.getDeclaredFields()) {
                paramsList.add(field.getName());
            }
        }
        APOLLO_REFRESH_SCOPE_MAP.put(className, paramsList);
    }
}