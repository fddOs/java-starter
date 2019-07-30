package cn.seed.web.config;

import brave.internal.HexCodec;
import brave.opentracing.BraveSpanContext;
import brave.propagation.TraceContext;
import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.ExternalException;
import cn.seed.common.core.HttpCodeEnum;
import cn.seed.common.core.Result;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ResultGenerator;
import cn.seed.common.core.ServiceException;
import cn.seed.common.elk.SeedLogstashMarker;
import cn.seed.common.elk.RequestLog;
import cn.seed.common.elk.ResponseLog;
import cn.seed.common.utils.*;
import com.alibaba.fastjson.JSON;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import cn.seed.web.config.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

/**
 * @Description:记录请求日志
 * @author:方典典
 * @time:2018/11/6 10:18
 */
@Order(Integer.MIN_VALUE + 1)
@Component
public class LoggingFilter extends OncePerRequestFilter {
    @Autowired
    private Tracer tracer;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String ATTRIBUTE_STOP_WATCH = LoggingFilter.class.getName()
            + ".StopWatch";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain
            filterChain) throws IOException {
        StopWatch stopWatch = this.createStopWatchIfNecessary(request);
        String requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String requestId = null;
        String errorMsg = "";
        int httpStatus = HttpCodeEnum.CODE_200.getCode();
        Map<String, String> headerMap = getRequestHeaderMap(request);
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            if (e.getCause() != null) {
                e = (Exception) e.getCause();
            }
            errorMsg = ExceptionUtils.getStackTrace(e);
            LoggerUtils.error(getClass(), errorMsg);
            if (e instanceof ServiceException) {
                responseResult(response, ResultGenerator.genFailResult(((ServiceException) e).getResultCode(),
                        e.getMessage()));
                return;
            }
            String exceptionMsg = "程序发生异常，错误代码:0X" + Long.toHexString(System.currentTimeMillis()).toUpperCase()
                    + (new Random().nextInt(900) + 100);
            response.setStatus(HttpCodeEnum.CODE_516.getCode());
            if (e instanceof ExternalException) {
                response.setStatus(HttpCodeEnum.CODE_518.getCode());
                String message = e.getMessage();
                int i = message.indexOf("---");
                if (i != -1) {
                    exceptionMsg = message.substring(i + 3);
                }
            }
            responseResult(response, ResultGenerator.genFailResult(ResultCode.INTERNAL_SERVER_ERROR,
                    exceptionMsg));
        } finally {
            boolean isClose = "none".equalsIgnoreCase(ApolloBaseConfig.getLogSwitch()) || (!"ALL".equalsIgnoreCase
                    (ApolloBaseConfig.getLogSwitch()) && "GET".equalsIgnoreCase(request.getMethod()));
            if (!isClose || StringUtils.isEmpty(errorMsg)) {
                if (requestId == null) {
                    requestId = UuidUtils.getRandomUUID();
                }
                String queryString = request.getQueryString();
                String requestUrl = request.getRequestURL().toString();
                if (!StringUtils.isEmpty(queryString)) {
                    requestUrl = requestUrl + "?" + queryString;
                }
                Object responseBody = getResponseBody(response);
                if (!request.isAsyncStarted()) {
                    if (response.isCommitted()) {
                        httpStatus = response.getStatus();
                    }
                    stopWatch.stop();
                    request.removeAttribute(ATTRIBUTE_STOP_WATCH);
                }
                String responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                RequestLog requestLog = new RequestLog(requestId, requestTime, true, requestUrl, getRequestBody
                        (request), request.getMethod(), headerMap);
                Map<String, String> responseHeaderMap = RequestInfoUtils.responseHeaderHandler(response);
                responseHeaderMap.put("response.code", String.valueOf(httpStatus));
                ResponseLog responseLog = new ResponseLog(responseTime, httpStatus, errorMsg, stopWatch
                        .getTotalTimeMillis(), responseBody, responseHeaderMap);
                LOGGER.info(new SeedLogstashMarker(requestLog, responseLog, ProjectInfoUtils.PROJECT_CONTEXT), null);
            }
        }
    }

    /**
     * getRequestHeaderMap
     *
     * @param request
     * @return java.util.Map
     * @author 方典典
     * @time 2019/1/15 17:44
     */
    private Map<String, String> getRequestHeaderMap(HttpServletRequest request) {
        Map<String, String> headerMap = RequestInfoUtils.requestHeaderHandler(request);
        Scope serverSpan = tracer.scopeManager().active();
        if (serverSpan != null) {
            SpanContext spanContext = serverSpan.span().context();
            if (spanContext instanceof BraveSpanContext) {
                TraceContext traceContext = ((BraveSpanContext) spanContext).unwrap();
                String traceId = HexCodec.toLowerHex(traceContext.traceId());
                String spanId = HexCodec.toLowerHex(traceContext.spanId());
                headerMap.put("x-b3-parentspanid", "0");
                if (traceContext.parentId() != null) {
                    headerMap.put("x-b3-parentspanid", HexCodec.toLowerHex(traceContext.parentId()));
                }
                headerMap.put("x-b3-traceid", traceId);
                headerMap.put("x-b3-spanid", spanId);
            }
        }
        return headerMap;
    }

    /**
     * createStopWatchIfNecessary
     *
     * @param request
     * @return org.springframework.util.StopWatch
     * @author 方典典
     * @time 2019/1/15 17:44
     */
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
    private Object getRequestBody(HttpServletRequest request) {
        if (isBinaryContent(request) || isMultipart(request)) {
            return JsonUtils.parse("{\"content\":\"二进制\"}");
        }
        String requestString = "";
        try {
            byte[] buf = ((ContentCachingRequestWrapper)request).getContentAsByteArray();
            requestString = new String(buf, 0, buf.length, "utf-8");
            return JsonUtils.parse(requestString);
        } catch (Exception e) {
            return JsonUtils.parse("{\"unknown\":\"ExceptionName:" + e.getClass().getName() + " ContentType:" +
                    request.getContentType() + " requestBody:" + requestString + "\"}");
        }
    }

    /**
     * @param response
     * @return com.alibaba.fastjson.JSON
     * @Description:获取响应Body
     * @exception:
     * @author: 方典典
     * @time:2018/11/6 16:50
     */
    private Object getResponseBody(HttpServletResponse response) {
        String bodyString = "";
        try {
            byte[] buf = ((ContentCachingResponseWrapper) response).getContentAsByteArray();
            bodyString = new String(buf, 0, buf.length, "utf-8");
            return JsonUtils.parse(bodyString);
        } catch (Exception e) {
            return JsonUtils.parse("{\"unknown\":\"ExceptionName:" + e.getClass().getName() + " ContentType:" +
                    response.getContentType() + " responseBody:" + bodyString + "\"}");
        }
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
