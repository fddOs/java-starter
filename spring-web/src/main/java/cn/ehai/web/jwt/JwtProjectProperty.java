package cn.ehai.web.jwt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * jwt配置
 *
 * @author lixiao
 * @date 2018-12-24 16:33
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProjectProperty {

    /**
     * jwt拦截器不拦截的url 默认 druid、swagger、heartbeat
     *
     * @author lixiao
     * @date 2018-12-24 16:37
     */
    @Value("interceptor-url")
    private String interceptorUrl;
    @Value("${login-url:}")
    private String loginUrl;

    private List<String> url = new ArrayList<>();

    public String getInterceptorUrl() {
        return interceptorUrl;
    }

    public void setInterceptorUrl(String interceptorUrl) {
        this.interceptorUrl = interceptorUrl;
        if (!StringUtils.isEmpty(interceptorUrl)) {
            String[] strings = interceptorUrl.split(",");
            if (null != strings && strings.length > 0) {
                for (String s : strings) {
                    url.add(s);
                }
            }
        }
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }
}
