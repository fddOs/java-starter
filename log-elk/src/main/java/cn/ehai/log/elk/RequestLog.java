package cn.ehai.log.elk;

import java.util.Map;

/**
 * @Description:RequestLog
 * @author:方典典
 * @time:2018/11/16 10:29
 */
public class RequestLog {
    /**
     * 请求唯一标记
     */
    private String requestId;
    /**
     * 请求时间
     */
    private String requestTime;

    /**
     * 入口
     */
    private Boolean isReceivedRequest;
    /**
     * 项目名称
     */
    private String projectContext;
    /**
     * 请求URL
     */
    private String requestUrl;
    /**
     * 请求体参数
     */
    private Object requestBody;

    private String requestMethod;

    private Map<String, String> requestHeader;

    public RequestLog() {
    }

    public RequestLog(String requestId, String requestTime, Boolean isReceivedRequest, String projectContext, String
            requestUrl, Object requestBody, String requestMethod, Map<String, String> requestHeader) {
        this.requestId = requestId;
        this.requestTime = requestTime;
        this.isReceivedRequest = isReceivedRequest;
        this.projectContext = projectContext;
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.requestBody = requestBody;
        this.requestHeader = requestHeader;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public Boolean getIsReceivedRequest() {
        return isReceivedRequest;
    }

    public void setIsReceivedRequest(Boolean receivedRequest) {
        isReceivedRequest = receivedRequest;
    }

    public String getProjectContext() {
        return projectContext;
    }

    public void setProjectContext(String projectContext) {
        this.projectContext = projectContext;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }
}
