package cn.ehai.common.utils;

import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * TODO
 *
 * @author lixiao
 * @date 2018/12/15 15:14
 */
public class IOUtils {

    /**
     * 获取请求藜麦的body
     * @param reader
     * @return java.lang.String
     * @author lixiao
     * @date 2018/12/15 15:16
     */
    public static String readerToString(HttpServletRequest reader){
        String body=null;
        try {
            body =reader.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            LoggerUtils.error(IOUtils.class, ExceptionUtils.getStackTrace(e));
        }
        return body;
    }

    public static  String getResponseBody(byte[] buf) {
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

}
