package cn.ehai.db.config;

import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ServiceException;
import cn.ehai.common.core.ServiceExpUtils;
import cn.ehai.common.utils.ProjectInfoUtils;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.github.pagehelper.PageHelper;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
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

    private final String ARCHIVE_DB = "archive";
    private List<String> initSql = Arrays.asList("set names utf8mb4;");
    private DruidDataSource dataSource;

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
    public SqlSessionTemplate masterSqlSessionTemplate(@Qualifier("archiveSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }


    @Bean(name = "archiveSqlSessionFactory")
    public SqlSessionFactory archiveSqlSessionFactory(@Qualifier("archiveDataSource") DataSource dataSource) throws Exception {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage(ProjectInfoUtils.getBasePackage() + ".entity");
        factory.setConfigLocation(resolver.getResources("classpath*:mybatis/mybatis-config.xml")[0]);
        //添加插件
        factory.setPlugins(new Interceptor[]{PageHelperUtils.pageHelper()});
        //添加XML目录
        factory.setMapperLocations(resolver.getResources("classpath*:mybatis/mapper/archive/*.xml"));
        return factory.getObject();
    }

    /**
     * 重新初始化数据库连接池
     *
     * @param dbInfo
     *            1433;DatabaseName=
     */
    public void resetArchiveDataBase(DBInfo dbInfo) {
        ServiceExpUtils.notNull(dataSource,ARCHIVE_DB+"数据源错误");
        DruidUtils.restart(dataSource,dbInfo,initSql);
    }


}
