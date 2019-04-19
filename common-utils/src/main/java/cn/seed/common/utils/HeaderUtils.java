package cn.seed.common.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Description:Header处理工具类
 * @author:方典典
 * @time:2018/7/9 10:48
 */
public class HeaderUtils {
    /**
     * @Description:header转码 iso8859-1转utf-8
     * @params:[request]
     * @return:java.util.Map<java.lang.String,java.lang.String>
     * @exception:
     * @author: 方典典
     * @time:2018/7/9 10:58
     */
    public static Map<String, String> requestHeaderHandler(HttpServletRequest request) {
        Enumeration<String> headerNum = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (headerNum.hasMoreElements()) {
            String headerName = headerNum.nextElement();
            try {
                headers.put(headerName, new String(request.getHeader(headerName).getBytes("iso-8859-1"), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        return headers;
    }

    /**
     * @param response
     * @return java.util.Map<java.lang.String       ,       java.lang.String>
     * @Description:header转码 iso8859-1转utf-8
     * @exception:
     * @author: 方典典
     * @time:2018/11/16 9:32
     */
    public static Map<String, String> responseHeaderHandler(HttpServletResponse response) {
        Collection<String> headerNames = response.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        headerNames.forEach(headerName -> {
            try {
                headers.put(headerName, new String(response.getHeader(headerName).getBytes("iso-8859-1"), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                headers.put(headerName, response.getHeader(headerName));
            }
        });
        return headers;
    }

}
