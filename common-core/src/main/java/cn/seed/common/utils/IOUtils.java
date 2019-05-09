package cn.seed.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

/**
 * IOUtils
 *
 * @author lixiao
 * @date 2018/12/15 15:14
 */
public class IOUtils {

    /**
     * 获取请求藜麦的body
     *
     * @param reader
     * @return java.lang.String
     * @author lixiao
     * @date 2018/12/15 15:16
     */
    public static String readerToString(HttpServletRequest reader) {
        BufferedReader br;
        StringBuilder builder = new StringBuilder();
        try {
            br = reader.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                builder.append(str);
            }

        } catch (IOException e) {
            LoggerUtils.error(IOUtils.class, new Object[]{reader}, e);
        }
        return builder.toString();
    }

    public static String getResponseBody(byte[] buf) {
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
