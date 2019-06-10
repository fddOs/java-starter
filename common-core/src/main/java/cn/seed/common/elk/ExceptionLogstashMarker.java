//package cn.seed.common.elk;
//
//import cn.seed.common.elk.ExceptionMsgWrapper;
//import com.fasterxml.jackson.core.JsonGenerator;
//import net.logstash.logback.marker.LogstashMarker;
//
//import java.io.IOException;
//
///**
// * @Description:LogstashMarker
// * @author:方典典
// * @time:2018/11/2 10:50
// */
//public class ExceptionLogstashMarker extends LogstashMarker {
//    private ExceptionMsgWrapper exceptionMsgWrapper;
//
//    public ExceptionLogstashMarker(ExceptionMsgWrapper exceptionMsgWrapper) {
//        super("ELK");
//        this.exceptionMsgWrapper = exceptionMsgWrapper;
//    }
//
//    @Override
//    public void writeTo(JsonGenerator generator) throws IOException {
//        generator.writeFieldName("exceptionMsg");
//        generator.writeObject(exceptionMsgWrapper);
//    }
//
//    public ExceptionMsgWrapper getExceptionMsgWrapper() {
//        return exceptionMsgWrapper;
//    }
//
//    public void setExceptionMsgWrapper(ExceptionMsgWrapper exceptionMsgWrapper) {
//        this.exceptionMsgWrapper = exceptionMsgWrapper;
//    }
//
//}