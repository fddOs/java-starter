package cn.seed.redis.model;

import org.springframework.context.annotation.Bean;

/**
 * TODO
 *
 * @author lixiao
 * @date 2018/11/27 09:59
 */
public class RedisInfo {
  /**
   *redis 集群信息
   */
   private String redisClusterUrl="";

  public String getRedisClusterUrl() {
    return redisClusterUrl;
  }

  public void setRedisClusterUrl(String redisClusterUrl) {
    this.redisClusterUrl = redisClusterUrl;
  }
}
