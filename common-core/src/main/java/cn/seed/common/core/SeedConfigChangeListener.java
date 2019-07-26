package cn.seed.common.core;

import java.util.Set;

/**
 * @Description:SeedConfigChangeListener
 * @author:方典典
 * @time:2019/4/30 16:11
 */
public interface SeedConfigChangeListener {

    /**
     * 监听操作
     *
     * @param namespace
     * @param keys
     * @return void
     * @author 方典典
     * @time 2019/4/30 16:16
     */
    void onChange(String namespace, Set<String> keys);
}
