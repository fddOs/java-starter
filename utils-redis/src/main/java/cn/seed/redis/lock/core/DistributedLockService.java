package cn.seed.redis.lock.core;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁操作模板
 * @author lixiao
 * @date 2018/11/26 19:42
 */
public interface DistributedLockService {

    /***
     * trylock 默认等待时间为0
     */
    long DEFAULT_WAIT_TIME = 0;
    /***
     * 默认超时时间为0
     */
    long DEFAULT_TIMEOUT   = 5;
    TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * 使用分布式锁，使用锁默认超时时间。
     * @param callback
     * @param fairLock 是否使用公平锁
     * @return
     */
    <T> T lock(DistributedLockCallback<T> callback, boolean fairLock);

    /**
     * 使用分布式锁。自定义锁的超时时间
     *
     * @param callback
     * @param leaseTime 锁超时时间。超时后自动释放锁。
     * @param timeUnit
     * @param fairLock 是否使用公平锁
     * @param <T>
     * @return
     */
    <T> T lock(DistributedLockCallback<T> callback, long leaseTime, TimeUnit timeUnit,
        boolean fairLock);

    /**
     * 尝试分布式锁，使用锁默认等待时间、超时时间。
     * @param callback
     * @param fairLock 是否使用公平锁
     * @param <T>
     * @return
     */
    <T> T tryLock(DistributedLockCallback<T> callback, boolean fairLock);

    /**
     * 尝试分布式锁，自定义等待时间、超时时间。
     * @param callback
     * @param waitTime 获取锁最长等待时间
     * @param leaseTime 锁超时时间。超时后自动释放锁。
     * @param timeUnit
     * @param fairLock 是否使用公平锁
     * @param <T>
     * @return
     */
    <T> T tryLock(DistributedLockCallback<T> callback, long waitTime, long leaseTime,
        TimeUnit timeUnit, boolean fairLock);

    /**
     * 尝试分布式锁，使用锁默认等待时间、超时 使用公平锁
     * @param lockName
     * @return java.lang.Boolean
     * @author lixiao
     * @date 2018/12/10 16:56
     */
    Boolean tryLock(String lockName);

    /**
     * 尝试分布式锁，使用锁默认等待时间、超时 使用公平锁
     * @param lockName
     * @return java.lang.Boolean
     * @author lixiao
     * @date 2018/12/10 16:56
     */
    Boolean tryLock(String lockName, long waitTime, boolean fairLock);

    /**
     * 手动解锁
     * @param lockName
     * @return void
     * @author lixiao
     * @date 2018/12/10 16:56
     */
    void unLock(String lockName);
}
