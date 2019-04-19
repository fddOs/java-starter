package cn.seed.web.config;

import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ResultGenerator;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.AESUtils;
import cn.seed.common.utils.IOUtils;
import cn.seed.common.utils.LoggerUtils;
import cn.seed.web.common.ExcludePathHandler;
import com.alibaba.fastjson.JSON;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 解密过滤器
 *
 * @author 方典典
 * @time 2019/3/1 16:02
 */
@Order(Integer.MIN_VALUE)
@Component
//@WebFilter(filterName = "decodeFilter", urlPatterns = "/**")
public class DecodeFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (StringUtils.isEmpty(request.getContentType()) || !request.getContentType().contains
                ("application/json")) {
            chain.doFilter(request, response);
            return;
        }
        boolean isDecode = Boolean.valueOf(ApolloBaseConfig.getReqDecode());
        if (!isDecode || ExcludePathHandler.contain(request, response, ApolloBaseConfig.getDecodeExcludePath())) {
            ServletRequest requestWrapper = null;
            if (request instanceof HttpServletRequest) {
                requestWrapper = new EhiHttpServletRequestWrapper((HttpServletRequest) request);
            }
            if (requestWrapper == null) {
                chain.doFilter(request, response);
            } else {
                chain.doFilter(requestWrapper, response);
            }
        } else {
            EhiHttpServletResponseWrapper contentCachingResponseWrapper = new EhiHttpServletResponseWrapper(
                    (HttpServletResponse) response);
            EhiDecodeServletRequestWrapper ehiDecodeServletRequestWrapper;
            try {
                ehiDecodeServletRequestWrapper = new EhiDecodeServletRequestWrapper(
                        (HttpServletRequest) request);
            } catch (Exception e) {
                String exceptionMsg = "";
                if (e instanceof ServiceException) {
                    exceptionMsg = e.getMessage();
                } else {
                    exceptionMsg = "解密失败";
                }
                LoggerUtils.error(getClass(), new Object[]{request, response, chain}, e);
                responseResult((HttpServletResponse) response, JSON.toJSONString(ResultGenerator.genFailResult
                        (ResultCode.BAD_REQUEST, exceptionMsg)));
                return;
            }
            if (ehiDecodeServletRequestWrapper == null) {
                chain.doFilter(request, contentCachingResponseWrapper);
            } else {
                chain.doFilter(ehiDecodeServletRequestWrapper, contentCachingResponseWrapper);
            }
            String respStr = IOUtils.getResponseBody(contentCachingResponseWrapper.getContent());
            responseResult((HttpServletResponse) response, respStr);
        }

    }

    @Override
    public void destroy() {

    }

    /**
     * 加密response
     *
     * @param response
     * @param result
     * @return void
     * @author 方典典
     * @time 2019/3/1 17:49
     */
    private void responseResult(HttpServletResponse response, String result) {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        String respStr;
        try {
            respStr = AESUtils.aesEncryptString(result);
        } catch (Exception ex) {
            respStr = "加密错误";
            LoggerUtils.error(getClass(), new Object[]{response, result}, ex);
        }
        response.setHeader("content-type", "text");
        try {
            response.setHeader("content-length", String.valueOf(respStr.getBytes("UTF-8").length));
            response.getWriter().write(respStr);
        } catch (Exception e) {
            LoggerUtils.error(getClass(), new Object[]{response, result}, e);
        }

    }
}
