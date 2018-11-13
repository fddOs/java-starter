package cn.ehai.web.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import cn.ehai.common.utils.LoggerUtils;
import org.owasp.encoder.Encode;
import org.springframework.util.StreamUtils;

/**
 * 重写HttpServletRequestWrapper方法
 * @author 
 * @version 
 */
public class EhiHttpServletRequestWrapper extends HttpServletRequestWrapper {
	private byte[] requestBody = null;

	public EhiHttpServletRequestWrapper (HttpServletRequest request) {

		super(request);

		//缓存请求body
		try {
			requestBody = StreamUtils.copyToByteArray(request.getInputStream());
		} catch (IOException e) {
			LoggerUtils.error(EhiHttpServletRequestWrapper.class,e.fillInStackTrace().toString());
		}
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
	 * 重写 getReader()
	 */
	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public String[] getParameterValues(String name) {
		
		String[] values = super.getParameterValues(name);
		if (values != null) {
			int length = values.length;
			String[] escapseValues = new String[length];
			for (int i = 0; i < length; i++) {
				escapseValues[i] = Encode.forHtmlContent(values[i]);
			}
			return escapseValues;
		}
		
		return  super.getParameterValues(name);
	}
}
