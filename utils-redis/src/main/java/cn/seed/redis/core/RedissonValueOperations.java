package cn.seed.redis.core;

import java.util.concurrent.TimeUnit;

/**
 * redis value 操作
 *
 * @author lixiao
 * @date 2019-12-13 10:28
 */
public interface RedissonValueOperations<K,V> {


    /**
     * 添加KEY
     * @param key
     * @param value
     * @return void
     * @author lixiao
     * @date 2019-12-16 09:45
     */
    void set(K key,V value);
    /**
     * 添加KEY
     * @param key
     * @param value
     * @return void
     * @author lixiao
     * @date 2019-12-16 09:45
     */
    void set(K key, V value, long timeout, TimeUnit unit);
    /**
     * 获取value
     * @return V
     * @author lixiao
     * @date 2019-12-16 09:45
     */
    V get(String key);
}