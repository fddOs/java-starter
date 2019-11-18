package cn.seed.web.jwt;

import cn.seed.common.core.ConfigCenterWrapper;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.LoggerUtils;
import cn.seed.common.utils.ProjectInfoUtils;
import cn.seed.web.config.BaseHeaderReqWrapper;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.StringUtils;

/**
 * jwt 添加token验证
 *
 * @author lixiao
 */
public class JwtTokenAuthentication {
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 1L;
    private static String SECRET = ProjectInfoUtils.PROJECT_JWT_SECRET;
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";
    private static final String SYSTEM_NAME = ProjectInfoUtils.PROJECT_CONTEXT;
    private static final String JWT_USER_ID = "sub";
    private static final String JWT_USER_NAME = "sub-name";
    private static final String HEADER_JWT_USER_ID = "jwt-user-id";

    public static String addAuthentication(HttpServletResponse res, String userId) {
        String jwt = Jwts.builder().setSubject(SYSTEM_NAME).claim(JWT_USER_ID, userId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
        return jwt;
    }

    public static String addAuthentication(HttpServletResponse res, String userId, String userName) {
        String jwt = Jwts.builder().setSubject(SYSTEM_NAME).claim(JWT_USER_ID, userId).claim(JWT_USER_NAME, userName)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
        return jwt;
    }

    /**
     * @Description:验证登录
     * @params:[request]
     * @return:boolean
     * @exception:
     * @author: 方典典
     * @time:2018/7/17 11:13
     */
    public static boolean getAuthentication(HttpServletRequest request) {
        String jwt = getJWT(request);
        if (StringUtils.isEmpty(jwt)) {
            return false;
        }
        if (verify(jwt) != null) {
            return true;
        }
        return false;
    }


    /**
     * @param request
     * @return
     * @description 获取token的用户名和工号
     * @author weida
     */
    @Deprecated
    public static String getUserInfoCombination(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Stream<Cookie> streamCookies = Stream.of(cookies);
        Optional<Cookie> streamCookie = streamCookies.filter(cookie -> "elc".equals(cookie.getName())).findFirst();
        StringBuilder userName = new StringBuilder();
        if (streamCookie.isPresent()) {
            Cookie token = streamCookie.get();
            try {
                userName.append(Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
                        .parseClaimsJws(token.getValue())
                        .getBody()
                        .getSubject());
                userName.append("-");
                String name = (String) Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
                        .parseClaimsJws(token.getValue())
                        .getBody()
                        .get(ConfigCenterWrapper.getDefaultEmpty("uniqueName"));
                if (name == null) {
                    throw new ServiceException(ResultCode.UNAUTHORIZED, "用户姓名获取失败，请重新登录");
                }
                userName.append(name);

            } catch (JwtException e) {
                LoggerUtils.error(JwtTokenAuthentication.class, "解析用户信息失败");
                throw new ServiceException(ResultCode.UNAUTHORIZED, "用户信息获取失败");
            }
        }
        return userName.toString();
    }

    /**
     * @param request
     * @return
     * @description 获取token的用户名和工号
     * @author weida
     */
    public static String getUserName(HttpServletRequest request) {
        Claims claims = verify(getJWT(request));
        if (claims == null) {
            return null;
        }
        return (String) claims.get(ConfigCenterWrapper.get("uniqueName", ""));
    }

    /**
     * 解析JWT添加到请求头上
     *
     * @param request
     * @return void
     * @author lixiao
     * @date 2018-12-24 16:46
     */
    public static void setJwtHeader(BaseHeaderReqWrapper request) {
        String userCode = getUserCode(request);
        if (!StringUtils.isEmpty(userCode)) {
            request.putHeader(HEADER_JWT_USER_ID, userCode);
            request.setAttribute("oprNo", userCode);
        }
    }

    /**
     * 获取UserCode
     *
     * @param request
     * @return java.lang.String
     * @author 方典典
     * @time 2019/1/9 17:49
     */
    public static String getUserCode(HttpServletRequest request) {
        Claims claims = verify(getJWT(request));
        if (claims == null) {
            return null;
        }
        Object userCode = claims.get(JWT_USER_ID);
        return userCode == null ? "" : userCode.toString();
    }

    /**
     * 获取用户名称
     * (
     *      此方法获取的名称为
     *          addAuthentication(HttpServletResponse res, String userId, String userName)
     *      操作后保存的名称
     * )
     */
    public static String getJwtUserName(HttpServletRequest request) {
        Claims claims = verify(getJWT(request));
        if (claims == null) {
            return null;
        }
        Object userName = claims.get(JWT_USER_NAME);
        return userName == null? "" : userName.toString();
    }

    /**
     * @Description:获取JWT
     * @params:[request]
     * @return:java.lang.String
     * @exception:
     * @author: 方典典
     * @time:2018/7/17 11:15
     */
    public static String getJWT(HttpServletRequest request) {
        String jwt = request.getHeader(HEADER_STRING);
        if (!StringUtils.isEmpty(jwt)) {
            return jwt;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        Stream<Cookie> streamCookies = Stream.of(cookies);
        Optional<Cookie> streamCookie = streamCookies.filter(cookie -> "elc".equals(cookie.getName())).findFirst();
        if (streamCookie.isPresent()) {
            return streamCookie.get().getValue();
        }
        return jwt;
    }

    /**
     * @Description:验证jwt
     * @params:[jwt]
     * @return:io.jsonwebtoken.Claims
     * @exception:
     * @author: 方典典
     * @time:2018/7/17 11:10
     */
    public static Claims verify(String jwt) {
        try {
            return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
                    .parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            LoggerUtils.error(JwtTokenAuthentication.class, ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

}
