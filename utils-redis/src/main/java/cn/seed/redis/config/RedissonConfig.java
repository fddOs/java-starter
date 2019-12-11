package cn.seed.redis.config;

import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.LoggerUtils;
import cn.seed.common.utils.ProjectInfoUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author lixiao
 * @date 2019-12-04 15:27
 */
@Configuration
public class RedissonConfig {

    @Autowired RedisParams redisParams;

    @Bean(destroyMethod = "shutdown")
    @RefreshScope
    RedissonClient redissonClient() {
        if(StringUtils.isEmpty(redisParams.getRedisUrl())){
            throw new ServiceException(ResultCode.FAIL,"redis 配置错误");
        }
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers()
            // 集群状态扫描间隔时间，单位是毫秒
            .setScanInterval(100)
            .setTimeout(1000)
            .setPingTimeout(1500)
            //失败重试次数
            .setRetryAttempts(1);
        String redisClusterUrl = redisParams.getRedisUrl();
        String[] serverArray = redisClusterUrl.split(",");
        if(null!=serverArray){
            for (String ipPort : serverArray) {
                if(!StringUtils.isEmpty(ipPort)){
                    clusterServersConfig.addNodeAddress("redis://"+ipPort);
                }
            }
        }

        if(!StringUtils.isEmpty(RedisParams.getRedisPassword())){
            clusterServersConfig.setPassword(RedisParams.getRedisPassword());
        }
        RedissonClient redissonClient=null;
        try{
            redissonClient = Redisson.create(config);
        }catch (Exception e){
            String errorMsg = ProjectInfoUtils.BASE_PACKAGE+"redis 连接失败"+redisParams.getRedisUrl();
            LoggerUtils.error(RedissonConfig.class,new Object[]{errorMsg },e);
            throw new ServiceException(ResultCode.FAIL,errorMsg);
        }
        return redissonClient;
    }
}
