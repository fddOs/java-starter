package cn.ehai.web.config;

import cn.ehai.common.core.Result;
import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ResultGenerator;
import cn.ehai.common.utils.*;
import com.alibaba.fastjson.JSON;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static cn.ehai.web.common.SignConfig.SIGN_HEADER;

/**
 * 请求签名验证
 *
 * @author lixiao
 * @date 2018/12/15 09:57
 */
public class RequestSignInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 验证签名
        EhiSignServletRequestWrapper ehiHttpServletRequestWrapper = new EhiSignServletRequestWrapper(request);
        if (validateSign(ehiHttpServletRequestWrapper)) {
            return true;
        } else {
            LoggerUtils.fmtError(RequestSignInterceptor.class, "签名认证失败，请求接口：{}，请求IP：{}，请求参数：{}", request
                            .getRequestURI(), getIpAddress(request),
                    JSON.toJSONString(request.getParameterMap()));
            responseResult(response, ResultGenerator.genFailResult(ResultCode.UNAUTHORIZED, "签名认证失败"));
            return false;
        }
    }


    /**
     * @Description: 验证签名的方式
     * @param: [request]
     * @return: boolean
     * @exception:
     * @author:　minhau
     * @time: 上午11:16 18-8-8
     */
    private boolean validateSign(EhiSignServletRequestWrapper request) {
        String requestBody = IOUtils.readerToString(request);
        String query = request.getQueryString();
        String resMd5 = SignUtils.sign(query, requestBody);

        if (StringUtils.isEmpty(requestBody) && query == null) {
            return true;
        }
        String md5 = request.getHeader(SIGN_HEADER);
        if (StringUtils.isEmpty(md5) || StringUtils.isEmpty(resMd5)) {
            return false;
        }

        return md5.equalsIgnoreCase(resMd5);
    }


    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，那么取第一个ip为客户端ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }

        return ip;
    }

    private void responseResult(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper
                    (getClass().getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new
                            Object[]{response, result}, ExceptionUtils.getStackTrace(ex))));
        }
    }
}
