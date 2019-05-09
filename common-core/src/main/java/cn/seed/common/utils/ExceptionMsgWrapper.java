package cn.seed.common.utils;

/**
 * @Description:EHIExceptionMsg
 * @author:方典典
 * @time:2019/1/4 11:18
 */
public class ExceptionMsgWrapper {
    private String projectContext;
    private String className;
    private String methodName;
    private Object[] objs;
    private String exceptionMsg;

    public ExceptionMsgWrapper() {
    }

    public ExceptionMsgWrapper(String className, String methodName, Object[] objs, String exceptionMsg) {
        this.projectContext = ProjectInfoUtils.PROJECT_CONTEXT;
        this.className = className;
        this.methodName = methodName;
        this.objs = objs;
        this.exceptionMsg = exceptionMsg;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getObjs() {
        return objs;
    }

    public void setObjs(Object[] objs) {
        this.objs = objs;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

    public String getProjectContext() {
        return projectContext;
    }

    public void setProjectContext(String projectContext) {
        this.projectContext = projectContext;
    }
}
