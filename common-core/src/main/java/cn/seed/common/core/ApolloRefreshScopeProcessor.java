package cn.seed.common.core;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringUtils;

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
    private PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}", ":", false);
    private PropertyPlaceholderHelper.PlaceholderResolver resolver = (String placeholderName) -> placeholderName
            .substring(0, placeholderName.lastIndexOf(":"));

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
        String className;
        for (Method method : methods) {
            Bean beanAnnotation = AnnotationUtils.findAnnotation(method, Bean.class);
            if (Objects.nonNull(beanAnnotation)) {
                if (beanAnnotation.name().length != 0) {
                    className = beanAnnotation.name()[0];
                } else {
                    className = method.getName();
                }
                addApolloRefreshInfo(AnnotationUtils.findAnnotation(method, ApolloRefreshScope.class), className);
            }
        }
        return bean;
    }

    /**
     * 添加ApolloBeanInfo
     *
     * @param apolloRefreshScope
     * @param className
     * @return void
     * @author 方典典
     * @time 2019/12/21 10:32
     */
    private void addApolloRefreshInfo(ApolloRefreshScope apolloRefreshScope, String className) {
        if (Objects.isNull(apolloRefreshScope)) {
            return;
        }
        List paramsList = APOLLO_REFRESH_SCOPE_MAP.get(className);
        paramsList = Objects.isNull(paramsList) ? new ArrayList<>() : paramsList;
        paramsList.addAll(Arrays.asList(apolloRefreshScope.paramNames()));
        Class[] paramClasses = apolloRefreshScope.paramClasses();
        String paramName = null;
        for (Class paramClass : paramClasses) {
            for (Field field : paramClass.getDeclaredFields()) {
                Value value = AnnotationUtils.findAnnotation(field, Value.class);
                if (Objects.nonNull(value)) {
                    paramName = propertyPlaceholderHelper.replacePlaceholders(value.value(), resolver);
                }
                if (StringUtils.isEmpty(paramName)) {
                    paramName = field.getName();
                }
                paramsList.add(paramName);
            }
        }
        APOLLO_REFRESH_SCOPE_MAP.put(className, paramsList);
    }
}