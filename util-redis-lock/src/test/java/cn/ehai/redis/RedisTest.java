package cn.ehai.redis;

import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.redis.annotation.DistributedLock;
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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UtilRedisApplication.class)
public class RedisTest {

  //@Autowired DistributedLockService distributedLockService;
  @Test
  public void test(){

     new Thread(new Runnable() {
       @Override public void run() {
           testLock("123456");
       }
     }).start();
    new Thread(new Runnable() {
      @Override public void run() {
          testLock("123456");
      }
    }).start();
    new Thread(new Runnable() {
      @Override public void run() {
          testLock("123456");
       }
   }).start();
    new Thread(new Runnable() {
      @Override public void run() {
          testLock("123456");
      }
    }).start();
    new Thread(new Runnable() {
      @Override public void run() {
          testLock("123456");
      }
    }).start();

  }

  @DistributedLock(param = "userId",argNum = 1)
  private void testLock(String userId){
      LoggerUtils.error(RedisTest.class,userId+Thread.currentThread().getName());
  }
}
