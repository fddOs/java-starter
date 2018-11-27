package cn.ehai.redis.lock;

/**
 *  分布式锁回调接口
 * @author lixiao
 * @date 2018/11/26 20:08
 */
public interface DistributedLockCallback<T> {
    /**
     * 调用者必须在此方法中实现需要加分布式锁的业务逻辑
     *
     * @return
     */
     T process();

    /**
     * 得到分布式锁名称
     *
     * @return
     */
     String getLockName();
}
