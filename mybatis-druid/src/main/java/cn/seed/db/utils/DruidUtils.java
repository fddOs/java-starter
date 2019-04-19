package cn.seed.db.utils;

import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.db.config.DBInfo;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import java.sql.SQLException;
import java.util.List;

/**
 * druid操作工具
 *
 * @author lixiao
 * @date 2019-04-14 17:06
 */
public class DruidUtils {

    /**
     * 创建数库库连接对象
     * @param dbInfo
     * @return com.alibaba.druid.pool.DruidDataSource
     * @author lixiao
     * @date 2019-04-14 17:07
     */
    public static  DruidDataSource getDataSource(DBInfo dbInfo) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(dbInfo.getUrl());
        dataSource.setUsername(dbInfo.getUserName());
        dataSource.setPassword(dbInfo.getPassword());
        dataSource.configFromPropety(System.getProperties());
        return dataSource;
    }

    /***
     * reset数据库
     * @param dataSource
     * @param dbInfo
     * @param initSql
     * @return void
     * @author lixiao
     * @date 2019-04-14 17:08
     */
    public static void restart (DruidDataSource dataSource,DBInfo dbInfo, List<String> initSql) {
        try {
            dataSource.restart();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl(dbInfo.getUrl());
            dataSource.setUsername(dbInfo.getUserName());
            dataSource.setPassword(dbInfo.getPassword());
            dataSource.configFromPropety(System.getProperties());
            dataSource.setConnectionInitSqls(initSql);
            dataSource.init();
        } catch (SQLException e) {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, dbInfo.getUrl()+"数据库重启失败！");
        }
    }
}
