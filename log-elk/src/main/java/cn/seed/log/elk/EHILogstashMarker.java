package cn.seed.log.elk;

import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.marker.LogstashMarker;

import java.io.IOException;

/**
 * @Description:LogstashMarker
 * @author:方典典
 * @time:2018/11/2 10:50
 */
public class EHILogstashMarker extends LogstashMarker {
    private RequestLog requestLog;
    private ResponseLog responseLog;

    public EHILogstashMarker(RequestLog requestLog, ResponseLog responseLog) {
        super("ELK");
        this.requestLog = requestLog;
        this.responseLog = responseLog;
    }

    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeFieldName("request");
        generator.writeObject(requestLog);
        generator.writeFieldName("response");
        generator.writeObject(responseLog);
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
}
