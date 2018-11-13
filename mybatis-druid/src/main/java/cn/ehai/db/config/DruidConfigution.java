package cn.ehai.db.config;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ServiceException;
import cn.ehai.common.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

/**
 * druid 数据连接池配置
 * 
 * @author 18834
 *
 */
@Configuration
public class DruidConfigution {
	// 指向druid的连接池，作为切换数据库使用，暂不支持多数据源
	private DruidDataSource dataSource;
	@Autowired
	private DBInfo dbInfo;
	private List<String> initSql = Arrays.asList("set names utf8mb4;");

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.druid")
	public DataSource getDataSource() {
		dataSource = DruidDataSourceBuilder.create().build();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl(dbInfo.getDbConfigUrl());
		dataSource.setUsername(dbInfo.getDbConfigName());
		dataSource.setPassword(dbInfo.getDbConfigKey());
		return dataSource;
	}

	/**
	 * 重新初始化数据库连接池
	 * 
	 * @param dataDruidSource
	 * @param dbInfo
	 *            1433;DatabaseName=
	 */
	public void resetDataBase(DBInfo dbInfo) {
		try {
			dataSource.restart();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUrl(dbInfo.getDbConfigUrl());
			dataSource.setUsername(dbInfo.getDbConfigName());
			dataSource.setPassword(dbInfo.getDbConfigKey());
			dataSource.configFromPropety(System.getProperties());
			dataSource.setConnectionInitSqls(initSql);
			dataSource.init();
		} catch (SQLException e) {
			LoggerUtils.error(getClass(), "重置数据库链接失败", e);
			throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "获取数据信息失败！");
		}
	}

}
