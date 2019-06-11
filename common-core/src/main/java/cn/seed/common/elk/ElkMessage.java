package cn.seed.common.elk;

/**
 * @Description:ElkMessage
 * @author:方典典
 * @time:2019/6/5 10:32
 */
public class ElkMessage {
    private RequestLog requestLog = new RequestLog();
    private ResponseLog responseLog = new ResponseLog();
    private ExceptionMsgWrapper errorMsg = new ExceptionMsgWrapper();
    private String projectContext;
    /**
     * 保留字段
     */
    private Object[] objs = new Object[]{};

    public ElkMessage(RequestLog requestLog, ResponseLog responseLog, String projectContext) {
        this.requestLog = requestLog;
        this.responseLog = responseLog;
        this.projectContext = projectContext;
    }

    public ElkMessage(String projectContext) {
        this.projectContext = projectContext;
    }

    public ElkMessage(ExceptionMsgWrapper errorMsg, String projectContext) {
        this.errorMsg = errorMsg;
        this.projectContext = projectContext;
    }

    public ElkMessage(Object[] objs, String projectContext) {
        this.objs = objs;
        this.projectContext = projectContext;
    }

    public RequestLog getRequestLog() {
        return requestLog;
    }

    public void setRequestLog(RequestLog requestLog) {
        this.requestLog = requestLog;
    }

    public ResponseLog getResponseLog() {
        return responseLog;
    }

    public void setResponseLog(ResponseLog responseLog) {
        this.responseLog = responseLog;
    }

    public ExceptionMsgWrapper getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(ExceptionMsgWrapper errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object[] getObjs() {
        return objs;
    }

    public void setObjs(Object[] objs) {
        this.objs = objs;
    }

    public String getProjectContext() {
        return projectContext;
    }

    public void setProjectContext(String projectContext) {
        this.projectContext = projectContext;
    }
}
