package cn.seed.common.elk;

import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.marker.LogstashMarker;

import java.io.IOException;

/**
 * @Description:LogstashMarker
 * @author:方典典
 * @time:2018/11/2 10:50
 */
public class SeedLogstashMarker extends LogstashMarker {
    private ElkMessage elkMessage;

    public SeedLogstashMarker(RequestLog requestLog, ResponseLog responseLog) {
        super("ELK");
        elkMessage = new ElkMessage(requestLog, responseLog);
    }

    public SeedLogstashMarker(ExceptionMsgWrapper exceptionMsgWrapper) {
        super("ELK");
        this.elkMessage = new ElkMessage(exceptionMsgWrapper);
    }

    public SeedLogstashMarker(Object[] objs) {
        super("ELK");
        this.elkMessage = new ElkMessage(objs);
    }

    public SeedLogstashMarker() {
        super("ELK");
        this.elkMessage = new ElkMessage();
    }

    public SeedLogstashMarker(ElkMessage elkMessage) {
        super("ELK");
        this.elkMessage = elkMessage;
    }

    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeFieldName("elkMessage");
        generator.writeObject(elkMessage);
    }

    public ElkMessage getElkMessage() {
        return elkMessage;
    }

    public void setElkMessage(ElkMessage elkMessage) {
        this.elkMessage = elkMessage;
    }
}
