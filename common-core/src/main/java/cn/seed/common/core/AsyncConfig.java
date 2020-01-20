package cn.seed.common.core;

import cn.seed.common.utils.LoggerUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.seed.common.core.ApolloBaseConfig.*;

/**
 * AsyncConfig
 *
 * @author 方典典
 * @return
 * @time 2019/9/20 15:58
 */
@ConfigurationProperties
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(getThreadCorePoolSize());
        executor.setMaxPoolSize(getThreadMaxPoolSize());
        executor.setKeepAliveSeconds(getThreadKeepAliveSeconds());
        executor.setQueueCapacity(getThreadQueueCapacity());
        executor.setThreadNamePrefix(getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(getThreadWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(getThreadAwaitTerminationSeconds());
        try {
            executor.setRejectedExecutionHandler((RejectedExecutionHandler) Class.forName
                    (getThreadRejectedExecutionHandler()).newInstance());
        } catch (Exception e) {
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            LoggerUtils.error(getClass(), "设置线程池拒绝策略失败,class:" + getThreadRejectedExecutionHandler() +
                    "。默认设置为CallerRunsPolicy");
        }
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object... params) -> {
            LoggerUtils.errorSendELKAndPrintDetailConsole(getClass(), params, ex);
        };
    }

}