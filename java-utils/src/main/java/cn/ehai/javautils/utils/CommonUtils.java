package cn.ehai.javautils.utils;

/**
 * @Description:工具类
 * @author:方典典
 * @time:2018/11/7 10:35
 */
public class CommonUtils {
    /**
     * @param
     * @return javautils.lang.String
     * @Description:获取项目名称
     * @exception:
     * @author: 方典典
     * @time:2018/11/7 10:36
     */
    public static String getProjectContext() {
        try {
            String path = CommonUtils.class.getClassLoader().getResource("").getPath();
            String[] projectURL = path.split("/");
            return projectURL[projectURL.length - 3];
        } catch (Exception e) {
            return "unknow-project";
        }
    }

    public static String getProjectPackage() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        String className = elements[elements.length - 1].getClassName();
        return className.substring(0,className.indexOf(".",8));
    }
}
