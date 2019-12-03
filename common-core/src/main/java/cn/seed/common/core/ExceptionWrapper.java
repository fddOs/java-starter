package cn.seed.common.core;

import cn.seed.common.utils.LoggerUtils;

import java.util.function.Supplier;

/**
 * 封装异常处理
 *
 * @author:方典典
 * @time:2019/5/5 9:14
 */
public class ExceptionWrapper {

    public interface Consumer {
        /**
         * doSomething
         *
         * @return void
         * @author 方典典
         * @time 2019/9/12 10:17
         */
        void accept();
    }

    /**
     * 执行带返回值的目标方法 在异常时抛出指定的异常信息
     *
     * @param errorMsg
     * @param wrapper
     * @return T
     * @author 方典典
     * @time 2019/5/5 10:17
     */
    public static <T> T executeWrapper(String errorMsg, Supplier<T> wrapper) {
        try {
            return wrapper.get();
        } catch (Exception e) {
            LoggerUtils.error(ExceptionWrapper.class, new Object[]{errorMsg, wrapper}, e);
            throw new ServiceException(ResultCode.FAIL, errorMsg, e);
        }
    }

    /**
     * 执行带返回值的目标方法 在异常时抛出指定的异常信息 并可实现finally中的操作
     *
     * @param errorMsg
     * @param supplier
     * @param consumer
     * @return T
     * @author 方典典
     * @time 2019/11/27 1 0:15
     */
    public static <T> T executeWrapper(String errorMsg, Supplier<T> supplier, Consumer consumer) {
        try {
            return supplier.get();
        } catch (Exception e) {
            LoggerUtils.error(ExceptionWrapper.class, new Object[]{errorMsg, supplier}, e);
            throw new ServiceException(ResultCode.FAIL, errorMsg, e);
        } finally {
            consumer.accept();
        }
    }

    /**
     * 执行无返回值的目标方法 在异常时抛出指定的异常信息 并可实现finally中的操作
     *
     * @param errorMsg
     * @param consumer
     * @return void
     * @author 方典典
     * @time 2019/5/5 16:16
     */
    public static void executeWrapper(String errorMsg, Consumer consumer) {
        try {
            consumer.accept();
        } catch (Exception e) {
            LoggerUtils.error(ExceptionWrapper.class, new Object[]{errorMsg, consumer}, e);
            throw new ServiceException(ResultCode.FAIL, errorMsg, e);
        }
    }

    /**
     * 执行无返回值的目标方法 在异常时抛出指定的异常信息
     *
     * @param errorMsg
     * @param consumer
     * @param consumerFinally
     * @return void
     * @author 方典典
     * @time 2019/11/27 10:29
     */
    public static void executeWrapper(String errorMsg, Consumer consumer, Consumer consumerFinally) {
        try {
            consumer.accept();
        } catch (Exception e) {
            LoggerUtils.error(ExceptionWrapper.class, new Object[]{errorMsg, consumer}, e);
            throw new ServiceException(ResultCode.FAIL, errorMsg, e);
        } finally {
            consumerFinally.accept();
        }
    }

}
