package cn.seed.common.core;

import cn.seed.common.utils.LoggerUtils;

/**
 * @Description:ExceptionWrapper
 * @author:方典典
 * @time:2019/5/5 9:14
 */
public class ExceptionWrapper {

    public interface Consumer<T> {
        /**
         * doSomething
         *
         * @return void
         * @author 方典典
         * @time 2019/9/12 10:17
         */
        void execute();
    }

    public interface Supplier<T> {

        /**
         * do something Gets a result.
         *
         * @return T
         * @author 方典典
         * @time 2019/9/12 10:18
         */
        T execute();
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
            return wrapper.execute();
        } catch (Exception e) {
            LoggerUtils.error(ExceptionWrapper.class, new Object[]{errorMsg, wrapper}, e);
            throw new ServiceException(ResultCode.FAIL, errorMsg, e);
        }
    }

    /**
     * 执行无返回值的目标方法 在异常时抛出指定的异常信息
     *
     * @param errorMsg
     * @param wrapper
     * @return void
     * @author 方典典
     * @time 2019/5/5 16:16
     */
    public static void executeWrapper(String errorMsg, Consumer wrapper) {
        try {
            wrapper.execute();
        } catch (Exception e) {
            LoggerUtils.error(ExceptionWrapper.class, new Object[]{errorMsg, wrapper}, e);
            throw new ServiceException(ResultCode.FAIL, errorMsg, e);
        }
    }

}
