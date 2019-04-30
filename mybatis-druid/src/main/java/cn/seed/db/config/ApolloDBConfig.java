package cn.seed.db.config;

import cn.seed.common.core.ApolloConfigWrapper;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.common.core.SpringContext;

import java.util.HashMap;
import java.util.Map;

import cn.seed.common.utils.ProjectInfoUtils;
import com.ctrip.framework.apollo.ConfigChangeListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;

import javax.annotation.PostConstruct;

/**
 * Apollo配置
 *
 * @author:方典典
 * @time:2018年5月22日 下午5:26:58
 */
@Component
public class ApolloDBConfig {

    /**
     * @param key
     * @return String
     * 获取数据库链接
     * @author: 方典典
     * @time:2018年5月22日 下午5:26:44
     */
    public String getDBConfig(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "请设置apollo数据库配置的Key!");
        }
        String dbConfigInfo = ApolloConfigWrapper.get(ProjectInfoUtils.PROJECT_APOLLO_DB_NAMESPACE, key, "");
        if (StringUtils.isEmpty(dbConfigInfo)) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "apollo数据库配置为空！");
        }
        return dbConfigInfo;
    }

    /**
     * 监听数据库链接变化
     *
     * @author: 方典典
     * @time:2018年5月22日 下午5:27:26
     */
    @PostConstruct
    private void dbConfigChangeListen() {
        ApolloConfigWrapper.registerListenerConfig(ProjectInfoUtils.PROJECT_APOLLO_DB_NAMESPACE,
                (ConfigChangeEvent changeEvent) -> {
                    if (changeEvent.isChanged(ProjectInfoUtils.PROJECT_APOLLO_DB_KEY)) {
                        DruidConfiguration druidConfiguration = SpringContext.getApplicationContext().getBean
                                (DruidConfiguration.class);
                        druidConfiguration.resetDataBase(masterDBInfo());
                    } else if (ProjectInfoUtils.PROJECT_APOLLO_ARCHIVE_ENABLED && changeEvent.isChanged(ProjectInfoUtils
                            .PROJECT_APOLLO_ARCHIVE_DB_KEY)) {
                        ArchiveDruidConfiguration archiveDruidConfiguration = SpringContext.getApplicationContext()
                                .getBean
                                        (ArchiveDruidConfiguration.class);
                        archiveDruidConfiguration.resetArchiveDataBase(archiveDBInfo());
                    }
                });

    }

    @Bean("masterDB")
    public DBInfo masterDBInfo() {
        String jdbcUrl = getDBConfig(ProjectInfoUtils.PROJECT_APOLLO_DB_KEY);
        return initDBInfo(jdbcUrl);
    }

    @Bean("archiveDB")
    public DBInfo archiveDBInfo() {
        if (ProjectInfoUtils.PROJECT_APOLLO_ARCHIVE_ENABLED) {
            String jdbcUrl = getDBConfig(ProjectInfoUtils.PROJECT_APOLLO_ARCHIVE_DB_KEY);
            return initDBInfo(jdbcUrl);
        }
        return new DBInfo();
    }

    private DBInfo initDBInfo(String jdbcUrl) {
        String separator = "\\|";
        String user = "user";
        String pass = "password";
        String urlFlag = "?";
        if (!jdbcUrl.contains(user) || !jdbcUrl.contains(pass) || !jdbcUrl.contains(urlFlag)) {
            throw new ServiceException(ResultCode.FAIL, "jdbcURL格式错误，请检查url" + jdbcUrl);
        }
        int index = jdbcUrl.indexOf(urlFlag) + 1;
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
        String userName = signMap.get(user);
        String password = signMap.get(pass);
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            throw new ServiceException(ResultCode.FAIL, "jdbcURL用户名或密码为空--" + jdbcUrl);
        }
        jdbcUrl = jdbcUrl.replace(password, "*");
        jdbcUrl = jdbcUrl.replace("|", "&");
        return new DBInfo(jdbcUrl, userName, password);
    }

}