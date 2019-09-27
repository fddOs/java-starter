package cn.seed.common.core;

import cn.seed.common.utils.ProjectInfoUtils;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.spring.annotation.ApolloAnnotationProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;

import com.ctrip.framework.apollo.spring.config.PropertySourcesProcessor;
import com.ctrip.framework.apollo.spring.util.BeanRegistrationUtil;
import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class ApolloConfigRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String[] namespaces = new String[]{ConfigConsts.NAMESPACE_APPLICATION, ProjectInfoUtils
                .PROJECT_APOLLO_COMMON_NAMESPACE, ProjectInfoUtils.PROJECT_APOLLO_DB_NAMESPACE};
        int order = Ordered.LOWEST_PRECEDENCE;
        PropertySourcesProcessor.addNamespaces(Lists.newArrayList(namespaces), order);

        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, PropertySourcesPlaceholderConfigurer.class
                        .getName(),
                PropertySourcesPlaceholderConfigurer.class);

        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, PropertySourcesProcessor.class.getName(),
                PropertySourcesProcessor.class);

        BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ApolloAnnotationProcessor.class.getName(),
                ApolloAnnotationProcessor.class);
    }
}
