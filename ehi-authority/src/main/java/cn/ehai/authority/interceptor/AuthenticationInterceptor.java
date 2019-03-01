package cn.ehai.authority.interceptor;

import cn.ehai.authority.annotation.EHiAuthentication;
import cn.ehai.authority.http.api.AuthApi;
import cn.ehai.common.core.Result;
import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ServiceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 鉴权拦截器
 *
 * @author xianglong.chen
 * @time 2019/2/21 上午9:45
 */
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    private static final String HEADER_STRING = "Authorization";
    private static String SECRET = "AgQGCAoMDfASFAIEBggKDA4QETAdBAYICgwOE52UAgQ=";
    private static final String HEADER_JWT_USER_ID = "jwt-user-id";
    private static final String JWT_USER_ID = "sub";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private AuthApi authApi;

    public AuthenticationInterceptor(AuthApi authApi) {
        Objects.requireNonNull(authApi);
        this.authApi = authApi;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            EHiAuthentication eHiAuthentication = ((HandlerMethod) handler).getMethodAnnotation
                    (EHiAuthentication.class);
            if (eHiAuthentication != null) {
                String systemCode = eHiAuthentication.systemCode();
                String moduleId = eHiAuthentication.moduleId();
                String userCode = getUserCode(request);
                if (StringUtils.isEmpty(userCode)) {
                    throw new ServiceException(ResultCode.BAD_REQUEST, "未获取到登录用户信息，请求失败");
                }
                Result<Boolean> booleanResult = authApi.verifyAuth(userCode, systemCode, moduleId);
                if (booleanResult != null && booleanResult.getErrorCode() == 200 && booleanResult.getResult() == true) {
                    return true;
                } else {
                    logger.debug("userCode[{}],systemCode[{}],moduleId[{}] 权限验证失败",userCode,systemCode,moduleId);
                    throw new ServiceException(ResultCode.BAD_REQUEST, "权限验证失败");
                }
            }
        }
        return true;
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
