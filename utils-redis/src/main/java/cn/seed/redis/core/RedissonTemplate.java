package cn.seed.redis.core;

import java.util.concurrent.TimeUnit;
import org.redisson.api.BatchOptions;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * TODO
 *
 * @author lixiao
 * @date 2019-12-09 16:57
 */
@Component
public class RedissonTemplate<K,V> {

    @Autowired RedissonClient redissonClient;

    //@Autowired RedisTemplate<String,String> redisTemplate;



   public <T>  T execute( RedissonCallBack<T> action,boolean pipeline){
       Assert.notNull(redissonClient, "redisson not initialized; call afterPropertiesSet() before using it");
       Assert.notNull(action, "Callback object must not be null");
       //if(pipeline){
       //    redissonClient.createBatch(createBatchOptions());
       //}
       return action.doInRedis(redissonClient);

   }









   @Bean
    BatchOptions createBatchOptions(){
       return BatchOptions.defaults()
           // 指定执行模式
           //
           // ExecutionMode.REDIS_READ_ATOMIC - 所有命令缓存在Redis节点中，以原子性事务的方式执行。
           //
           // ExecutionMode.REDIS_WRITE_ATOMIC - 所有命令缓存在Redis节点中，以原子性事务的方式执行。
           //
           // ExecutionMode.IN_MEMORY - 所有命令缓存在Redisson本机内存中统一发送，但逐一执行（非事务）。默认模式。
           //
           // ExecutionMode.IN_MEMORY_ATOMIC - 所有命令缓存在Redisson本机内存中统一发送，并以原子性事务的方式执行。
           //
           .executionMode(BatchOptions.ExecutionMode.IN_MEMORY)
           // 告知Redis不用返回结果（可以减少网络用量）
           .skipResult()
           // 将写入操作同步到从节点
           // 同步到2个从节点，等待时间为1秒钟
           .syncSlaves(2, 1, TimeUnit.SECONDS)
           // 处理结果超时为2秒钟
           .responseTimeout(1, TimeUnit.SECONDS)
           // 命令重试等待间隔时间为2秒钟
           .retryInterval(1, TimeUnit.SECONDS)
           // 命令重试次数。仅适用于未发送成功的命令
           .retryAttempts(1);
    }

}
