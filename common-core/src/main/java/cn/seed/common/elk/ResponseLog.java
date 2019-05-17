package cn.seed.common.elk;

import java.util.Map;

/**
 * @Description:ResponseLog
 * @author:方典典
 * @time:2018/11/16 10:31
 */
public class ResponseLog {
    /**
     * 响应时间
     */
    private String responseTime;
    /**
     * Http状态码
     */
    private Integer httpStatus;
    /**
     * 异常信息
     */
    private String error;
    /**
     * 接口耗时
     */
    private Long timeTaken;
    /**
     * result
     */
    private Object responseBody;

    private Map<String, String> responseHeader;

    public ResponseLog() {
    }

    public ResponseLog(String responseTime, Integer httpStatus, String error, Long timeTaken, Object responseBody,
                       Map<String,String> responseHeader) {
        this.responseTime = responseTime;
        this.httpStatus = httpStatus;
        this.error = error;
        this.timeTaken = timeTaken;
        this.responseBody = responseBody;
        this.responseHeader = responseHeader;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeaders(Map<String, String> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public Long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
    }
}
