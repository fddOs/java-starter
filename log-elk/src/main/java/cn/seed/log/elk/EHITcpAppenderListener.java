package cn.seed.log.elk;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import cn.seed.common.core.SpringContext;
import cn.seed.common.utils.LoggerUtils;
import cn.seed.email.domain.EmailKeyValue;
import cn.seed.email.service.EmailService;
import cn.seed.email.utils.EmailUtils;
import com.alibaba.fastjson.JSONObject;
import net.logstash.logback.appender.listener.TcpAppenderListener;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:EHITcpAppenderListener
 * @author:方典典
 * @time:2018/11/19 15:47
 */
public class EHITcpAppenderListener implements TcpAppenderListener {
    @Override
    public void eventSent(Appender appender, Socket socket, DeferredProcessingAware deferredProcessingAware, long
            durationInNanos) {

    }

    @Override
    public void eventSendFailure(Appender appender, DeferredProcessingAware deferredProcessingAware, Throwable reason) {
        LoggingEvent event;
        EHILogstashMarker marker = null;
        try {
            event = (LoggingEvent) deferredProcessingAware;
            marker = (EHILogstashMarker) event.getMarker();
        } catch (Exception e) {
            LoggerUtils.error(getClass(), new Object[]{appender, deferredProcessingAware, reason}, e);
        }
        EmailService emailService = SpringContext.getApplicationContext().getBean(EmailService.class);
        EmailKeyValue request = new EmailKeyValue("request", JSONObject.toJSONString(marker.getRequestLog()));
        EmailKeyValue response = new EmailKeyValue("response", JSONObject.toJSONString(marker.getResponseLog()));
        EmailKeyValue failReason = new EmailKeyValue("failReason", ExceptionUtils.getMessage(reason));
        List<EmailKeyValue> emailKeyValues = new ArrayList<>();
        emailKeyValues.add(request);
        emailKeyValues.add(response);
        emailKeyValues.add(failReason);
        emailService.send(EmailUtils.generateEmail(300053, "", emailKeyValues));
    }

    @Override
    public void connectionOpened(Appender appender, Socket socket) {
    }

    @Override
    public void connectionFailed(Appender appender, InetSocketAddress address, Throwable reason) {
    }

    @Override
    public void connectionClosed(Appender appender, Socket socket) {
    }

    @Override
    public void appenderStarted(Appender appender) {

    }

    @Override
    public void appenderStopped(Appender appender) {

    }

    @Override
    public void eventAppended(Appender appender, DeferredProcessingAware deferredProcessingAware, long
            durationInNanos) {

    }

    @Override
    public void eventAppendFailed(Appender appender, DeferredProcessingAware deferredProcessingAware, Throwable
            reason) {

    }
}
