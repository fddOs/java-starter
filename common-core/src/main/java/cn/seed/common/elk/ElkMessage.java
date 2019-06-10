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
    /**
     * 保留字段
     */
    private Object[] objs = new Object[]{};

    public ElkMessage(RequestLog requestLog, ResponseLog responseLog) {
        this.requestLog = requestLog;
        this.responseLog = responseLog;
    }

    public ElkMessage() {
    }

    public ElkMessage(ExceptionMsgWrapper errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ElkMessage(Object[] objs) {
        this.objs = objs;
    }

    public ElkMessage(RequestLog requestLog) {
        this.requestLog = requestLog;
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
}
