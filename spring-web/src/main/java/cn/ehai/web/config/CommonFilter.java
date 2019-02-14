package cn.ehai.web.config;

import cn.ehai.common.core.*;
import cn.ehai.common.utils.EHIExceptionLogstashMarker;
import cn.ehai.common.utils.EHIExceptionMsgWrapper;
import cn.ehai.common.utils.AESUtils;
import cn.ehai.common.utils.IOUtils;
import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.common.utils.SignUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;

/**
 * @Description:通用拦截器处理参数不能二次读取问题
 * @author:lixiao
 * @time:2017年11月10日 下午4:46:07
 */
//@Order(1)
//@Configuration
//@WebFilter(filterName = "CommonFilter", urlPatterns = "/**")
//@ConditionalOnProperty(
//        prefix = "project",
//        value = "sign",
//        havingValue = "true"
//)
public class CommonFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String excludePaths = "/druid/**,/swagger-resources/**,/v2/**,/heartbeat" + ApolloBaseConfig.get("web.sign" +
                ".exclude-path", "");
        List<String> excludePathList = Arrays.asList(excludePaths.split(","));
        AntPathMatcher matcher = new AntPathMatcher();
        excludePathList.forEach(excludePath -> {
            if (matcher.match(excludePath, ((RequestFacade) request).getServletPath())) {
                try {
                    chain.doFilter(request, response);
                } catch (Exception e) {
                    if (!(e instanceof ServiceException)) {
                        LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper
                                (getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new
                                        Object[]{request, response, chain}, ExceptionUtils.getStackTrace(e))));
                    }
                } finally {
                    return;
                }
            }
        });
        ServletRequest requestWrapper = null;
        try {
            if (request instanceof HttpServletRequest) {
                requestWrapper = new EhiHttpServletRequestWrapper((HttpServletRequest) request);
            }
        } catch (Exception e) {
            responseResult((HttpServletResponse) response, ResultGenerator.genFailResult(ResultCode.UNAUTHORIZED,
                    "签名错误"));
            return;
        }

        EhiHttpServletResponseWrapper contentCachingResponseWrapper = new EhiHttpServletResponseWrapper(
                (HttpServletResponse) response);

        if (requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            try {
                chain.doFilter(requestWrapper, contentCachingResponseWrapper);
            } catch (Exception e) {
                boolean isLogger = !(e instanceof ServiceException);
                if (isLogger) {
                    LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper(getClass()
                            .getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new
                            Object[]{request, response, chain}, ExceptionUtils.getStackTrace(e))));
                }
            } finally {

                ServletOutputStream out;
                try {
                    String respStr = IOUtils.getResponseBody(contentCachingResponseWrapper.getContent());


                    byte[] aesResp = AESUtils.aesEncryptString(respStr).getBytes("UTF-8");

                    String resSign = SignUtils.signResponse(respStr);
                    contentCachingResponseWrapper.setHeader("x-ehi-sign", resSign);
                    contentCachingResponseWrapper.setHeader("content-type", "text");
                    contentCachingResponseWrapper.setHeader("content-length", String.valueOf(aesResp.length));
                    out = response.getOutputStream();
                    out.write(aesResp);
                    out.flush();

                } catch (Exception e) {
                    LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper(getClass()
                            .getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new
                            Object[]{request, response, chain}, ExceptionUtils.getStackTrace(e))));
                } finally {

                }
            }
        }


    }

    @Override
    public void destroy() {

    }

    private void responseResult(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result));

        } catch (IOException ex) {
        }
    }
}
