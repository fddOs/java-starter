package cn.ehai.redis.lock;

@FunctionalInterface
public interface Action {
    void action();
}
