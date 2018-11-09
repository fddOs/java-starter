package cn.ehai.javautils.utils;

/**
 * @Description:工具类
 * @author:方典典
 * @time:2018/11/7 10:35
 */
public class ProjectInfoUtils {
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
            String path = Class.forName(getStackTopClassName()).getResource("/").getPath();
            String[] projectURL = path.split("/");
            return projectURL[projectURL.length - 3];
        } catch (Exception e) {
            return "unknow-project";
        }
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
        return elements[elements.length - 1].getClassName();
    }
}
