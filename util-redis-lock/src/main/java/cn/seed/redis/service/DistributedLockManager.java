package cn.seed.redis.service;

import cn.seed.redis.annotation.DistributedLock;
import cn.seed.redis.lock.Action;

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
