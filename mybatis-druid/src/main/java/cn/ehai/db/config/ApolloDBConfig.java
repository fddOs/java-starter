package cn.ehai.db.config;

import java.util.HashMap;
import java.util.Map;

import cn.ehai.common.core.*;
import cn.ehai.common.utils.AESUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

/**
 * @Description:Apollo配置
 * @author:方典典
 * @time:2018年5月22日 下午5:26:58
 */
@Component
@ConfigurationProperties(prefix = "db")
@EnableApolloConfig(value = {"EHI.DBConfig"})
public class ApolloDBConfig {

    @ApolloConfig("EHI.DBConfig")
    private Config dbConfig;
    private String key;

    /**
     * @return String
     * @Description:获取数据库链接
     * @exception:
     * @author: 方典典
     * @time:2018年5月22日 下午5:26:44
     */
    public String getDBConfig() {
        if (StringUtils.isEmpty(key)) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "请设置apollo数据库配置的Key!");
        }
        String dbConfigInfo = dbConfig.getProperty(key, "");
        try {
            return AESUtils.aesDecryptString(dbConfigInfo);
        } catch (Exception e) {
            return dbConfigInfo;
        }
    }

    /**
     * @param changeEvent void
     * @Description:监听数据库链接变化
     * @exception:
     * @author: 方典典
     * @time:2018年5月22日 下午5:27:26
     */
    @ApolloConfigChangeListener("EHI.DBConfig")
    private void configChangeListter(ConfigChangeEvent changeEvent) {
        if (changeEvent.isChanged("Apollo_YD_Onlineservice")) {
            DruidConfigution druidConfigution = SpringContext.getApplicationContext().getBean(DruidConfigution.class);
            druidConfigution.resetDataBase(initDBInfo());
        }
    }

    @Bean
    public DBInfo initDBInfo() {
        String separator = "\\|";
        String jdbcUrl = getDBConfig();
        if (!jdbcUrl.contains("user") || !jdbcUrl.contains("password") || !jdbcUrl.contains("?")) {
            throw new ServiceException(ResultCode.FAIL, "jdbcURL格式错误，请检查url" + jdbcUrl);
        }
        int index = jdbcUrl.indexOf('?') + 1;
        if (index >= jdbcUrl.length()) {
            throw new ServiceException(ResultCode.FAIL, "jdbcURL--? 位置错误，请检查url" + jdbcUrl);
        }
        String queryParam = jdbcUrl.substring(index);
        Map<String, String> signMap = new HashMap<>();
        if (!StringUtils.isEmpty(queryParam)) {
            String[] params = queryParam.split(separator);
            for (String param : params) {
                String[] string = param.split("=");
                if (string.length == 2 && !StringUtils.isEmpty(string[1])) {
                    signMap.put(string[0], string[1]);
                }
            }
        }
        String userName = signMap.get("user");
        String password = signMap.get("password");
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            throw new ServiceException(ResultCode.FAIL, "jdbcURL用户名或密码为空--" + jdbcUrl);
        }
        jdbcUrl = jdbcUrl.replace(password, "*");
        jdbcUrl = jdbcUrl.replace("|", "&");
        return new DBInfo(jdbcUrl, userName, password);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}