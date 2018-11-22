package cn.ehai.log.elk;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import net.logstash.logback.appender.listener.TcpAppenderListener;

import java.net.InetSocketAddress;
import java.net.Socket;

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
        LoggingEvent event = (LoggingEvent) deferredProcessingAware;
        EHILogstashMarker marker = (EHILogstashMarker)event.getMarker();
        // TODO
    }

    @Override
    public void connectionOpened(Appender appender, Socket socket) {
    }

    @Override
    public void connectionFailed(Appender appender, InetSocketAddress address, Throwable reason) {
        // TODO
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
