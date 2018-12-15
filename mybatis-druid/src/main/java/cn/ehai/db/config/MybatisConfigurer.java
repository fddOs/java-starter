package cn.ehai.db.config;

import cn.ehai.common.core.ProjectInfoProperties;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Mybatis & Mapper & PageHelper 配置
 */
@Configuration
@EnableConfigurationProperties(ProjectInfoProperties.class)
public class MybatisConfigurer {

    @Autowired
    private ProjectInfoProperties projectInfoProperties;

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage(projectInfoProperties.getBasePackage() + ".entity,cn.ehai.log.entity");

        //配置分页插件，详情请查阅官方文档
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        //分页尺寸为0时查询所有纪录不再执行分页
        properties.setProperty("pageSizeZero", "true");
        //页码<=0 查询第一页，页码>=总页数查询最后一页
        properties.setProperty("reasonable", "false");
        //支持通过 Mapper 接口参数来传递分页参数
        properties.setProperty("supportMethodsArguments", "true");
        pageHelper.setProperties(properties);
        //添加插件
        factory.setPlugins(new Interceptor[]{pageHelper});
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String logPath = "jar:" + this.getClass().getResource("").getPath();
        logPath = logPath.substring(0, logPath.indexOf("jar!/") + 5);
        factory.setMapperLocations(ArrayUtils.addAll(resolver.getResources(logPath + "mybatis/mapper/*.xml"),
                resolver.getResources("classpath:mybatis/mapper/*.xml")));
        return factory.getObject();
    }

//    @Bean
//    public MapperScannerConfigurer mapperScannerConfigurer() {
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
////        LoggerUtils.info(getClass(), "base Package:" + projectInfoProperties.getBasePackage());
////        mapperScannerConfigurer.setBasePackage(projectInfoProperties.getBasePackage() + ".dao,cn.ehai.log.dao");
//        return mapperScannerConfigurer;
//    }

}

