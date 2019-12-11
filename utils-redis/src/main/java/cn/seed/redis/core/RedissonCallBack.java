package cn.seed.redis.core;

import javax.validation.constraints.NotNull;
import org.redisson.api.RedissonClient;

/**
 *
 * redis 操作回调接口
 * @author lixiao
 * @date 2019-12-10 10:38
 */
public interface RedissonCallBack<T> {

    @NotNull
    T doInRedis(RedissonClient redissonClient);
}
