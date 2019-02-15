package cn.ehai.web.jwt;

import cn.ehai.common.core.*;
import cn.ehai.common.utils.EHIExceptionLogstashMarker;
import cn.ehai.common.utils.EHIExceptionMsgWrapper;
import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.web.common.ExcludePathHandler;
import cn.ehai.web.config.EhiHeaderReqWrapper;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * jwt的过滤器
 *
 * @author lixiao
 * @date 2018-12-20 15:34
 */
@Configuration
//@ConditionalOnProperty(value = "jwt.enabled", havingValue = "true")
public class JwtFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        EhiHeaderReqWrapper contentCachingRequestWrapper = new EhiHeaderReqWrapper(httpServletRequest);
        if (ExcludePathHandler.contain(request, response, ApolloBaseConfig.get("web.sign" +
                ".exclude-path", ""))) {
            chain.doFilter(contentCachingRequestWrapper, response);
        }
        JwtTokenAuthentication.setJwtHeader(contentCachingRequestWrapper);
        String loginUrl = ApolloBaseConfig.get("web.login-url", "");
        if (JwtTokenAuthentication.getAuthentication(httpServletRequest)) {
            chain.doFilter(contentCachingRequestWrapper, response);
        } else {
            if (!StringUtils.isEmpty(loginUrl)) {
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + loginUrl);
            }
            responseResult(httpServletResponse, ResultGenerator.genFailResult(ResultCode.UNAUTHORIZED, "jwt token " +
                    "验证失败"));
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
            LoggerUtils.error(getClass(), new Object[]{response, result}, ex);
        }
    }
}
