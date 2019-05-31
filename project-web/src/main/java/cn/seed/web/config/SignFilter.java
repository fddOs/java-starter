package cn.seed.web.config;

import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.IOUtils;
import cn.seed.common.utils.LoggerUtils;
import cn.seed.common.utils.RequestInfoUtils;
import cn.seed.common.utils.SignUtils;
import cn.seed.web.common.ExcludePathHandler;
import cn.seed.web.common.SignConfig;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.springframework.http.HttpMethod.GET;

/**
 * 签名过滤器
 *
 * @author 方典典
 * @time 2019/3/1 15:57
 */
@Order(Integer.MIN_VALUE + 2)
@Component
public class SignFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        boolean isSign = Boolean.valueOf(ApolloBaseConfig.getReqSign());
        if (RequestInfoUtils.contentTypeIsNotApplicationJson(request) || !isSign || ExcludePathHandler.contain
                (request, response, ApolloBaseConfig.getSignExcludePath())) {
            chain.doFilter(request, response);
        } else {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String requestBody = "";
            if (!GET.name().equals(httpServletRequest.getMethod())) {
                try {
                    requestBody = StreamUtils.copyToString(request.getInputStream(),
                            Charset.forName("UTF-8"));
                } catch (IOException e) {
                    LoggerUtils.error(getClass(), new Object[]{request}, e);
                    throw new ServiceException(ResultCode.UNAUTHORIZED, "body获取错误");
                }
            }
            if (!signRequest(httpServletRequest, requestBody, httpServletRequest.getQueryString())) {
                throw new ServiceException(ResultCode.UNAUTHORIZED, "签名错误");
            }
            chain.doFilter(request, response);
            try {
                String respStr = IOUtils.getResponseBody(((ContentCachingResponseWrapper) response).getContentAsByteArray());
                String resSign = SignUtils.signResponse(respStr);
                ((HttpServletResponse) response).setHeader("x-seed-sign", resSign);
            } catch (Exception e) {
                LoggerUtils.error(getClass(), new Object[]{request, response, chain}, e);
                throw new ServiceException(ResultCode.UNAUTHORIZED, "签名错误");
            }
        }
    }

    @Override
    public void destroy() {

    }

    private boolean signRequest(HttpServletRequest request, String requestBody, String query) {
        if (StringUtils.isEmpty(requestBody) && StringUtils.isEmpty(query)) {
            return true;
        }
        String sign = SignUtils.sign(query, requestBody);
        String signHeader = request.getHeader(SignConfig.SIGN_HEADER);
        if (StringUtils.isEmpty(signHeader) || StringUtils
                .isEmpty(sign)) {
            return false;
        }
        return signHeader.equalsIgnoreCase(sign);
    }
}
