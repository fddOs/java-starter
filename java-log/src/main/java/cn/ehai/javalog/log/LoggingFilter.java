package cn.ehai.javalog.log;

import cn.ehai.javalog.entity.LogELK;
import cn.ehai.javautils.core.*;
import cn.ehai.javautils.utils.CommonUtils;
import cn.ehai.javautils.utils.HeaderUtils;
import cn.ehai.javautils.utils.LoggerUtils;
import cn.ehai.javautils.utils.UuidUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @Description:记录请求日志
 * @author:方典典
 * @time:2018/11/6 10:18
 */
@Component
public class LoggingFilter extends OncePerRequestFilter {
    private static final Random RANDOM = new Random();
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
        Integer httpStatus = 200;
        try {
            filterChain.doFilter(wrapperRequest, wrapperResponse);
        } catch (Exception e) {
            if (e.getCause() != null) {
                e = (Exception) e.getCause();
            }
            LoggerUtils.error(getClass(), ExceptionUtils.getStackTrace(e));
            if (!exceptionHandler(e, wrapperResponse)) {
                // 异常码:0X + 时间戳 + 100~999随机数
                requestId = "0X" + Long.toHexString(new Date().getTime()).toUpperCase()
                        + (RANDOM.nextInt(900) + 100);
                errorMsg = ExceptionUtils.getStackTrace(e);
                wrapperResponse.setStatus(5001);
                responseResult(wrapperResponse, ResultGenerator.genFailResult(ResultCode.INTERNAL_SERVER_ERROR,
                        "程序发生异常，错误代码:" + requestId));
            }
        } finally {
            boolean isClose = "none".equalsIgnoreCase(ApolloBaseConfig.getLogSwitch());
            boolean isBool = !"ALL".equalsIgnoreCase(ApolloBaseConfig.getLogSwitch()) && "GET".equalsIgnoreCase(wrapperRequest.getMethod());
            if (isClose || isBool) {
                wrapperResponse.copyBodyToResponse();
                return;
            }
            if (requestId == null) {
                requestId = UuidUtils.getRandomUUID();
            }
            String queryString = request.getQueryString();
            String requestUrl = request.getServletPath();
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
            LogELK logELK = new LogELK(requestId, requestTime, responseTime, stopWatch.getTotalTimeMillis(), true,
                    CommonUtils.getProjectContext(), httpStatus, requestUrl, errorMsg, getRequestBody
                    (wrapperRequest),
                    responseBody, HeaderUtils.headerHandler(request));
            LOGGER.info(new EHILogstashMarker(logELK), null);
        }
    }

    /**
     * @param e
     * @param response
     * @return boolean
     * @Description:异常处理
     * @exception:
     * @author: 方典典
     * @time:2018/11/6 16:44
     */
    private boolean exceptionHandler(Throwable e, HttpServletResponse response) throws IOException {
        if (e instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) e;
            responseResult(response, ResultGenerator.genFailResult(serviceException.getCode(), serviceException
                    .getMessage()));
            return true;
        }
        if (e instanceof BindException) {
            BindException bindException = (BindException) e;
            List<FieldError> fieldErrors = bindException.getBindingResult().getFieldErrors();
            responseResult(response, ResultGenerator.genFailResult(ResultCode.BAD_REQUEST, fieldErrors.get(0)
                    .getDefaultMessage()));
            return true;
        }
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) e;
            List<FieldError> fieldErrors = methodArgumentNotValidException.getBindingResult().getFieldErrors();
            responseResult(response, ResultGenerator.genFailResult(ResultCode.BAD_REQUEST, fieldErrors.get(0)
                    .getDefaultMessage()));
            return true;
        }
        if (e instanceof NoHandlerFoundException) {
            NoHandlerFoundException noHandlerFoundException = (NoHandlerFoundException) e;
            responseResult(response, ResultGenerator.genFailResult(ResultCode.NOT_FOUND, noHandlerFoundException
                    .getMessage()));
            return true;
        }
        return false;
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
            String payload;
            try {
                payload = new String(buf, 0, buf.length, request.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                payload = "{\"unknown\":\"" + ExceptionUtils.getStackTrace(e) + "\"}";
            }
            return JSON.parseObject(payload.replaceAll("\\n", ""));
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
        if (buf.length > 0) {
            String payload;
            try {
                payload = new String(buf, 0, buf.length, response.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                payload = "{\"unknown\":\"" + ExceptionUtils.getStackTrace(e) + "\"}";
            }
            return JSON.parseObject(payload);
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
