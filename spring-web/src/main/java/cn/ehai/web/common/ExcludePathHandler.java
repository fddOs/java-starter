package cn.ehai.web.common;

import cn.ehai.common.core.ApolloBaseConfig;
import cn.ehai.common.core.ServiceException;
import cn.ehai.common.utils.EHIExceptionLogstashMarker;
import cn.ehai.common.utils.EHIExceptionMsgWrapper;
import cn.ehai.common.utils.LoggerUtils;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.AntPathMatcher;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:ExcludePathHandler
 * @author:方典典
 * @time:2019/2/14 17:19
 */
public class ExcludePathHandler {

    /**
     * ExcludePathHandler
     *
     * @param request
     * @param response
     * @param excludePath
     * @return boolean
     * @author 方典典
     * @time 2019/2/14 17:23
     */
    public static boolean contain(ServletRequest request, ServletResponse response, String excludePath) {
        String excludePaths = "/druid/**,/swagger-resources/**,/v2/**,/heartbeat" + excludePath;
        List<String> excludePathList = Arrays.asList(excludePaths.split(","));
        AntPathMatcher matcher = new AntPathMatcher();
        for (String path : excludePathList) {
            if (matcher.match(path, ((RequestFacade) request).getServletPath())) {
                return true;
            }
        }
        return false;
    }
}
