package cn.seed.common.core;

import cn.seed.common.utils.AESUtils;
import cn.seed.common.utils.ProjectInfoUtils;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:ConfigCenterWrapper
 * @author:方典典
 * @time:2019/4/29 16:07
 */
public class ConfigCenterWrapper {
    private static final Map<String, String> CONFIG_MAP = new ConcurrentHashMap<>();

    static {
        SeedConfigChangeListener seedConfigChangeListener = (String namespace, Set<String> keys) ->
                keys.forEach(key -> {
                    String value = getNamespace(namespace).getProperty(key, "");
                    try {
                        value = AESUtils.aesDecryptString(value);
                    } catch (Exception e) {
                        //IGNORE
                    }
                    CONFIG_MAP.put(key, value);
                });
        registerListenerConfig("application", seedConfigChangeListener);
        registerListenerConfig(ProjectInfoUtils.PROJECT_APOLLO_COMMON_NAMESPACE, seedConfigChangeListener);
        registerListenerConfig(ProjectInfoUtils.PROJECT_APOLLO_DB_NAMESPACE, seedConfigChangeListener);
    }

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
     * @param defaultValue
     * @return
     */
    public static String get(String key, String defaultValue) {
        return aesDecrypt(getNamespace(null), key, defaultValue);
    }

    public static String getDefaultEmpty(String key) {
        return aesDecrypt(getNamespace(null), key, "");
    }

    public static String getDefaultNull(String key) {
        return aesDecrypt(getNamespace(null), key, null);
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
        return aesDecrypt(getNamespace(namespace), key, defaultValue);
    }

    /**
     * 解密Apollo配置
     *
     * @param config
     * @param key
     * @param defaultValue
     * @return java.lang.String
     * @author 方典典
     * @time 2019/2/28 17:07
     */
    private static String aesDecrypt(Config config, String key, String defaultValue) {
        if (StringUtils.isEmpty(key)) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "请求失败:获取配置key为空，请稍后重试");
        }
        if (CONFIG_MAP.containsKey(key)) {
            return CONFIG_MAP.get(key);
        }
        String str = config.getProperty(key, AESUtils.aesEncryptString(defaultValue));
        try {
            str = AESUtils.aesDecryptString(str);
        } catch (Exception e) {
            //IGNORE
        }
        if (str == null) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "Apollo配置[" + key + "]不能为null");
        }
        CONFIG_MAP.put(key, str);
        return str;
    }
}
