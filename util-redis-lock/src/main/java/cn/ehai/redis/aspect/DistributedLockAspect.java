package cn.ehai.redis.aspect;

import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.redis.annotation.DistributedLock;
import cn.ehai.redis.lock.DistributedLockCallback;
import cn.ehai.redis.lock.DistributedLockService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 注解切面
 * @author lixiao
 * @date 2018/11/26 19:43
 */
@Aspect
@Component
public class DistributedLockAspect {

    @Autowired
    private DistributedLockService lockTemplate;

    @Pointcut("@annotation(cn.ehai.redis.annotation.DistributedLock)")
    public void DistributedLockAspect() {}

    @Around(value = "DistributedLockAspect()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        //得到使用注解的方法。可使用Method.getAnnotation(Class<T> annotationClass)获取指定的注解，然后可获得注解的属性
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Object[] arguments = pjp.getArgs();
        String[] params = ((MethodSignature) pjp.getSignature()).getParameterNames();
        final String lockName = getLockName(method, arguments,params);
        return lock(pjp, method, lockName);
    }


    /**
     * 获取锁名称
     * @param method
     * @param args
     * @return java.lang.String
     * @author lixiao
     * @date 2018-12-27 09:22
     */
    private String getLockName(Method method, Object[] args,String[] params) {
        Objects.requireNonNull(method);
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);

        String lockName = annotation.lockName(),param = annotation.param();

        if (isEmpty(lockName)) {
            if (args.length > 0) {
                if (isNotEmpty(param)) {
                    Object arg;
                    String selectParam;
                    if (annotation.argNum() > 0 && annotation.argNum()<=args.length) {
                        arg = args[annotation.argNum() - 1];
                        selectParam=params[annotation.argNum() - 1];
                    } else {
                        arg = args[0];
                        selectParam=params[0];
                    }
                    if(isParamType(arg)&&selectParam.equals(param)){
                        lockName = String.valueOf(arg);
                    }else{
                        lockName = String.valueOf(getParam(arg, param));
                    }
                } else if (annotation.argNum() > 0) {
                    lockName = args[annotation.argNum() - 1].toString();
                }
            }
        }

        if (isNotEmpty(lockName)) {
            String preLockName = annotation.lockNamePre(),
                    postLockName = annotation.lockNamePost(),
                    separator = annotation.separator();

            StringBuilder lName = new StringBuilder();
            if (isNotEmpty(preLockName)) {
                lName.append(preLockName).append(separator);
            }
            lName.append(lockName);
            if (isNotEmpty(postLockName)) {
                lName.append(separator).append(postLockName);
            }

            lockName = lName.toString();

            return lockName;
        }

        throw new IllegalArgumentException("Can't get or generate lockName accurately!");
    }

    /**
     * 判断参数值是否可以直接获取
     * @param arg
     * @return boolean
     * @author lixiao
     * @date 2018-12-27 09:42
     */
     private boolean isParamType(Object arg){
         boolean isType = false;
         try {
             isType = arg instanceof String||arg instanceof Integer ||arg instanceof Byte
                 ||arg instanceof Short ||arg instanceof Long||arg instanceof Character||
                 ((Class)arg.getClass().getField("TYPE").get(null)).isPrimitive();
         } catch (IllegalAccessException e) {
             LoggerUtils.error(DistributedLockAspect.class, ExceptionUtils.getStackTrace(e));
             isType = false;
         } catch (NoSuchFieldException e) {
             LoggerUtils.error(DistributedLockAspect.class, ExceptionUtils.getStackTrace(e));
             isType = false;
         }

         return isType;
     }

    /**
     * 从方法参数获取数据
     *
     * @param param
     * @param arg 方法的参数数组
     * @return
     */
    private Object getParam(Object arg, String param) {

        if (isNotEmpty(param) && arg != null) {
            try {
                Object result = PropertyUtils.getProperty(arg, param);
                return result;
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(arg + "没有属性" + param + "或未实现get方法。", e);
            } catch (Exception e) {
                throw new RuntimeException("", e);
            }
        }
        throw new IllegalArgumentException("");
    }

    private Object lock(ProceedingJoinPoint pjp, Method method, final String lockName) {

        DistributedLock annotation = method.getAnnotation(DistributedLock.class);

        boolean fairLock = annotation.fairLock();

        boolean tryLock = annotation.tryLock();

        if (tryLock) {
            return tryLock(pjp, annotation, lockName, fairLock);
        } else {
            return lock(pjp,lockName, fairLock);
        }
    }

    private Object lock(ProceedingJoinPoint pjp, final String lockName, boolean fairLock) {
        return lockTemplate.lock(new DistributedLockCallback<Object>() {
            @Override
            public Object process() {
                return proceed(pjp);
            }

            @Override
            public String getLockName() {
                return lockName;
            }
        }, fairLock);
    }

    private Object tryLock(ProceedingJoinPoint pjp, DistributedLock annotation, final String lockName, boolean fairLock) {

        long waitTime = annotation.waitTime(),
                leaseTime = annotation.leaseTime();
        TimeUnit timeUnit = annotation.timeUnit();

        return lockTemplate.tryLock(new DistributedLockCallback<Object>() {
            @Override
            public Object process() {
                return proceed(pjp);
            }

            @Override
            public String getLockName() {
                return lockName;
            }
        }, waitTime, leaseTime, timeUnit, fairLock);
    }

    private Object proceed(ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    private boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }
}
