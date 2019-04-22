package cn.seed.common.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.marker.LogstashMarker;

import java.io.IOException;

/**
 * @Description:LogstashMarker
 * @author:方典典
 * @time:2018/11/2 10:50
 */
public class EHIExceptionLogstashMarker extends LogstashMarker {
    private EHIExceptionMsgWrapper ehiExceptionMsgWrapper;

    public EHIExceptionLogstashMarker(EHIExceptionMsgWrapper ehiExceptionMsgWrapper) {
        super("ELK");
        this.ehiExceptionMsgWrapper = ehiExceptionMsgWrapper;
    }

    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        generator.writeFieldName("exceptionMsg");
        generator.writeObject(ehiExceptionMsgWrapper);
    }

    public EHIExceptionMsgWrapper getEhiExceptionMsgWrapper() {
        return ehiExceptionMsgWrapper;
    }

    public void setEhiExceptionMsgWrapper(EHIExceptionMsgWrapper ehiExceptionMsgWrapper) {
        this.ehiExceptionMsgWrapper = ehiExceptionMsgWrapper;
    }

}