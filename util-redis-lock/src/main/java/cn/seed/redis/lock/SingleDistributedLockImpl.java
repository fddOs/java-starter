package cn.seed.redis.lock;

import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.LoggerUtils;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
        //是否获取到锁
        boolean isLock = false;
        //是否redis发生异常
        boolean isRedisException = false;
        RLock lock =getLock(redisson, callback.getLockName(), fairLock);
        try {

            if(lock!=null){
                lock.lock(leaseTime, timeUnit);
            }
        }catch (Exception e){
            LoggerUtils.error(SingleDistributedLockImpl.class,new Object[]{"Redis lock 异常"},e);
        }finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        try {
            return callback.process();
        } catch (Exception e){
            throw new ServiceException(ResultCode.FAIL,e.getMessage(),e);
        }finally {
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
        //是否获取到锁
        boolean isLock = false;
        //是否redis发生异常
        boolean isRedisException = false;

        RLock lock = getLock(redisson, callback.getLockName(), fairLock);
        try {
            isLock= (lock != null&&lock.tryLock(waitTime, leaseTime, timeUnit));
        }catch (Exception e){
            isRedisException =true;
        }
        try {
            // 如果获取redis锁异常 或者 成功获取锁，直接执行目标方法
            if(isLock||isRedisException){
                return callback.process();
            }
        }catch (Exception e) {
            throw new ServiceException(ResultCode.FAIL,e.getMessage(),e);
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        throw new ServiceException(ResultCode.FAIL,"获取Redis锁失败");
    }

    @Override
    public Boolean tryLock(String lockName) {
        return tryLock(lockName,DEFAULT_TIMEOUT,true);
    }

    @Override public Boolean tryLock(String lockName, long timeOut, boolean fairLock) {
        RLock lock = getLock(redisson, lockName, fairLock);
        try {
            if(lock!=null){
                return lock.tryLock(DEFAULT_WAIT_TIME, timeOut, DEFAULT_TIME_UNIT);
            }
        } catch (Exception e) {
            LoggerUtils.error(getClass(), new Object[]{lockName+"redis 异常"}, e);
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
        RLock lock = null;
        try{
            if (fairLock) {
                lock = redisson.getFairLock(lockName);
            } else {
                lock = redisson.getLock(lockName);
            }
        }catch (Exception e){
            LoggerUtils.error(SingleDistributedLockImpl.class, new Object[]{lockName+"redis 异常"}, e);
        }
        return lock;
    }

}
