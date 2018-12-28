package cn.ehai.common.core;

import cn.ehai.common.utils.AESUtils;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;

@Component
@EnableApolloConfig(value = {"EHI.JavaCommon"})
public class ApolloCommonConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApolloCommonConfig.class);

    @ApolloConfig("EHI.JavaCommon")
    private static Config javaCommon;
    private LoggingSystem loggingSystem;

    private static String aesDecrypt(String key, String defaultValue) {
        if (StringUtils.isEmpty(key)) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "请求失败:获取配置key为空，请稍后重试");
        }
        String str = javaCommon.getProperty(key, defaultValue);
        try {
            str = AESUtils.aesDecryptString(str);
        } catch (Exception e) {
            logger.error("ApolloCommonConfig 解密配置异常：", e);
        }
        return str;
    }

    /**
     * 根据key获取对应的apollo配置
     *
     * @param key
     * @return
     */
    public static String getApolloConfig(String key) {
        return aesDecrypt(key, "none");
    }
}
