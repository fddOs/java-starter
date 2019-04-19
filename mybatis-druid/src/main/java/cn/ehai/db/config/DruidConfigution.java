package cn.ehai.db.config;

import cn.ehai.common.core.ServiceExpUtils;
import cn.ehai.db.utils.DruidUtils;
import cn.ehai.db.utils.SqlSessionFactoryUtils;
import com.alibaba.druid.pool.DruidDataSource;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * druid 数据连接池配置
 * 
 * @author 18834
 *
 */
@Configuration
@MapperScan(basePackages = "cn.ehai.**.dao",sqlSessionTemplateRef = "masterSqlSessionTemplate")
public class DruidConfigution {
	private List<String> initSql = Arrays.asList("set names utf8mb4;");
	private String masterLocation = "classpath*:mybatis/mapper/**/*.xml";

	private DruidDataSource dataSource;

	@Bean("masterDataSource")
	@Primary
	public DataSource masterDataSource(@Qualifier("masterDB") DBInfo dbInfo) {
		dataSource = DruidUtils.getDataSource(dbInfo);
		return dataSource;
	}

	@Bean(name = "masterTransactionManager")
	@Primary
	public DataSourceTransactionManager masterTransactionManager(@Qualifier("masterDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	@Bean(name = "masterSqlSessionTemplate")
	@Primary
	public SqlSessionTemplate masterSqlSessionTemplate(@Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@Bean(name = "masterSqlSessionFactory")
	@Primary
	public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource dataSource) throws Exception {

		return SqlSessionFactoryUtils.create(dataSource,masterLocation);
	}




	/**
	 * 重新初始化数据库连接池
	 * 
	 * @param dbInfo
	 *            1433;DatabaseName=
	 */
	public void resetDataBase(DBInfo dbInfo) {
		ServiceExpUtils.notNull(dataSource,dbInfo.getUrl()+"数据源错误");
		DruidUtils.restart(dataSource,dbInfo,initSql);
	}




}
