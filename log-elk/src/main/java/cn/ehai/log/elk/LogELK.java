/**
 *
 */
package cn.ehai.log.elk;

import java.util.Map;

/**
 * @Description:对接ELK日志系统实体类
 * @author:方典典
 * @time:2017年12月29日 下午2:57:42
 */
public class LogELK {
    /**
     * 请求唯一标记
     */
    private String requestId;
    /**
     * 请求时间
     */
    private String requestTime;
    /**
     * 响应时间
     */
    private String responseTime;
    /**
     * 接口耗时
     */
    private Long timeTaken;

    /**
     * 入口
     */
    private Boolean isReceivedRequest;
    /**
     * 项目名称
     */
    private String projectContext;
    /**
     * Http状态码
     */
    private Integer httpStatus;
    /**
     * 请求URL
     */
    private String url;
    /**
     * 异常信息
     */
    private String error;
    /**
     * 请求体参数
     */
    private Object requestBody;
    /**
     * result
     */
    private Object responseBody;

    /**
     * Headers
     */
    private Map<String, String> headers;

    public LogELK() {
        super();
    }

    public LogELK(String requestId, String requestTime, String responseTime, Long timeTaken, Boolean isReceivedRequest, String
            projectContext, Integer httpStatus, String url, String error, Object requestBody, Object responseBody,
                  Map<String, String> headers) {
        this.requestId = requestId;
        this.requestTime = requestTime;
        this.responseTime = responseTime;
        this.timeTaken = timeTaken;
        this.isReceivedRequest = isReceivedRequest;
        this.projectContext = projectContext;
        this.httpStatus = httpStatus;
        this.url = url;
        this.error = error;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.headers = headers;
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

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public Long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Boolean getReceivedRequest() {
        return isReceivedRequest;
    }

    public void setReceivedRequest(Boolean receivedRequest) {
        isReceivedRequest = receivedRequest;
    }

    public String getProjectContext() {
        return projectContext;
    }

    public void setProjectContext(String projectContext) {
        this.projectContext = projectContext;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
