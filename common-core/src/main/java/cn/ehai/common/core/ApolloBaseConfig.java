package cn.ehai.common.core;

import javax.annotation.PostConstruct;

import cn.ehai.common.utils.AESUtils;
import cn.ehai.common.utils.LoggerUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import org.springframework.util.StringUtils;

/**
 * @Description:Apollo配置
 * @author:方典典
 * @time:2018年5月22日 下午5:26:58
 */
@Component
@EnableApolloConfig(value = {"application"})
public class ApolloBaseConfig {

    @ApolloConfig("application")
    private static Config application;
    private LoggingSystem loggingSystem;

    /**
     * @return String
     * @Description:获取当前配置环境
     * @exception:
     * @author: 方典典
     * @time:2018年5月22日 下午6:06:04
     */
    public static String getPlatForm() {
        return aesDecrypt("platForm", "dev");
    }

    /**
     * @return String
     * @Description:启用POST日志记录
     * @exception:
     * @author: 方典典
     * @time:2018年5月22日 下午6:07:32
     */
    public static String getLogSwitch() {
        return aesDecrypt("logSwitch", "true");
    }

    /**
     * @return String
     * @Description:业务日志级别
     * @exception:
     * @author: 方典典
     * @time:2018年5月22日 下午6:08:04
     */
    public static String getServiceAnnotationLogLevel() {
        return aesDecrypt("serviceAnnotationLogLevel", "true");
    }

    public static String getServiceCommonLogLevel() {
        return aesDecrypt("serviceCommonLogLevel", "false");
    }

    private ApolloBaseConfig(LoggingSystem loggingSystem) {
        Assert.notNull(loggingSystem, "LoggingSystem must not be null");
        this.loggingSystem = loggingSystem;
    }

    /**
     * @Description:根据配置初始化日志级别 void
     * @exception:
     * @author: 方典典
     * @time:2018年5月22日 下午5:27:12
     */
    @PostConstruct
    private void initLogLevel() {
        String strLevel = aesDecrypt("logLevel", "info");
        String logName = aesDecrypt("logName", null);
        LogLevel level = LogLevel.valueOf(strLevel.toUpperCase());
        loggingSystem.setLogLevel(logName, level);
        LoggerUtils.isDebug = LoggerFactory.getLogger(LoggerUtils.class).isDebugEnabled();
        LoggerUtils.isInfo = LoggerFactory.getLogger(LoggerUtils.class).isInfoEnabled();
    }

    /**
     * @param changeEvent void
     * @Description:监听日志级别变化
     * @exception:
     * @author: 方典典
     * @time:2018年5月22日 下午5:27:26
     */
    @ApolloConfigChangeListener("application")
    private void configChangeListen(ConfigChangeEvent changeEvent) {
        if (changeEvent.isChanged("logLevel") || changeEvent.isChanged("logName")) {
            initLogLevel();
        }
    }

    public static String aesDecrypt(String key, String defaultValue) {
        if (StringUtils.isEmpty(key)) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "请求失败:获取配置key为空，请稍后重试");
        }
        String str = application.getProperty(key, defaultValue);
        if(StringUtils.isEmpty(str)){
            return defaultValue;
        }
        try {
            str = AESUtils.aesDecryptString(str);
        } catch (Exception e) {
            //TODO
        }
        return str;
    }

    /**
     * 根据key获取配置
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String get(String key, String defaultValue) {
        return aesDecrypt(key, defaultValue);
    }
}