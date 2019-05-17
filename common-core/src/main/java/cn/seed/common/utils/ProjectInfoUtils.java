package cn.seed.common.utils;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;

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
    public static final String PROJECT_APOLLO_COMMON_NAMESPACE;
    public static final String PROJECT_APOLLO_DB_NAMESPACE;
    public static final String PROJECT_APOLLO_DB_KEY;
    public static final int PROJECT_FEIGN_CONNECT_TIMEOUT_MILLIS;
    public static final int PROJECT_FEIGN_READ_TIMEOUT_MILLIS;

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
        PROJECT_APOLLO_COMMON_NAMESPACE = getProjectCommonNamespace();
        PROJECT_APOLLO_DB_NAMESPACE = getProjectDBNamespace();
        PROJECT_APOLLO_DB_KEY = getProjectDBKey();
        PROJECT_FEIGN_CONNECT_TIMEOUT_MILLIS = getProjectFeignConnectTimeoutMillis();
        PROJECT_FEIGN_READ_TIMEOUT_MILLIS = getProjectFeignReadTimeoutMillis();
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

    /**
     * 获取项目所在的公共配置 不配置默认为EHI.JavaCommon
     *
     * @return java.lang.String
     * @author 方典典
     * @time 2019/4/29 16:42
     */
    private static String getProjectCommonNamespace() {
        String commonNamespace = (String) ProjectInfoUtils.applicationProperty.getProperty("project.apollo" +
                ".common-namespace");
        if (StringUtils.isEmpty(commonNamespace)) {
            return "EHI.JavaCommon";
        }
        return commonNamespace;
    }

    /**
     * 获取项目所在的DB配置 不配置默认为EHI.DBConfig
     *
     * @return java.lang.String
     * @author 方典典
     * @time 2019/4/29 16:43
     */
    private static String getProjectDBNamespace() {
        String dbNamespace = (String) ProjectInfoUtils.applicationProperty.getProperty("project.apollo.db-namespace");
        if (StringUtils.isEmpty(dbNamespace)) {
            return "EHI.DBConfig";
        }
        return dbNamespace;
    }

    /**
     * 获取项目数据库key
     *
     * @return java.lang.String
     * @author 方典典
     * @time 2019/4/29 17:20
     */
    private static String getProjectDBKey() {
        return (String) ProjectInfoUtils.applicationProperty.getProperty("project.apollo.db-key");
    }

    /**
     * 获取是否启动归档数据库
     *
     * @return boolean
     * @author 方典典
     * @time 2019/4/29 17:20
     */
    private static boolean getProjectArchiveEnabled() {
        Object obj = ProjectInfoUtils.applicationProperty.getProperty("project.apollo.archive.enabled");
        if (StringUtils.isEmpty(obj)) {
            return false;
        }
        return (boolean) obj;
    }

    /**
     * feign连接超时
     *
     * @return int
     * @author 方典典
     * @time 2019/4/30 10:16
     */
    private static int getProjectFeignConnectTimeoutMillis() {
        Object connectTimeoutMillis = ProjectInfoUtils.applicationProperty.getProperty("project.feign" +
                ".connect-timeout-millis");
        if (StringUtils.isEmpty(connectTimeoutMillis)) {
            return 20000;
        }
        return (int) connectTimeoutMillis;
    }

    /**
     * feign读取超时
     *
     * @return int
     * @author 方典典
     * @time 2019/4/30 10:17
     */
    private static int getProjectFeignReadTimeoutMillis() {
        Object readTimeoutMillis = ProjectInfoUtils.applicationProperty.getProperty("project.feign" +
                ".read-timeout-millis");
        if (StringUtils.isEmpty(readTimeoutMillis)) {
            return 20000;
        }
        return (int) readTimeoutMillis;
    }

}