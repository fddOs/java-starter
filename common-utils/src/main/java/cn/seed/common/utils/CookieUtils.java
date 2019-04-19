package cn.seed.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * cookie操作类
 *
 * @Description:TODO
 * @author:lixiao
 * @time:2018年1月31日 下午1:46:20
 */
public class CookieUtils {

    private static final String COOKIE_KEY = "!@Hd124&^$fg#F";

    /**
     * 设置cookie
     *
     * @param response
     * @param name     cookie名字
     * @param value    cookie值
     * @param maxAge   cookie生命周期 以秒为单位
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        try {
            if (!StringUtils.isBlank(value)) {
                value = AESUtils.aesEncryptString(value, COOKIE_KEY);
            }
            Cookie cookie = new Cookie(name, value);
            if (maxAge > 0) {
                cookie.setMaxAge(maxAge);
            }
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception ex) {
            LoggerUtils.error(CookieUtils.class, new Object[]{response, name, value, maxAge}, ex);
        }
    }

    /**
     * 设置cookie 当前会话有效
     *
     * @param response
     * @param name
     * @param value    void
     * @Description:TODO
     * @exception:
     * @author: lixiao
     * @time:2018年2月1日 上午9:11:54
     */
    public static void addCookie(HttpServletResponse response, String name, String value) {
        addCookie(response, name, value, 0);
    }

    /**
     * 清空Cookie操作 clearCookie
     *
     * @param request
     * @param response
     * @return boolean
     */
    public static boolean clearCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        boolean bool = false;
        Cookie[] cookies = request.getCookies();
        if (null == cookies || cookies.length == 0) {
            return bool;
        }
        try {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = new Cookie(name, null);
                cookie.setMaxAge(0);
                // 根据你创建cookie的路径进行填写
                cookie.setPath("/");
                response.addCookie(cookie);
                bool = true;
            }
        } catch (Exception ex) {
            LoggerUtils.error(CookieUtils.class, new Object[]{request, response, name}, ex);
        }
        return bool;
    }

    /**
     * 清空Cookie操作 clearCookie
     *
     * @param request
     * @param response
     * @return boolean
     */
    public static boolean clearCookie(HttpServletRequest request, HttpServletResponse response, String name,
                                      String domain) {
        boolean bool = false;
        Cookie[] cookies = request.getCookies();
        if (null == cookies || cookies.length == 0) {
            return bool;
        }
        try {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = new Cookie(name, null);
                cookie.setMaxAge(0);
                // 根据你创建cookie的路径进行填写
                cookie.setPath("/");
                cookie.setDomain(domain);
                response.addCookie(cookie);
                bool = true;
            }
        } catch (Exception ex) {
            LoggerUtils.error(CookieUtils.class, new Object[]{request, response, name, domain}, ex);
        }
        return bool;
    }

    /**
     * 获取指定cookies的值 加密
     *
     * @param request
     * @param name
     * @return String
     */
    public static String findCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies || cookies.length == 0) {
            return null;
        }
        String string = null;
        try {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                String cname = cookie.getName();
                if (!StringUtils.isBlank(cname) && cname.equals(name)) {
                    //if (!StringUtils.isBlank(string)) {
                    string = AESUtils.aesDecryptString(cookie.getValue(), COOKIE_KEY);
                    //}
                }

            }
        } catch (Exception ex) {
            LoggerUtils.error(CookieUtils.class, new Object[]{request, name}, ex);
        }
        return string;
    }

    /**
     * 获取指定cookies的值  未加密
     *
     * @param request
     * @param name
     * @return String
     * @Description:TODO
     * @exception:
     * @author: lixiao
     * @time:2018年2月1日 上午9:18:37
     */
    public static String findExternalCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies || cookies.length == 0) {
            return null;
        }
        String string = null;
        try {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                String cname = cookie.getName();
                if (!StringUtils.isBlank(cname) && cname.equals(name)) {
                    string = cookie.getValue();
                }

            }
        } catch (Exception ex) {
            LoggerUtils.error(CookieUtils.class, new Object[]{request, name}, ex);
        }
        return string;
    }

}
