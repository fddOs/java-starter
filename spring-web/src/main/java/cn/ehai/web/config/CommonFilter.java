package cn.ehai.web.config;

import cn.ehai.common.core.ServiceException;
import cn.ehai.common.utils.AESUtils;
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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

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
					String respStr = getResponseBody(contentCachingResponseWrapper);

					String resSign = SignUtils.signResponse(respStr);
					contentCachingResponseWrapper.setHeader("x-ehi-sign",resSign);

					respStr="{\"errorCode\":15000403,\"message\":\"验证码接口错误，请稍后再试。\",\"result\":null}";
					out= response.getOutputStream();
					out.write(AESUtils.aesEncryptString(respStr).getBytes("UTF-8"));
					out.flush();

				} catch (Exception e){
					LoggerUtils.error(CommonFilter.class, ExceptionUtils.getStackTrace(e));
				}finally {

				}
			}
		}


	}

	/**
	 * @param response
	 * @return com.alibaba.fastjson.JSON
	 * @Description:获取响应Body
	 * @exception:
	 * @author: 方典典
	 * @time:2018/11/6 16:50
	 */
	private String getResponseBody(EhiHttpServletResponseWrapper response) throws IOException {
		byte[] buf = response.getContent();
		String bodyString;
		if (buf.length > 0) {
			try {
				bodyString = new String(buf, 0, buf.length, "utf-8");
				return bodyString;
			} catch (Exception e) {
				return "unknown";
			}
		}
		return "unknown";
	}

	@Override
	public void destroy() {

	}

}
