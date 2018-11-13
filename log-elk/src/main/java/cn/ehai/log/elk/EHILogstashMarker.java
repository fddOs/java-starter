package cn.ehai.log.elk;

import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.marker.LogstashMarker;

import java.io.IOException;

/**
 * @Description:LogstashMarker
 * @author:方典典
 * @time:2018/11/2 10:50
 */
public class EHILogstashMarker extends LogstashMarker {
    private LogELK logELK;

    public EHILogstashMarker(LogELK logELK) {
        super("ELK");
        this.logELK = logELK;
    }

    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeFieldName("json_message");
        generator.writeObject(logELK);
    }

}
