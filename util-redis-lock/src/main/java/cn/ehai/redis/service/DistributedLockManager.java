package cn.ehai.redis.service;

import cn.ehai.redis.annotation.DistributedLock;
import cn.ehai.redis.lock.Action;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class DistributedLockManager {



    @DistributedLock(lockName = "lock", lockNamePost = ".lock")
    public int aspect(Supplier<Integer> supplier) {
        return supplier.get();
    }

    @DistributedLock(lockName = "lock", lockNamePost = ".lock")
    public void doSomething(Action action) {
        action.action();
    }
}
