package cn.seed.authority.interceptor;

import cn.seed.authority.annotation.FunctionAuthVerify;
import cn.seed.authority.annotation.WebAuthentication;
import cn.seed.authority.service.WebAuthority;
import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.Result;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.web.jwt.JwtTokenAuthentication;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 鉴权拦截器
 *
 * @author xianglong.chen
 * @time 2019/2/21 上午9:45
 */
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    private WebAuthority webAuthority;

    public AuthenticationInterceptor(WebAuthority webAuthority) {
        Objects.requireNonNull(webAuthority);
        this.webAuthority = webAuthority;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {
        if (handler instanceof HandlerMethod) {
            WebAuthentication webAuthentication = ((HandlerMethod) handler).getMethodAnnotation
                    (WebAuthentication.class);
            FunctionAuthVerify functionAuthVerify = ((HandlerMethod) handler).getMethodAnnotation
                    (FunctionAuthVerify.class);
            if (webAuthentication != null || functionAuthVerify != null) {
                String userCode = JwtTokenAuthentication.getUserCode(request);
                if (StringUtils.isEmpty(userCode)) {
                    throw new ServiceException(ResultCode.BAD_REQUEST, "验证功能权限失败，用户工号为空");
                }
                // 获取系统编码，如果没有配置，那就取全局配置
                String systemCode;
                String[] moduleIds;
                if(functionAuthVerify !=null ){
                    systemCode = functionAuthVerify.systemCode();
                    moduleIds = functionAuthVerify.moduleIds();
                }else{
                    systemCode = webAuthentication.systemCode();
                    moduleIds = webAuthentication.moduleIds();
                }
                if (StringUtils.isEmpty(systemCode)) {
                    systemCode = ApolloBaseConfig.getSystemCode();
                }
                for (String moduleId : moduleIds) {
                    Result<Boolean> booleanResult = webAuthority.verifyAuth(userCode, systemCode, moduleId);
                    if (booleanResult == null || booleanResult.getErrorCode() != 0 || !booleanResult.getResult()) {
                        throw new ServiceException(ResultCode.UNAUTHORIZED, "操作失败：没有权限");
                    }
                }
            }
        }
        return true;
    }

}
