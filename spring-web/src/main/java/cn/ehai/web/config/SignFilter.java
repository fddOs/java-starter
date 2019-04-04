package cn.ehai.web.config;

import cn.ehai.common.core.*;
import cn.ehai.common.utils.AESUtils;
import cn.ehai.common.utils.IOUtils;
import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.common.utils.SignUtils;
import cn.ehai.web.common.ExcludePathHandler;
import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static cn.ehai.web.common.SignConfig.SIGN_HEADER;
import static org.springframework.http.HttpMethod.GET;

/**
 * 签名过滤器
 *
 * @author 方典典
 * @time 2019/3/1 15:57
 */
@Order(Integer.MIN_VALUE + 2)
@Component
//@WebFilter(filterName = "signFilter", urlPatterns = "/**")
public class SignFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        boolean isSign = Boolean.valueOf(ApolloBaseConfig.getReqSign());
        if (!isSign || ExcludePathHandler.contain(request, response, ApolloBaseConfig.getSignExcludePath())) {
            chain.doFilter(request, response);
        } else {
            EhiHttpServletResponseWrapper contentCachingResponseWrapper;
            if (response instanceof EhiHttpServletResponseWrapper) {
                contentCachingResponseWrapper = (EhiHttpServletResponseWrapper) response;
            } else {
                contentCachingResponseWrapper = new EhiHttpServletResponseWrapper((HttpServletResponse) response);
            }
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String requestBody = "";
            if (!GET.name().equals(httpServletRequest.getMethod())) {
                try {
                    requestBody = StreamUtils.copyToString(request.getInputStream(),
                            Charset.forName("UTF-8"));
                } catch (IOException e) {
                    LoggerUtils.error(getClass(), new Object[]{request}, e);
                    throw new ServiceException(ResultCode.UNAUTHORIZED, "body获取错误");
                }
            }
            if (!signRequest(httpServletRequest, requestBody, httpServletRequest.getQueryString())) {
                throw new ServiceException(ResultCode.UNAUTHORIZED, "签名错误");
            }
            chain.doFilter(request, contentCachingResponseWrapper);
            try {
                String respStr = IOUtils.getResponseBody(contentCachingResponseWrapper.getContent());
                String resSign = SignUtils.signResponse(respStr);
                ((HttpServletResponse) response).setHeader("x-ehi-sign", resSign);
                ServletOutputStream out = response.getOutputStream();
                out.write(respStr.getBytes("UTF-8"));
                out.flush();
            } catch (Exception e) {
                LoggerUtils.error(getClass(), new Object[]{request, response, chain}, e);
                throw new ServiceException(ResultCode.UNAUTHORIZED, "签名错误");
            }
        }
    }

    @Override
    public void destroy() {

    }

    private boolean signRequest(HttpServletRequest request, String requestBody, String query) {
        if (StringUtils.isEmpty(requestBody) && StringUtils.isEmpty(query)) {
            return true;
        }
        String sign = SignUtils.sign(query, requestBody);
        String signHeader = request.getHeader(SIGN_HEADER);
        if (StringUtils.isEmpty(signHeader) || StringUtils
                .isEmpty(sign)) {
            return false;
        }
        return signHeader.equalsIgnoreCase(sign);
    }
}
