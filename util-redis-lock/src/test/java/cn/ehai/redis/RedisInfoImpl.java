package cn.ehai.redis;

import cn.ehai.redis.model.RedisInfo;
import cn.ehai.redis.service.RedisInfoService;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author lixiao
 * @date 2018-12-27 09:49
 */
@Service
public class RedisInfoImpl implements RedisInfoService {

    @Override
   public RedisInfo redisInfo(){
        RedisInfo redisInfo = new RedisInfo();
        redisInfo.setRedisClusterUrl("192.168.5.55:6383,192.168.5.55:6384,192.168.5.56:6383,192.168.5.56:6384,192.168.9.227:6379,192.168.9.227:6380");
        return redisInfo;
    };
}
