package cn.ehai.web.jwt;

import cn.ehai.web.config.EhiHeaderReqWrapper;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * jwt的过滤器
 *
 * @author lixiao
 * @date 2018-12-20 15:34
 */
@Configuration
@ConditionalOnProperty(value = "jwt.enabled", havingValue = "true")
public class JwtFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        EhiHeaderReqWrapper contentCachingRequestWrapper = new EhiHeaderReqWrapper((HttpServletRequest) request);
        JwtTokenAuthentication.setJwtHeader(contentCachingRequestWrapper);
        chain.doFilter(contentCachingRequestWrapper,response);
    }

    @Override public void destroy() {

    }
}
