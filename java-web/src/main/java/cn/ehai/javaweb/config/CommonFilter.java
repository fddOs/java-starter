package cn.ehai.javaweb.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * @Description:通用拦截器处理参数不能二次读取问题
 * @author:lixiao
 * @time:2017年11月10日 下午4:46:07
 */
@Order(1)
@Component
@WebFilter(filterName = "CommonFilter", urlPatterns = "/**")
public class CommonFilter  implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		ServletRequest requestWrapper = null;  
        if(request instanceof HttpServletRequest) {  
            requestWrapper = new EhiHttpServletRequestWrapper((HttpServletRequest) request);  
        }  
        if(requestWrapper == null) {  
            chain.doFilter(request, response);  
        } else {  
            chain.doFilter(requestWrapper, response);  
        }    
	}

	@Override
	public void destroy() {

	}

}
