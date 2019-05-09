package cn.seed.common.core;

import cn.seed.common.utils.AESUtils;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.springframework.util.StringUtils;

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
    public static Config getNamespace(String namespace) {
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
            seedConfigChangeListener.onChange(changeEvent.changedKeys());
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
        String str = config.getProperty(key, defaultValue);
        try {
            str = AESUtils.aesDecryptString(str);
        } catch (Exception e) {
            //IGNORE
        }
        return str;
    }
}
