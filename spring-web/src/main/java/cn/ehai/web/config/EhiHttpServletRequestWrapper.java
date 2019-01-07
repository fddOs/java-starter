package cn.ehai.web.config;

import cn.ehai.common.utils.EHIExceptionLogstashMarker;
import cn.ehai.common.utils.EHIExceptionMsgWrapper;
import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ServiceException;
import cn.ehai.common.utils.AESUtils;
import cn.ehai.common.utils.SignUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import cn.ehai.common.utils.LoggerUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import static cn.ehai.web.common.SignConfig.SIGN_HEADER;

/**
 * 重写HttpServletRequestWrapper方法
 * @author 
 * @version 
 */
public class EhiHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private byte[] requestBody = null;

	private Map<String, String[]> parameterMap;

	private String charse = "UTF-8";

	public EhiHttpServletRequestWrapper (HttpServletRequest request) {

		super(request);
		parameterMap = handlerQuestString(request.getQueryString());
		String reqBody="";
		//缓存请求body
		try {
			reqBody = aesDecrypt(StreamUtils.copyToString(request.getInputStream(),
				Charset.forName(charse)));
			if(reqBody==null){
				reqBody="";
			}
			requestBody = reqBody.getBytes(charse);
		} catch (Exception e) {
            LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper(getClass()
                    .getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new Object[]{request},
                    ExceptionUtils.getStackTrace(e))));
			throw new ServiceException(ResultCode.UNAUTHORIZED,"签名错误");
		}
		if(!signRequest(request,reqBody,getQueryString())){
			throw new ServiceException(ResultCode.UNAUTHORIZED,"签名错误");
		}

	}


	private boolean signRequest(HttpServletRequest request,String requestBody,String query){

		String resMd5 = SignUtils.sign(query,requestBody);

		if(org.apache.commons.lang3.StringUtils.isEmpty(requestBody) && query == null ){
			return true;
		}
		String md5 = request.getHeader(SIGN_HEADER);
		if(org.apache.commons.lang3.StringUtils.isEmpty(md5)  || org.apache.commons.lang3.StringUtils
			.isEmpty(resMd5) ){
			return false;
		}

		return md5.equalsIgnoreCase(resMd5);
	}

	/**
	 * 重写 getInputStream()
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if(requestBody == null){
			requestBody= new byte[0];
		}
		final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
		return new ServletInputStream() {
			@Override
			public int read() throws IOException {
				return bais.read();
			}

			@Override
			public boolean isFinished() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isReady() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setReadListener(ReadListener listener) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	/**
	 * AES解密
	 * @param string
	 * @return java.lang.String
	 * @author lixiao
	 * @date 2018/12/15 15:49
	 */
	private String aesDecrypt(String string){
		try {
			return AESUtils.aesDecryptString(string);
		} catch (Exception e){
			throw new ServiceException(ResultCode.UNAUTHORIZED,"参数解密错误");
		}
	}

	/**
	 * 重写 getReader()
	 */
	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}


	private Map<String, String[]> handlerQuestString(String questSting){
		Map<String, String[]> paramsMap = new HashMap();
		if(StringUtils.isEmpty(questSting)){
			return paramsMap;
		}
		try {
			questSting= URLDecoder.decode(questSting,charse);
		} catch (UnsupportedEncodingException e) {
			throw new ServiceException(ResultCode.FAIL,"URLDecoder解码失败");
		}
		try {
			String params = aesDecrypt(questSting);
			if (!StringUtils.isEmpty(params)) {
				String[] paramList = params.split("&");
				for (String param : paramList) {
					String[] string = param.split("=");
					if (string.length == 2 && !StringUtils.isEmpty(string[1])) {
						paramsMap.put(string[0], new String[]{string[1]});
					}
				}
			}
		} catch (Exception e){
			LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper(getClass()
					.getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new Object[]{questSting},
					ExceptionUtils.getStackTrace(e))));
		}
		return paramsMap;
	}

	// 重写几个HttpServletRequestWrapper中的方法
	/**
	 * 获取所有参数名
	 *
	 * @return 返回所有参数名
	 */
	@Override
	public Enumeration<String> getParameterNames() {
		Vector<String> vector = new Vector<String>(parameterMap.keySet());
		return vector.elements();
	}

	/**
	 * 获取指定参数名的值，如果有重复的参数名，则返回第一个的值 接收一般变量 ，如text类型
	 *
	 * @param name
	 *            指定参数名
	 * @return 指定参数名的值
	 */
	@Override
	public String getParameter(String name) {
		String[] results = parameterMap.get(name);
		if (results == null || results.length <= 0)
			return null;
		else {
			return results[0];
		}
	}

	/**
	 * 获取指定参数名的所有值的数组，如：checkbox的所有数据
	 * 接收数组变量 ，如checkobx类型
	 */
	@Override
	public String[] getParameterValues(String name) {
		String[] results = parameterMap.get(name);
		if (results == null || results.length <= 0)
			return null;
		else {
			int length = results.length;
			for (int i = 0; i < length; i++) {
				results[i] = results[i];
			}
			return results;
		}
	}
}
