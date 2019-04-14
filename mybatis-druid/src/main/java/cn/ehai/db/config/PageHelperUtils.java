package cn.ehai.db.config;

import com.github.pagehelper.PageHelper;
import java.util.Properties;

/**
 * 数据库分页插件设置
 *
 * @author lixiao
 * @date 2019-04-14 17:13
 */
public class PageHelperUtils {

    public static PageHelper pageHelper(){
        //配置分页插件，详情请查阅官方文档
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        //分页尺寸为0时查询所有纪录不再执行分页
        properties.setProperty("pageSizeZero", "true");
        //页码<=0 查询第一页，页码>=总页数查询最后一页
        properties.setProperty("reasonable", "true");
        //支持通过 Mapper 接口参数来传递分页参数
        properties.setProperty("supportMethodsArguments", "true");
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}
