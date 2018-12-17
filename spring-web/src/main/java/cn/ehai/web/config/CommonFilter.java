package cn.ehai.web.config;

import cn.ehai.common.core.ServiceException;
import cn.ehai.common.utils.AESUtils;
import cn.ehai.common.utils.IOUtils;
import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.common.utils.SignUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * @Description:通用拦截器处理参数不能二次读取问题
 * @author:lixiao
 * @time:2017年11月10日 下午4:46:07
 */
@Order(1)
@Configuration
@WebFilter(filterName = "CommonFilter", urlPatterns = "/**")
@ConditionalOnProperty(
	prefix = "project",
	value= "signFilter",
	havingValue = "true"
)
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

		EhiHttpServletResponseWrapper contentCachingResponseWrapper = new EhiHttpServletResponseWrapper((HttpServletResponse) response);

		if(requestWrapper == null) {
			chain.doFilter(request, response);
		} else {
			try{
				chain.doFilter(requestWrapper, contentCachingResponseWrapper);
			}catch (Exception e){
				boolean isLogger = !(e instanceof ServiceException);
				if(isLogger){
					LoggerUtils.error(CommonFilter.class, ExceptionUtils.getStackTrace(e));
				}
			}finally {

				ServletOutputStream out;
				try {
					String respStr = IOUtils.getResponseBody(contentCachingResponseWrapper.getContent());


					byte[] aesResp = AESUtils.aesEncryptString(respStr).getBytes("UTF-8");

					String resSign = SignUtils.signResponse(respStr);
					contentCachingResponseWrapper.setHeader("x-ehi-sign",resSign);
					contentCachingResponseWrapper.setHeader("content-type","text");
					contentCachingResponseWrapper.setHeader("content-length",String.valueOf(aesResp.length));
					out= response.getOutputStream();
					out.write(aesResp);
					out.flush();

				} catch (Exception e){
					LoggerUtils.error(CommonFilter.class, ExceptionUtils.getStackTrace(e));
				}finally {

				}
			}
		}


	}

	@Override
	public void destroy() {

	}

}
