package cn.seed.redis.core;

/**
 * redis 基本操作
 * @author lixiao
 * @date 2019-12-13 14:30
 */
public interface RedissonOperations<K,V> {

    /**
     *  执行方法
     * @author lixiao
     * @date 2019-12-13 14:30
     */
     <T>  T execute( RedissonCallBack<T> action,boolean pipeline);

     <T>  T execute( RedissonCallBack<T> action);

    /**
     * delete key
     * @return boolean
     * @author lixiao
     * @date 2019-12-16 09:16
     */
     Boolean deleteKey(String key);

     /**
      * delete key
      * @return long
      * @author lixiao
      * @date 2019-12-16 09:16
      */
     long deleteKey(String...key);

     /**
      * 判断key是否存在
      * @return java.lang.Boolean
      * @author lixiao
      * @date 2019-12-16 09:15
      */
     Boolean hasKey(String key);

     /**
      *  操作字符串类型
      * @return org.springframework.data.redis.core.RedissonValueOperations<K,V>
      * @author lixiao
      * @date 2019-12-16 09:15
      */
     RedissonValueOperations<K, V> opsForValue();
}
