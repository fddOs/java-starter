package cn.seed.common.core;

import cn.seed.common.utils.ProjectInfoUtils;
import com.ctrip.framework.apollo.core.ConfigConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static cn.seed.common.core.ConfigCenterWrapper.registerListenerConfig;

/**
 * Apollo配置Bean自动刷新
 *
 * @author 方典典
 * @return
 * @time 2019/9/26 10:17
 */
@Configuration
@Import(ApolloConfigRegistrar.class)
public class ApolloConfigBeanRefresh implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Autowired
    private RefreshScope refreshScope;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        apolloConfigListener();
    }

    private void apolloConfigListener() {
        SeedConfigChangeListener seedConfigChangeListener = (String namespace, Set<String> keys) ->
                publicApolloConfigChange(keys);
        registerListenerConfig(ConfigConsts.NAMESPACE_APPLICATION, seedConfigChangeListener);
        registerListenerConfig(ProjectInfoUtils.PROJECT_APOLLO_COMMON_NAMESPACE, seedConfigChangeListener);
        registerListenerConfig(ProjectInfoUtils.PROJECT_APOLLO_DB_NAMESPACE, seedConfigChangeListener);
    }

    private void publicApolloConfigChange(Set<String> configKeys) {
        //更新配置
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(configKeys));
        refreshScope.refreshAll();
    }

}