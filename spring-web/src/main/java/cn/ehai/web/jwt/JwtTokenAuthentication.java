package cn.ehai.web.jwt;

import cn.ehai.common.core.SpringContext;
import cn.ehai.common.utils.ProjectInfoUtils;
import cn.ehai.web.config.EhiHeaderReqWrapper;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

/**
 * jwt 添加token验证
 *
 * @author lixiao
 */
public class JwtTokenAuthentication {
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 1L;
    private static String SECRET = "AgQGCAoMDfASFAIEBggKDA4QETAdBAYICgwOE52UAgQ=";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";
    private static final String SYSTEM_NAME = ProjectInfoUtils.getProjectContext();
    private static final String JWT_USER_ID = "sub";
    private static final String HEADER_JWT_USER_ID = "jwt-user-id";
    private static final String JWT_HEADER = "x-ehi-sign";

    public static String addAuthentication(HttpServletResponse res, String userId) {
        String jwt = Jwts.builder().setSubject(SYSTEM_NAME).claim(JWT_USER_ID, userId)
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
     * 解析JWT添加到请求头上
     *
     * @param request
     * @return void
     * @author lixiao
     * @date 2018-12-24 16:46
     */
    public static void setJwtHeader(EhiHeaderReqWrapper request) {
        String userCode = getUserCode(request);
        if (StringUtils.isEmpty(userCode)) {
            request.putHeader(HEADER_JWT_USER_ID, userCode);
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
        if (claims != null) {
            return claims.get(JWT_USER_ID).toString();
        }
        return null;
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
            return null;
        }
    }
}
