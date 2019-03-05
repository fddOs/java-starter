package cn.ehai.redis.config;

import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.redis.lock.DistributedLockService;
import cn.ehai.redis.lock.SingleDistributedLockImpl;
import cn.ehai.redis.model.RedisInfo;
import cn.ehai.redis.service.RedisInfoService;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 *
 *
 * @author lixiao
 * @date 2018/11/26 20:22
 */
@Configuration
public class RedissonConfig {

  @Autowired
  RedisInfoService redisInfoService;

  @Bean(destroyMethod = "shutdown")
  RedissonClient redissonClient() {
    Config config = new Config();
    if (redisInfoService != null) {
      ClusterServersConfig clusterServersConfig = config.useClusterServers()
          // 集群状态扫描间隔时间，单位是毫秒
          .setScanInterval(500);
      String redisClusterUrl = redisInfoService.redisInfo().getRedisClusterUrl();
      if(StringUtils.isEmpty(redisClusterUrl)){
        return null;
      }
      String[] serverArray = redisClusterUrl.split(",");
     if(null!=serverArray){
       for (String ipPort : serverArray) {
         if(!StringUtils.isEmpty(ipPort)){
           clusterServersConfig.addNodeAddress("redis://"+ipPort);
         }
       }
     }
    }else{
      throw  new IllegalArgumentException("需要配置redis连接信息");
    }
    RedissonClient redissonClient=null;
    try{
      redissonClient = Redisson.create(config);
    }catch (Exception e){
      LoggerUtils.error(RedissonConfig.class,new Object[]{
          redisInfoService.redisInfo().getRedisClusterUrl()+"redis 连接失败"},e);
    }
    return redissonClient;
  }

}
