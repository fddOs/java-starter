package cn.ehai.web.config;

import cn.ehai.common.core.ApolloBaseConfig;
import cn.ehai.common.core.Result;
import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ResultGenerator;
import cn.ehai.common.core.ServiceException;
import cn.ehai.common.utils.AESUtils;
import cn.ehai.common.utils.EHIExceptionLogstashMarker;
import cn.ehai.common.utils.EHIExceptionMsgWrapper;
import cn.ehai.common.utils.IOUtils;
import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.common.utils.SignUtils;
import com.alibaba.fastjson.JSON;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


/**
 * 验证加密、签名返回参数过滤器
 *
 * @author lixiao
 * @date 2019-02-12 16:00
 */
@Order(100)
@Configuration
public class VerificationResFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        EhiHttpServletResponseWrapper contentCachingResponseWrapper = new EhiHttpServletResponseWrapper(
            (HttpServletResponse) response);
            try {
                chain.doFilter(request, contentCachingResponseWrapper);
            } catch (Exception e) {
                boolean isLogger = !(e instanceof ServiceException);
                if (isLogger) {
                    LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper(getClass()
                        .getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new
                        Object[]{request, response, chain}, ExceptionUtils.getStackTrace(e))));
                }
            }
        ServletOutputStream out;
        try {
            String respStr = IOUtils.getResponseBody(contentCachingResponseWrapper.getContent());
            boolean reqDecode = Boolean.parseBoolean(
                ApolloBaseConfig.get("reqDecode","false"));
            if(reqDecode){
                respStr = AESUtils.aesEncryptString(respStr);
            }

            byte[] aesResp =respStr.getBytes("UTF-8");
            String resSign = SignUtils.signResponse(respStr);
            contentCachingResponseWrapper.setHeader("x-ehi-sign", resSign);
            contentCachingResponseWrapper.setHeader("content-type", "text");
            contentCachingResponseWrapper.setHeader("content-length", String.valueOf(aesResp.length));
            out = response.getOutputStream();
            out.write(aesResp);
            out.flush();

        } catch (Exception e) {
            LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper(getClass()
                .getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new
                Object[]{request, response, chain}, ExceptionUtils.getStackTrace(e))));
        } finally {

        }
    }

    @Override
    public void destroy() {

    }
}
