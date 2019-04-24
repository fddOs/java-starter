package cn.seed.log.elk;

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
import cn.seed.common.utils.HeaderUtils;
import cn.seed.common.utils.JsonUtils;
import cn.seed.common.utils.LoggerUtils;
import cn.seed.common.utils.ProjectInfoUtils;
import cn.seed.common.utils.UuidUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    //    private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String ATTRIBUTE_STOP_WATCH = LoggingFilter.class.getName()
            + ".StopWatch";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain
            filterChain) throws IOException {
        StopWatch stopWatch = this.createStopWatchIfNecessary(request);
        String requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
            if (e instanceof ServiceException) {
                responseResult(wrapperResponse, ResultGenerator.genFailResult(((ServiceException) e).getResultCode(),
                        e.getMessage()));
                return;
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
            Object responseBody = getResponseBody(wrapperResponse);
            wrapperResponse.copyBodyToResponse();
            if (!wrapperRequest.isAsyncStarted()) {
                if (wrapperResponse.isCommitted()) {
                    httpStatus = wrapperResponse.getStatus();
                }
                stopWatch.stop();
                request.removeAttribute(ATTRIBUTE_STOP_WATCH);
            }
            String responseTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            RequestLog requestLog = new RequestLog(requestId, requestTime, true,
                    ProjectInfoUtils.PROJECT_CONTEXT, requestUrl, getRequestBody(wrapperRequest),
                    request.getMethod(), headerMap);
            Map<String, String> responseHeaderMap = HeaderUtils.responseHeaderHandler(response);
            responseHeaderMap.put("response.code", String.valueOf(httpStatus));
            ResponseLog responseLog = new ResponseLog(responseTime, httpStatus, errorMsg, stopWatch
                    .getTotalTimeMillis(), responseBody, responseHeaderMap);
            LOGGER.info(new EHILogstashMarker(requestLog, responseLog), null);
        }
    }

    /**
     * getRequestHeaderMap
     *
     * @param request
     * @return java.util.Map<java.lang.String               ,               java.lang.String>
     * @author 方典典
     * @time 2019/1/15 17:44
     */
    private Map<String, String> getRequestHeaderMap(HttpServletRequest request) {
        Map<String, String> headerMap = HeaderUtils.requestHeaderHandler(request);
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
    private Object getRequestBody(ContentCachingRequestWrapper request) {
        if (isBinaryContent(request) || isMultipart(request)) {
            return JsonUtils.parse("{\"content\":\"二进制\"}");
        }
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            String requestString = null;
            try {
                requestString = new String(buf, 0, buf.length, "utf-8");
                return JsonUtils.parse(requestString);
            } catch (Exception e) {
                return JsonUtils.parse("{\"unknown\":\"ExceptionName:" + e.getClass().getName() + " ContentType:" +
                        request.getContentType() + " requestBody:" + requestString + "\"}");
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
    private Object getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        String bodyString = "";
        if (buf.length > 0) {
            try {
                bodyString = new String(buf, 0, buf.length, "utf-8");
                return JsonUtils.parse(bodyString);
            } catch (Exception e) {
                return JsonUtils.parse("{\"unknown\":\"ExceptionName:" + e.getClass().getName() + " ContentType:" +
                        response.getContentType() + " responseBody:" + bodyString + "\"}");
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
