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

    public SeedLogstashMarker(RequestLog requestLog, ResponseLog responseLog, String projectContext) {
        super("ELK");
        elkMessage = new ElkMessage(requestLog, responseLog, projectContext);
    }

    public SeedLogstashMarker(ExceptionMsgWrapper exceptionMsgWrapper, String projectContext) {
        super("ELK");
        this.elkMessage = new ElkMessage(exceptionMsgWrapper, projectContext);
    }

    public SeedLogstashMarker(Object[] objs, String projectContext) {
        super("ELK");
        this.elkMessage = new ElkMessage(objs, projectContext);
    }

    public SeedLogstashMarker(String projectContext) {
        super("ELK");
        this.elkMessage = new ElkMessage(projectContext);
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
