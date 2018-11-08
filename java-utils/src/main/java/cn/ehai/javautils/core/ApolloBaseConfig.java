package cn.ehai.javautils.core;

import java.util.List;

import javax.annotation.PostConstruct;

import cn.ehai.javautils.utils.AESUtils;
import cn.ehai.javautils.utils.LoggerUtils;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import cn.ehai.online.common.util.LoggerUtils;
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
     * @Description:获取SocketIP
     * @params:[]
     * @return:java.lang.String
     * @exception:
     * @author: 方典典
     * @time:2018/7/17 11:06
     */
    public static String getSocketIP() {
        return aesDecrypt("online-service.socketIP", "");
    }

    /**
     * @Description:获取reportApiUrl
     * @params:[]
     * @return:java.lang.String
     * @exception:
     * @author: 方典典
     * @time:2018/7/17 11:06
     */
    public static String getReportApiUrl() {
        return aesDecrypt("reportApiUrl", "");
    }

    /**
     * @Description:获取reportApiToken
     * @params:[]
     * @return:java.lang.String
     * @exception:
     * @author: 方典典
     * @time:2018/7/17 11:06
     */
    public static String getReportApiToken() {
        return aesDecrypt("reportApiToken", "");
    }

    /**
     * @Description:获取SocketPort
     * @params:[]
     * @return:java.lang.Integer
     * @exception:
     * @author: 方典典
     * @time:2018/7/17 11:06
     */
    public static Integer getSocketPort() {
        return Integer.valueOf(aesDecrypt("online-service.socketPort", null));
    }

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
    public static String getServiceLogLevel() {
        return aesDecrypt("serviceLogLevel", "info");
    }

    /**
     * @return String
     * @Description:资源文件地址
     * @exception:
     * @author: 方典典
     * @time:2018年5月30日 上午10:52:33
     */
    public static List<String> getResourcesUrl() {
        return (List<String>) JSONObject.parse(aesDecrypt("resourcesUrl ", "[]"));
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

    private static String aesDecrypt(String key, String defaultValue) {
        if (StringUtils.isEmpty(key)) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "请求失败:获取配置key为空，请稍后重试");
        }
        String str = application.getProperty(key, defaultValue);
        try {
            str = AESUtils.aesDecryptString(str);
        } catch (Exception e) {
            //TODO
        }
        return str;
    }
}