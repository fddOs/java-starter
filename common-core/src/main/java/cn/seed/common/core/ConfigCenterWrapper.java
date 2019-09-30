package cn.seed.common.core;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * @Description:ConfigCenterWrapper
 * @author:方典典
 * @time:2019/4/29 16:07
 */
public class ConfigCenterWrapper {

    /**
     * 获取指定的namespace config
     *
     * @param namespace
     * @return com.ctrip.framework.apollo.Config
     * @author 方典典
     * @time 2019/4/29 16:10
     */
    private static Config getNamespace(String namespace) {
        if (StringUtils.isEmpty(namespace)) {
            // return application config
            return ConfigService.getAppConfig();
        }
        return ConfigService.getConfig(namespace);
    }

    /**
     * 注册apollo配置监听器
     *
     * @param namespace
     * @param seedConfigChangeListener
     * @return void
     * @author 方典典
     * @time 2019/4/29 16:16
     */
    public static void registerListenerConfig(String namespace, SeedConfigChangeListener seedConfigChangeListener) {
        getNamespace(namespace).addChangeListener((ConfigChangeEvent changeEvent) -> {
            seedConfigChangeListener.onChange(namespace, changeEvent.changedKeys());
        });
    }

    /**
     * 根据key获取application内的配置
     *
     * @param key
     * @param defaultValue 不能为null
     * @return
     */
    public static String get(String key, @NotNull String defaultValue) {
        return getNamespace(null).getProperty(key, defaultValue);
    }

    public static String getDefaultEmpty(String key) {
        return getNamespace(null).getProperty(key, "");
    }

    public static String getDefaultNull(String key) {
        return getNamespace(null).getProperty(key, null);
    }

    /**
     * 根据key获取指定namespace内的配置
     *
     * @param namespace
     * @param key
     * @param defaultValue
     * @return
     */
    public static String get(String namespace, String key, String defaultValue) {
        return getNamespace(namespace).getProperty(key, defaultValue);
    }

}
