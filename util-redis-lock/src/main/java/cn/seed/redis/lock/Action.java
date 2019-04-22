package cn.seed.redis.lock;

@FunctionalInterface
public interface Action {
    void action();
}
