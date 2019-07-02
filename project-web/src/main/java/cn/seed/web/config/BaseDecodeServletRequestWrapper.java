package cn.seed.web.config;

import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.AESUtils;
import cn.seed.common.utils.LoggerUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;

import static org.springframework.http.HttpMethod.GET;

/**
 * 对请求参数进行解密
 *
 * @author lixiao
 * @date 2019-02-12 14:24
 */
public class BaseDecodeServletRequestWrapper extends ContentCachingRequestWrapper {

    private byte[] requestBody;

    private Map<String, String[]> parameterMap;

    private String queryString;

    private static final String CHARSE = "UTF-8";


    public BaseDecodeServletRequestWrapper(HttpServletRequest request) {

        super(request);
        //解密url的参数
        parameterMap = handlerQuestString(request.getQueryString());
        //处理body的参数
        String reqBody = handlerBodyParams(request);
        try {
            requestBody = reqBody.getBytes(CHARSE);
        } catch (UnsupportedEncodingException e) {
            LoggerUtils.error(getClass(), new Object[]{request}, e);
            throw new ServiceException(ResultCode.FAIL, e.getMessage());
        }

    }


    /**
     * 处理请求的body参数
     *
     * @param request
     * @return java.lang.String
     * @author lixiao
     * @date 2019-02-12 14:44
     */
    private String handlerBodyParams(HttpServletRequest request) {
        if (GET.name().equals(request.getMethod())) {
            return "";
        }
        String reqBody;
        try {
            reqBody = aesDecrypt(StreamUtils.copyToString(request.getInputStream(),
                    Charset.forName(CHARSE)));
        } catch (Exception e) {
            LoggerUtils.error(getClass(), new Object[]{request}, e);
            throw new ServiceException(ResultCode.UNAUTHORIZED, "body参数解密错误");
        }
        if (reqBody == null) {
            reqBody = "";
        }
        return reqBody;
    }

    /**
     * 重写 getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (requestBody == null) {
            requestBody = new byte[0];
        }
        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }
        };
    }

    /**
     * AES解密
     *
     * @param string
     * @return java.lang.String
     * @author lixiao
     * @date 2018/12/15 15:49
     */
    private String aesDecrypt(String string) {
        return AESUtils.aesDecryptString(string);
    }

    /**
     * 重写 getReader()
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }


    /**
     * 解密url后面的参数
     *
     * @param questSting
     * @return java.util.Map
     * @author lixiao
     * @date 2019-02-12 15:50
     */
    private Map<String, String[]> handlerQuestString(String questSting) {
        Map<String, String[]> paramsMap = new HashMap();
        if (StringUtils.isEmpty(questSting)) {
            return paramsMap;
        }
        //是否解密

        try {
            //对请求url的参数进行url解码
            questSting = URLDecoder.decode(questSting, CHARSE);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(ResultCode.FAIL, "URLDecoder解码失败");
        }
        //对请求url的参数进行解密
        queryString = aesDecrypt(questSting);
        if (!StringUtils.isEmpty(queryString)) {
            String[] paramList = queryString.split("&");
            for (String param : paramList) {
                String[] string = param.split("=");
                if (string.length == 2 && !StringUtils.isEmpty(string[1])) {
                    paramsMap.put(string[0], new String[]{string[1]});
                }
            }
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
     * @param name 指定参数名
     * @return 指定参数名的值
     */
    @Override
    public String getParameter(String name) {
        String[] results = parameterMap.get(name);
        if (results == null || results.length <= 0) {
            return "";
        } else {
            return results[0];
        }
    }

    /**
     * 获取指定参数名的所有值的数组  如果参数重名后面的参数会覆盖前面的参数
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] results = parameterMap.get(name);
        if (results == null || results.length <= 0) {
            return new String[]{};
        } else {
            return results;
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public byte[] getContentAsByteArray() {
        return requestBody;
    }
}