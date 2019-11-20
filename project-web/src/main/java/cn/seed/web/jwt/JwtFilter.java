package cn.seed.web.jwt;

import cn.seed.common.core.*;
import cn.seed.common.utils.LoggerUtils;
import cn.seed.web.common.ExcludePathHandler;
import cn.seed.web.config.BaseHeaderReqWrapper;

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
//        LoggerUtils.info(JwtFilter.class,"doFilter  "+((HttpServletRequest) request).getServletPath());
        if (!Boolean.valueOf(ApolloBaseConfig.getJwtEnable()) || ExcludePathHandler.contain(request,
                response, ApolloBaseConfig.getJwtExcludePath())) {
            chain.doFilter(request, response);
        } else {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            BaseHeaderReqWrapper contentCachingRequestWrapper = new BaseHeaderReqWrapper(httpServletRequest);
            String loginUrl = ApolloBaseConfig.getWebLoginUrl();
            if (JwtTokenAuthentication.getAuthentication(httpServletRequest)) {
                JwtTokenAuthentication.setJwtHeader(contentCachingRequestWrapper);
                chain.doFilter(contentCachingRequestWrapper, response);
            } else {
                if (!StringUtils.isEmpty(loginUrl)) {
                    ((HttpServletResponse) response).sendRedirect(httpServletRequest.getContextPath() + loginUrl);
                }else{
                    throw new ServiceException(ResultCode.UNAUTHORIZED, "用户JWT信息验证失败");
                }
            }
        }
    }

    @Override
    public void destroy() {

    }

}
