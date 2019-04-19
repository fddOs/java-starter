package cn.seed.web.jwt;

import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.Result;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ResultGenerator;
import cn.seed.common.utils.LoggerUtils;
import cn.seed.web.common.ExcludePathHandler;
import cn.seed.web.config.EhiHeaderReqWrapper;

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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * jwt的过滤器
 *
 * @author lixiao
 * @date 2018-12-20 15:34
 */
@Order(1)
//@Configuration
//@WebFilter(filterName = "jwtFilter", urlPatterns = "/**")
@Component
public class JwtFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        LoggerUtils.info(JwtFilter.class,"doFilter  "+((HttpServletRequest) request).getServletPath());
        if (!Boolean.valueOf(ApolloBaseConfig.getJwtEnable()) || ExcludePathHandler.contain(request,
                response, ApolloBaseConfig.getJwtExcludePath())) {
            chain.doFilter(request, response);
        } else {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            EhiHeaderReqWrapper contentCachingRequestWrapper = new EhiHeaderReqWrapper(httpServletRequest);
            String loginUrl = ApolloBaseConfig.getWebLoginUrl();
            if (JwtTokenAuthentication.getAuthentication(httpServletRequest)) {
                JwtTokenAuthentication.setJwtHeader(contentCachingRequestWrapper);
                chain.doFilter(contentCachingRequestWrapper, response);
            } else {
                if (!StringUtils.isEmpty(loginUrl)) {
                    ((HttpServletResponse) response).sendRedirect(httpServletRequest.getContextPath() + loginUrl);
                }
                responseResult((HttpServletResponse) response, ResultGenerator.genFailResult(
                    ResultCode
                        .UNAUTHORIZED, "jwt token" + " 验证失败"));
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
            response.getOutputStream().write(JSON.toJSONString(result).getBytes("UTF-8"));
        } catch (IOException ex) {
            LoggerUtils.error(getClass(), new Object[]{response, result}, ex);
        }
    }
}
