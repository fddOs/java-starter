package cn.seed.redis.core;

import java.util.concurrent.TimeUnit;
import org.redisson.api.RBucket;

/**
 * 默认的简单对象实现
 *
 * @author lixiao
 * @date 2019-12-16 09:17
 */
public class DefaultValueOperations<K,V> extends AbstractOperations<K,V> implements
    RedissonValueOperations<K,V> {

    DefaultValueOperations(RedissonTemplate<K, V> template) {
        super(template);
    }

    @Override public void set(K key, V value) {
        set(key,value,60,TimeUnit.SECONDS);
    }

    @Override public void set(K key, V value, long timeout, TimeUnit unit) {
        execute(conn->{
            RBucket<V> rBucket = conn.getBucket((String)key);
            rBucket.set(value,timeout,unit);
            return null;
            },false);
    }

    @Override public V get(String key) {
        return execute(conn->{RBucket<V> rBucket = conn.getBucket(key);return rBucket.get();},false);
    }
}
