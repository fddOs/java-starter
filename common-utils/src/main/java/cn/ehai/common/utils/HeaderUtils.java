package cn.ehai.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
    public static Map<String, String> headerHandler(HttpServletRequest request) {
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

}
