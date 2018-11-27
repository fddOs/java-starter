package cn.ehai.redis;

import cn.ehai.redis.lock.DistributedLockService;
import org.apache.commons.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TODO
 *
 * @author lixiao
 * @date 2018/11/27 13:41
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = UtilRedisApplication.class)
public class RedisTest {

  //@Autowired DistributedLockService distributedLockService;
  //@Test
  //public void test(){
  //
  //   new Thread(new Runnable() {
  //     @Override public void run() {
  //       boolean isLock = distributedLockService.tryLock("1234566");
  //       System.out.print(isLock);
  //     }
  //   }).start();
  //  new Thread(new Runnable() {
  //    @Override public void run() {
  //      boolean isLock = distributedLockService.tryLock("1234566");
  //      System.out.print(isLock);
  //    }
  //  }).start();
  //  new Thread(new Runnable() {
  //    @Override public void run() {
  //      boolean isLock = distributedLockService.tryLock("1234566");
  //      System.out.print(isLock);
  //    }
  //  }).start();
  //  new Thread(new Runnable() {
  //    @Override public void run() {
  //      boolean isLock = distributedLockService.tryLock("1234566");
  //      System.out.print(isLock);
  //    }
  //  }).start();
  //  new Thread(new Runnable() {
  //    @Override public void run() {
  //      boolean isLock = distributedLockService.tryLock("1234566");
  //      System.out.print(isLock);
  //    }
  //  }).start();
  //
  //
  //}
}
