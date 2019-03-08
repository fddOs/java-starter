package cn.ehai.common.utils;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

/**
 * @Description:工具类
 * @author:方典典
 * @time:2018/11/7 10:35
 */
public class ProjectInfoUtils {
    private static Map map;

    static {
        Yaml yaml = new Yaml();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            map = yaml.loadAs(resolver.getResource("classpath:application.yml").getInputStream(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBasePackage() {
        String basePackage = (String) ((Map) map.get("project")).get("base-package");
        if(StringUtils.isEmpty(basePackage)){
            throw new RuntimeException("缺少配置：project.base-package");
        }
        return basePackage;
    }

    public static String getProjectContext() {
        String projectContext = (String) ((Map) map.get("project")).get("context");
        if(StringUtils.isEmpty(projectContext)){
            throw new RuntimeException("缺少配置：project.context");
        }
        return projectContext;
    }

    /**
     * @param
     * @return java.lang.String
     * @Description:获取项目basepakeage
     * @exception:
     * @author: 方典典
     * @time:2018/11/9 16:45
     */
    public static String getProjectPackage() {
        String className = getStackTopClassName();
        return className.substring(0, className.indexOf(".", 8));
    }

    /**
     * @param
     * @return java.lang.String
     * @Description:获取栈顶类名
     * @exception:
     * @author: 方典典
     * @time:2018/11/9 9:50
     */
    public static String getStackTopClassName() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = elements.length - 1; i >= 0; i--) {
            String className = elements[i].getClassName();
            if (className.startsWith("cn.ehai")) {
                return className;
            }
        }
        throw new RuntimeException("获取栈顶类名失败，请联系管理员");
    }
}
