package cn.seed.common.utils;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Description:工具类
 * @author:方典典
 * @time:2018/11/7 10:35
 */
public class ProjectInfoUtils {
    public static PropertySource applicationProperty;
    private static final String CLASS_LOADER_NAME = "java.lang.ClassLoader";
    public static final String BASE_PACKAGE;
    public static final String DEFAULT_BASE_PACKAGE_PREFIX = "cn.ehai";
    public static final String PROJECT_CONTEXT;

    static {
        YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
        try {
            applicationProperty = yamlPropertySourceLoader.load("application.yml", new
                    PathMatchingResourcePatternResolver().getResource("classpath:application.yml"), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BASE_PACKAGE = getBasePackage();
        PROJECT_CONTEXT = getProjectContext();
    }

    /**
     * 获取包名前缀
     *
     * @param
     * @return java.lang.String
     * @author 方典典
     * @time 2019/4/22 15:58
     */
    private static String getBasePackagePrefix() {
        String basePackagePrefix = (String) applicationProperty.getProperty("project.base-package-prefix");
        if (StringUtils.isEmpty(basePackagePrefix)) {
            return DEFAULT_BASE_PACKAGE_PREFIX;
        }
        return basePackagePrefix;
    }

    /**
     * @param
     * @return java.lang.String
     * @Description:获取项目basepakeage
     * @exception:
     * @author: 方典典
     * @time:2018/11/9 16:45
     */
    private static String getBasePackage() {
        ClassLoader classLoader = ProjectInfoUtils.class.getClassLoader();
        Class loadClass = classLoader.getClass();
        while (!CLASS_LOADER_NAME.equals(loadClass.getName())) {
            loadClass = loadClass.getSuperclass();
        }
        Package[] packages;
        try {
            Method getPackages = loadClass.getDeclaredMethod("getPackages");
            getPackages.setAccessible(true);
            packages = (Package[]) getPackages.invoke(classLoader);
        } catch (Exception e) {
            throw new RuntimeException("获取项目包名失败");
        }
        for (Package p : packages) {
            if (p.getName().startsWith(getBasePackagePrefix())) {
                String basePackage = p.getName();
                int lastChar = basePackage.indexOf(".", 8);
                if (lastChar < 0) {
                    return basePackage;
                }
                return basePackage.substring(0, basePackage.indexOf(".", 8));
            }
        }
        throw new RuntimeException("获取项目包名失败");
    }

    /**
     * 获取项目名称
     *
     * @param
     * @return java.lang.String
     * @author 方典典
     * @time 2019/4/22 15:59
     */
    private static String getProjectContext() {
        try {
            String path = ProjectInfoUtils.class.getResource("/").getPath();
            String[] projectURL = path.split("/");
            return projectURL[projectURL.length - 3];
        } catch (Exception e) {
            throw new RuntimeException("获取项目名称失败");
        }
    }

}
