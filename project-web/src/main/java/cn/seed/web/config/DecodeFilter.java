package cn.seed.web.config;

import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ResultGenerator;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.AESUtils;
import cn.seed.common.utils.IOUtils;
import cn.seed.common.utils.LoggerUtils;
import cn.seed.common.utils.RequestInfoUtils;
import cn.seed.web.common.ExcludePathHandler;
import com.alibaba.fastjson.JSON;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import cn.seed.web.config.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

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
        response.setCharacterEncoding("UTF-8");
        //        ServletRequest requestWrapper = new BaseHttpServletRequestWrapper((HttpServletRequest) request);
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
//        BaseHttpServletResponseWrapper responseWrapper = new BaseHttpServletResponseWrapper(
//                (HttpServletResponse) response);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse)
                response);
        boolean isDecode = Boolean.valueOf(ApolloBaseConfig.getReqDecode());
        if (RequestInfoUtils.contentTypeIsNotApplicationJson(request) || !isDecode || ExcludePathHandler.contain
                (request, response, ApolloBaseConfig.getDecodeExcludePath())) {
            chain.doFilter(requestWrapper, responseWrapper);
            responseWrapper.copyBodyToResponse();
//            responseResult(responseWrapper, IOUtils.getResponseBody(responseWrapper.getContent()),
//                    false);
        } else {
            BaseDecodeServletRequestWrapper decodeRequestWrapper;
            try {
                decodeRequestWrapper = new BaseDecodeServletRequestWrapper((HttpServletRequest) request);
            } catch (Exception e) {
                String exceptionMsg = "";
                if (e instanceof ServiceException) {
                    exceptionMsg = e.getMessage();
                } else {
                    exceptionMsg = "解密失败";
                }
                LoggerUtils.error(getClass(), new Object[]{request, response, chain}, e);
                responseResult(responseWrapper, JSON.toJSONString(ResultGenerator.genFailResult
                        (ResultCode.BAD_REQUEST, exceptionMsg)), true);
                responseWrapper.copyBodyToResponse();
                return;
            }
            chain.doFilter(decodeRequestWrapper, responseWrapper);
            String respStr = IOUtils.getResponseBody(responseWrapper.getContentAsByteArray());
            responseWrapper.resetBuffer();
            responseResult(responseWrapper, respStr, true);
            responseWrapper.copyBodyToResponse();
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
     * @param isEncrypt
     * @return void
     * @author 方典典
     * @time 2019/3/1 17:49
     */
    private void responseResult(HttpServletResponse response, String result, boolean isEncrypt) {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        response.setHeader("Cache-Control", "no-store");
        String respStr = result;
        if (isEncrypt) {
            try {
                respStr = AESUtils.aesEncryptString(result);
            } catch (Exception ex) {
                respStr = "加密错误";
                LoggerUtils.error(getClass(), new Object[]{response, result}, ex);
            }
            response.setHeader("content-type", "text");
        }

        try {
            response.setHeader("content-length", String.valueOf(respStr.getBytes("UTF-8").length));
            response.getWriter().write(respStr);
        } catch (Exception e) {
            LoggerUtils.error(getClass(), new Object[]{response, result}, e);
        }

    }
}
