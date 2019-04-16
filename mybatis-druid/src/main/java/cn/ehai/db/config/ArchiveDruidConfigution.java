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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * TODO
 *
 * @author lixiao
 * @date 2019-04-14 16:40
 */
@Configuration
@MapperScan(basePackages = "cn.ehai.**.dao.archive" ,sqlSessionTemplateRef = "archiveSqlSessionTemplate")
@ConditionalOnProperty(value = "db.archive-enabled", havingValue = "true")
public class ArchiveDruidConfigution {

    private List<String> initSql = Arrays.asList("set names utf8mb4;");
    private DruidDataSource dataSource;
    private String archiveLocation = "classpath*:mybatis/mapper/archive/*.xml";

    @Bean("archiveDataSource")
    public DataSource archiveDataSource(@Qualifier("archiveDB")DBInfo archiveDBInfo) {
        dataSource= DruidUtils.getDataSource(archiveDBInfo);
        return dataSource;
    }
    @Bean(name = "archiveTransactionManager")
    public DataSourceTransactionManager archiveTransactionManager(@Qualifier("archiveDataSource")
        DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    @Bean(name = "archiveSqlSessionTemplate")
    public SqlSessionTemplate masterSqlSessionTemplate(@Qualifier("archiveSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


    @Bean(name = "archiveSqlSessionFactory")
    public SqlSessionFactory archiveSqlSessionFactory(@Qualifier("archiveDataSource") DataSource dataSource) throws Exception {
        return SqlSessionFactoryUtils.create(dataSource,archiveLocation);
    }

    /**
     * 重新初始化数据库连接池
     *
     * @param dbInfo
     *            1433;DatabaseName=
     */
    public void resetArchiveDataBase(DBInfo dbInfo) {
        ServiceExpUtils.notNull(dataSource,dbInfo.getUrl()+"数据源错误");
        DruidUtils.restart(dataSource,dbInfo,initSql);
    }


}
