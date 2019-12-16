package cn.seed.redis.core;

/**
 * redisson 操作静态类
 *
 * @author lixiao
 * @date 2019-12-12 16:12
 */
public abstract class AbstractOperations<K,V> {

    final RedissonTemplate<K,V> template;
    AbstractOperations(RedissonTemplate<K,V> template){this.template = template;}

    <T> T execute(RedissonCallBack<T> callback, boolean b) {
        return template.execute(callback, b);
    }
}
