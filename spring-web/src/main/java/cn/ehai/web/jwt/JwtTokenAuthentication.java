package cn.ehai.web.jwt;

import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.web.config.EhiHeaderReqWrapper;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import cn.ehai.common.utils.ProjectInfoUtils;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * jwt 添加token验证
 *
 * @author lixiao
 */
public class JwtTokenAuthentication {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 1L; // 1 days
    private static String SECRET = "AgQGCAoMDfASFAIEBggKDA4QETAdBAYICgwOE52UAgQ=";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";
    private static final String SYSTEM_NAME = ProjectInfoUtils.getProjectContext();
    private static final String JWT_USER_ID = "sub";
    private static final String HEADER_JWT_USER_ID="jwt-user-id";
    private static final String JWT_HEADER ="x-ehi-sign";

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
        try {
            verify(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void setJwtHeader(EhiHeaderReqWrapper request) {
        String jwt = getJWT(request);
        try {
            Claims claims = verify(jwt);
            request.putHeader(HEADER_JWT_USER_ID,String.valueOf(claims.get(JWT_USER_ID)));
        } catch (Exception e) {
            LoggerUtils.error(JwtTokenAuthentication.class, ExceptionUtils.getMessage(e));
        }
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
        String jwt=request.getHeader(JWT_HEADER);
        if(!StringUtils.isEmpty(jwt)){
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
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(SECRET))
                .parseClaimsJws(jwt).getBody();
    }
}
