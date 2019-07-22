package cn.seed.common.core;

import javax.annotation.PostConstruct;

import cn.seed.common.utils.LoggerUtils;
import cn.seed.common.utils.ProjectInfoUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.springframework.util.StringUtils;

/**
 * @Description:Apollo配置
 * @author:方典典
 * @time:2018年5月22日 下午5:26:58
 */
@Component
public class ApolloBaseConfig {

    private LoggingSystem loggingSystem;

    public static String getPlatForm() {
        return getCommonConfig("platForm", "dev");
    }

    public static String getLogSwitch() {
        return getCommonConfig("logSwitch", "true");
    }

    public static String getServiceAnnotationLogLevel() {
        return getCommonConfig("serviceAnnotationLogLevel", "true");
    }

    public static String getServiceCommonLogLevel() {
        return getCommonConfig("serviceCommonLogLevel", "false");
    }

    public static String getJwtEnable() {
        return getCommonConfig("jwtEnable", "false");
    }

    public static String getJwtExcludePath() {
        return getCommonConfig("jwtExcludePath", "");
    }

    public static String getWebLoginUrl() {
        return getCommonConfig("webLoginUrl", "");
    }

    public static String getReqDecode() {
        return getCommonConfig("reqDecode", "false");
    }

    public static String getReqSign() {
        return getCommonConfig("reqSign", "false");
    }

    public static String getSignExcludePath() {
        return getCommonConfig("signExcludePath", "");
    }

    public static String getDecodeExcludePath() {
        return getCommonConfig("decodeExcludePath", "");
    }

    public static String getAuthUrl() {
        return getCommonConfig("authUrl", null);
    }

    public static String getWebCrossDomain() {
        return getCommonConfig("webCrossDomain", "false");
    }

    public static String getMessageCenterUrl() {
        return getCommonConfig("messageCenterUrl", "false");
    }

    public static String getRedisOptionalUrl() {
        return getCommonConfig("redis.optional.url", null);
    }

    public static boolean getOkHttpSSLEnable() {
        return Boolean.getBoolean(getCommonConfig("okHttpSSLEnable", "false"));
    }

    public static String getSignHeader() {
        return getCommonConfig("signHeader", "x-seed-sign");
    }

    private ApolloBaseConfig(LoggingSystem loggingSystem) {
        Assert.notNull(loggingSystem, "LoggingSystem must not be null");
        this.loggingSystem = loggingSystem;
    }

    /**
     * 根据配置初始化日志级别 void
     *
     * @param
     * @return void
     * @author 方典典
     * @time 2019/2/28 17:07
     */
    @PostConstruct
    private void initLogLevel() {
        String strLevel = getCommonConfig("logLevel", "info");
        String logName = getCommonConfig("logName", null);
        LogLevel level = LogLevel.valueOf(strLevel.toUpperCase());
        loggingSystem.setLogLevel(logName, level);
        LoggerUtils.isDebug = LoggerFactory.getLogger(LoggerUtils.class).isDebugEnabled();
        LoggerUtils.isInfo = LoggerFactory.getLogger(LoggerUtils.class).isInfoEnabled();
    }

    /**
     * 监听日志级别变化
     *
     * @param changeEvent
     * @return void
     * @author 方典典
     * @time 2019/2/28 17:07
     */
    @ApolloConfigChangeListener("application")
    private void configChangeListen(ConfigChangeEvent changeEvent) {
        if (changeEvent.isChanged("logLevel") || changeEvent.isChanged("logName")) {
            initLogLevel();
        }
    }

    /**
     * 获取公共配置
     *
     * @param key
     * @param defaultValue
     * @return java.lang.String
     * @author 方典典
     * @time 2019/2/28 17:30
     */
    private static String getCommonConfig(String key, String defaultValue) {
        String prefix = ProjectInfoUtils.PROJECT_CONTEXT + ".";
        String value = ConfigCenterWrapper.get(ProjectInfoUtils.PROJECT_APOLLO_COMMON_NAMESPACE, prefix + key, "");
        if (StringUtils.isEmpty(value)) {
            value = ConfigCenterWrapper.get(ProjectInfoUtils.PROJECT_APOLLO_COMMON_NAMESPACE, key, defaultValue);
        }
        return value;
    }

}