package cn.seed.redis.lock;

import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.LoggerUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SingleDistributedLockImpl implements DistributedLockService {
    @Autowired
    private RedissonClient redisson;

    @Override
    public <T> T lock(DistributedLockCallback<T> callback, boolean fairLock) {
        return lock(callback, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT, fairLock);
    }

    @Override
    public <T> T lock(DistributedLockCallback<T> callback, long leaseTime, TimeUnit timeUnit, boolean fairLock) {
        RLock lock = getLock(redisson, callback.getLockName(), fairLock);
        try {
            lock.lock(leaseTime, timeUnit);
            return callback.process();
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public <T> T tryLock(DistributedLockCallback<T> callback, boolean fairLock) {
        return tryLock(callback, DEFAULT_WAIT_TIME, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT, fairLock);
    }

    @Override
    public <T> T tryLock(DistributedLockCallback<T> callback,
                         long waitTime,
                         long leaseTime,
                         TimeUnit timeUnit,
                         boolean fairLock) {
        RLock lock = getLock(redisson, callback.getLockName(), fairLock);
        try {
            if (lock.tryLock(waitTime, leaseTime, timeUnit)) {
                return callback.process();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        throw new ServiceException(ResultCode.REDIS_ERROR, "redis请求锁失败");
    }

    @Override
    public Boolean tryLock(String lockName) {
        return tryLock(lockName,DEFAULT_TIMEOUT,true);
    }

    @Override public Boolean tryLock(String lockName, long timeOut, boolean fairLock) {
        RLock lock = getLock(redisson, lockName, fairLock);
        try {
            return lock.tryLock(DEFAULT_WAIT_TIME, timeOut, DEFAULT_TIME_UNIT);
        } catch (Exception e) {
            LoggerUtils.error(getClass(), new Object[]{lockName}, e);
        }
        return true;
    }

    @Override
    public void unLock(String lockName) {
        RLock lock = getLock(redisson, lockName, true);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    private static RLock getLock(RedissonClient redisson, String lockName, boolean fairLock) {
        RLock lock;
        if (fairLock) {
            lock = redisson.getFairLock(lockName);
        } else {
            lock = redisson.getLock(lockName);
        }
        return lock;
    }

}
