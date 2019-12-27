package cn.seed.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * redis 参数配置
 *
 * @author lixiao
 * @date 2019-12-09 13:43
 */
@Configuration
@ConfigurationProperties
public class RedisParams {

    private String redisUrl;
    private String redisPassword;

    public RedisParams(){
    }

    public  String  getRedisUrl(){
        return redisUrl;
    }

    public void setRedisUrl(String redisUrl) {
        this.redisUrl = redisUrl;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public void setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
    }
}
