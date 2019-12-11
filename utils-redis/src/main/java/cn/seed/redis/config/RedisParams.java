package cn.seed.redis.config;

import cn.seed.common.core.ConfigCenterWrapper;
import cn.seed.common.utils.ProjectInfoUtils;
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

    private final static String REDIS_KEY = "redis.optional.url";
    private final static String REDIS_PASS_WORD = "redis.optional.password";

    //@Value("${redis.optional.url}")
    private String redisUrl;

    public RedisParams(){
    }

    /**
     *  获取redis连接字符串
     * @return java.lang.String
     * @author lixiao
     * @date 2019-12-09 13:58
     */
    //public static String  getRedisUrl(){
    //    return ConfigCenterWrapper.get(ProjectInfoUtils.PROJECT_APOLLO_COMMON_NAMESPACE,REDIS_KEY,"");
    //}
    public  String  getRedisUrl(){
        return redisUrl;
    }

    public void setRedisUrl(String redisUrl) {
        this.redisUrl = redisUrl;
    }

    /**
     *  获取redis连接密码
     * @return java.lang.String
     * @author lixiao
     * @date 2019-12-09 13:58
     */
    public static String  getRedisPassword(){
        return ConfigCenterWrapper.get(ProjectInfoUtils.PROJECT_APOLLO_COMMON_NAMESPACE,REDIS_PASS_WORD,"");
    }
}
