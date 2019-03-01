package cn.ehai.web.config;

import cn.ehai.common.core.ApolloBaseConfig;
import cn.ehai.common.core.Result;
import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ResultGenerator;
import cn.ehai.common.core.ServiceException;
import cn.ehai.common.utils.AESUtils;
import cn.ehai.common.utils.EHIExceptionLogstashMarker;
import cn.ehai.common.utils.EHIExceptionMsgWrapper;
import cn.ehai.common.utils.IOUtils;
import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.common.utils.SignUtils;
import cn.ehai.web.common.ExcludePathHandler;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 验证加密、签名请求过滤器
 *
 * @author lixiao
 * @date 2019-02-12 15:32
 */
@Order(Integer.MIN_VALUE)
@Configuration
@WebFilter(filterName = "VerificationReqFilter", urlPatterns = "/**")
public class VerificationReqFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (ExcludePathHandler.contain(request, response, ApolloBaseConfig.get("verificationExcludePath", ""))) {
            chain.doFilter(request, response);
        } else {
            ServletRequest requestWrapper = null;
            try {
                if (request instanceof HttpServletRequest) {
                    requestWrapper = new EhiSignServletRequestWrapper((HttpServletRequest) request);
                }
            } catch (Exception e) {
                String exceptionMsg = "";
                if (e instanceof ServiceException) {
                    exceptionMsg = e.getMessage();
                } else {
                    exceptionMsg = "参数验证失败";
                }
                responseResult((HttpServletResponse) response, ResultGenerator.genFailResult(ResultCode.UNAUTHORIZED,
                        exceptionMsg));
                LoggerUtils.error(getClass(), new Object[]{request, response, chain}, e);
                return;
            }
            if (requestWrapper == null) {
                chain.doFilter(request, response);
            } else {
                chain.doFilter(requestWrapper, response);
            }
        }
    }

    @Override
    public void destroy() {

    }

    private void responseResult(HttpServletResponse response, Result result) {
        boolean reqDecode = Boolean.parseBoolean(
                ApolloBaseConfig.get("reqDecode", "false"));
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        try {
            String respStr = JSON.toJSONString(result);
            if (reqDecode) {
                respStr = AESUtils.aesEncryptString(respStr);
                response.setHeader("content-type", "text");
            } else {
                response.setHeader("content-type", "application/json");
            }
            String resSign = SignUtils.signResponse(respStr);
            response.setHeader("x-ehi-sign", resSign);

            response.setHeader("content-length", String.valueOf(respStr.getBytes("UTF-8").length));
            response.getWriter().write(respStr);
        } catch (Exception ex) {
        }
    }
}
