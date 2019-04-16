package cn.ehai.db.utils;

import cn.ehai.common.utils.ProjectInfoUtils;
import com.github.pagehelper.PageHelper;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * 数据库分页插件设置
 *
 * @author lixiao
 * @date 2019-04-14 17:13
 */
public class SqlSessionFactoryUtils {

    /**
     * 生成 SqlSessionFactory
     * @param dataSource 数据源
     * @param mapperLocation mapper路径
     * @return org.apache.ibatis.session.SqlSessionFactory
     * @author lixiao
     * @date 2019-04-16 21:59
     */
    public static SqlSessionFactory create(DataSource dataSource,String mapperLocation) throws Exception {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage(ProjectInfoUtils.getBasePackage() + ".entity,cn.ehai.log.entity");
        factory.setConfigLocation(resolver.getResources("classpath*:mybatis/mybatis-config.xml")[0]);
        //添加插件
        SqlPlugins.INSTANCE.addSqlIntercepter(SqlSessionFactoryUtils.pageHelper());
        factory.setPlugins(SqlPlugins.INSTANCE.getSqlIntercepter().toArray(new Interceptor[SqlPlugins.INSTANCE.getSqlIntercepter().size()]));
        //添加XML目录
        factory.setMapperLocations(resolver.getResources(mapperLocation));
        return factory.getObject();
    }


    private  static PageHelper pageHelper(){
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
        return pageHelper;
    }
}
