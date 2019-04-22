package cn.seed.rpc.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:Feign参数配置
 * @author:方典典
 * @time:2018/11/12 10:41
 */
@ConfigurationProperties(prefix = "feign")
public class FeignProperties {
    private Integer connectTimeoutMillis = 30000;
    private Integer readTimeoutMillis = 30000;

    public Integer getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(Integer connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public Integer getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(Integer readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }
}
