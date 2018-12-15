package cn.ehai.log.elk;

import brave.internal.HexCodec;
import brave.opentracing.BraveSpanContext;
import brave.propagation.TraceContext;
import cn.ehai.common.core.*;
import cn.ehai.common.utils.HeaderUtils;
import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.common.utils.UuidUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * @Description:记录请求日志
 * @author:方典典
 * @time:2018/11/6 10:18
 */
@Component
@EnableConfigurationProperties(ProjectInfoProperties.class)
public class LoggingFilter extends OncePerRequestFilter {
    @Autowired
    private Tracer tracer;
    @Autowired
    private ProjectInfoProperties projectInfoProperties;
    private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String ATTRIBUTE_STOP_WATCH = LoggingFilter.class.getName()
            + ".StopWatch";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain
            filterChain) throws IOException {
        StopWatch stopWatch = this.createStopWatchIfNecessary(request);
        String requestTime = SIMPLE_FORMAT.format(new Date());
        ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrapperResponse = new ContentCachingResponseWrapper(response);
        String requestId = null;
        String errorMsg = "";
        int httpStatus = HttpCodeEnum.CODE_200.getCode();
        Map<String, String> headerMap = getRequestHeaderMap(request);
        try {
            filterChain.doFilter(wrapperRequest, wrapperResponse);
        } catch (Exception e) {
            if (e.getCause() != null) {
                e = (Exception) e.getCause();
            }
            errorMsg = ExceptionUtils.getStackTrace(e);
            LoggerUtils.error(getClass(), errorMsg);
            String exceptionMsg = "程序发生异常，错误代码:0X" + Long.toHexString(System.currentTimeMillis()).toUpperCase()
                    + (new Random().nextInt(900) + 100);
            wrapperResponse.setStatus(HttpCodeEnum.CODE_516.getCode());
            if (e instanceof ExternalException) {
                wrapperResponse.setStatus(HttpCodeEnum.CODE_518.getCode());
                String message = e.getMessage();
                int i = message.indexOf("---");
                if (i != -1) {
                    exceptionMsg = message.substring(i + 3);
                }
            }
            responseResult(wrapperResponse, ResultGenerator.genFailResult(ResultCode.INTERNAL_SERVER_ERROR,
                    exceptionMsg));
        } finally {
            boolean isClose = "none".equalsIgnoreCase(ApolloBaseConfig.getLogSwitch());
            boolean isBool = !"ALL".equalsIgnoreCase(ApolloBaseConfig.getLogSwitch()) && "GET".equalsIgnoreCase
                    (wrapperRequest.getMethod());
            if ((isClose || isBool) && StringUtils.isEmpty(errorMsg)) {
                wrapperResponse.copyBodyToResponse();
                return;
            }
            if (requestId == null) {
                requestId = UuidUtils.getRandomUUID();
            }
            String queryString = request.getQueryString();
            String requestUrl = request.getRequestURL().toString();
            if (!StringUtils.isEmpty(queryString)) {
                requestUrl = requestUrl + "?" + queryString;
            }
            JSON responseBody = getResponseBody(wrapperResponse);
            wrapperResponse.copyBodyToResponse();
            if (!wrapperRequest.isAsyncStarted()) {
                if (wrapperResponse.isCommitted()) {
                    httpStatus = wrapperResponse.getStatus();
                }
                stopWatch.stop();
                request.removeAttribute(ATTRIBUTE_STOP_WATCH);
            }
            String responseTime = SIMPLE_FORMAT.format(new Date());
            RequestLog requestLog = new RequestLog(requestId, requestTime, true,
                    projectInfoProperties.getBasePackage(), requestUrl, getRequestBody(wrapperRequest),
                    request.getMethod(), headerMap);
            Map<String, String> responseHeaderMap = HeaderUtils.responseHeaderHandler(response);
            responseHeaderMap.put("response.code", String.valueOf(httpStatus));
            ResponseLog responseLog = new ResponseLog(responseTime, httpStatus, errorMsg, stopWatch
                    .getTotalTimeMillis(), responseBody, responseHeaderMap);
            LOGGER.info(new EHILogstashMarker(requestLog, responseLog), null);
        }
    }

    private Map<String, String> getRequestHeaderMap(HttpServletRequest request) {
        Map<String, String> headerMap = HeaderUtils.requestHeaderHandler(request);
        Scope serverSpan = tracer.scopeManager().active();
        if (serverSpan != null) {
            SpanContext spanContext = serverSpan.span().context();
            if (spanContext instanceof BraveSpanContext) {
                TraceContext traceContext = ((BraveSpanContext) spanContext).unwrap();
                String traceId = HexCodec.toLowerHex(traceContext.traceId());
                String spanId = HexCodec.toLowerHex(traceContext.spanId());
                headerMap.put("parentId", "0");
                if (traceContext.parentId() != null) {
                    headerMap.put("parentId", HexCodec.toLowerHex(traceContext.parentId()));
                }
                headerMap.put("traceId", traceId);
                headerMap.put("spanId", spanId);
            }
        }
        return headerMap;
    }

    private StopWatch createStopWatchIfNecessary(HttpServletRequest request) {
        StopWatch stopWatch = (StopWatch) request.getAttribute(ATTRIBUTE_STOP_WATCH);
        if (stopWatch == null) {
            stopWatch = new StopWatch();
            stopWatch.start();
            request.setAttribute(ATTRIBUTE_STOP_WATCH, stopWatch);
        }
        return stopWatch;
    }

    /**
     * @param request
     * @return com.alibaba.fastjson.JSON
     * @Description:获取请求参数
     * @exception:
     * @author: 方典典
     * @time:2018/11/6 16:50
     */
    private JSON getRequestBody(ContentCachingRequestWrapper request) {
        if (isBinaryContent(request) || isMultipart(request)) {
            return JSON.parseObject("{\"content\":\"二进制\"}");
        }
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                return JSON.parseObject(new String(buf, 0, buf.length, "utf-8"));
            } catch (Exception e) {
                return JSON.parseObject("{\"unknown\":\"ExceptionName:" + e.getClass().getName() + " ContentType:" +
                        request.getContentType() + "\"}");
            }
        }
        return new JSONObject();
    }

    /**
     * @param response
     * @return com.alibaba.fastjson.JSON
     * @Description:获取响应Body
     * @exception:
     * @author: 方典典
     * @time:2018/11/6 16:50
     */
    private JSON getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        String bodyString = "";
        if (buf.length > 0) {
            try {
                bodyString = new String(buf, 0, buf.length, "utf-8");
                return JSON.parseObject(bodyString);
            } catch (Exception e) {
                return JSON.parseObject("{\"unknown\":\"ExceptionName:" + e.getClass().getName() + " ContentType:" +
                        response.getContentType() + "ResponseBody:" + bodyString + "\"}");
            }
        }
        return new JSONObject();
    }

    /**
     * @param request
     * @return boolean
     * @Description:检查是否为二进制
     * @exception:
     * @author: 方典典
     * @time:2018/11/6 16:50
     */
    private boolean isBinaryContent(final HttpServletRequest request) {
        if (request.getContentType() == null) {
            return false;
        }
        return request.getContentType().startsWith("image") || request.getContentType().startsWith("video") ||
                request.getContentType().startsWith("audio");
    }

    /**
     * @param request
     * @return boolean
     * @Description:检查是否问文件
     * @exception:
     * @author: 方典典
     * @time:2018/11/6 16:50
     */
    private boolean isMultipart(final HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().startsWith("multipart/form-data");
    }


    /**
     * @param response
     * @param result
     * @return void
     * @Description:响应
     * @exception:
     * @author: 方典典
     * @time:2018/11/6 16:51
     */
    private void responseResult(HttpServletResponse response, Result result) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(result));
    }

}
