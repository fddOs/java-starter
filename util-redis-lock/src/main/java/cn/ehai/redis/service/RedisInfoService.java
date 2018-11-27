package cn.ehai.redis.service;

import cn.ehai.redis.model.RedisInfo;

/**
 * redis 信息提供类
 * @author lixiao
 * @date 2018/11/27 09:58
 */
public interface RedisInfoService {

  /**
   * redis 连接信息  默认为空
   * @param
   * @return cn.ehai.redis.model.RedisInfo
   * @author lixiao
   * @date 2018/11/27 09:59
   */
  default  RedisInfo redisInfo() {return new RedisInfo();}
}
