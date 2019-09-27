package cn.seed.common.core;

import cn.seed.common.utils.LoggerUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * AsyncConfig
 *
 * @author 方典典
 * @return
 * @time 2019/9/20 15:58
 */
@Component
@ConfigurationProperties
public class AsyncConfig implements AsyncConfigurer {
    private Integer threadCorePoolSize;
    private Integer threadMaxPoolSize;
    private Integer threadKeepAliveSeconds;
    private Integer threadQueueCapacity;
    private String threadNamePrefix;
    private Boolean threadWaitForTasksToCompleteOnShutdown;
    private Integer threadAwaitTerminationSeconds;
    private String threadRejectedExecutionHandler;
    @Autowired
    private Environment environment;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCorePoolSize);
        executor.setMaxPoolSize(threadMaxPoolSize);
        executor.setKeepAliveSeconds(threadKeepAliveSeconds);
        executor.setQueueCapacity(threadQueueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(threadWaitForTasksToCompleteOnShutdown);
        executor.setAwaitTerminationSeconds(threadAwaitTerminationSeconds);
        try {
            executor.setRejectedExecutionHandler((RejectedExecutionHandler) Class.forName
                    (threadRejectedExecutionHandler).newInstance());
        } catch (Exception e) {
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            LoggerUtils.error(getClass(), "设置线程池拒绝策略失败,class:" + threadRejectedExecutionHandler +
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

    public Integer getThreadCorePoolSize() {
        return threadCorePoolSize;
    }

    public void setThreadCorePoolSize(Integer threadCorePoolSize) {
        this.threadCorePoolSize = threadCorePoolSize;
    }

    public Integer getThreadMaxPoolSize() {
        return threadMaxPoolSize;
    }

    public void setThreadMaxPoolSize(Integer threadMaxPoolSize) {
        this.threadMaxPoolSize = threadMaxPoolSize;
    }

    public Integer getThreadKeepAliveSeconds() {
        return threadKeepAliveSeconds;
    }

    public void setThreadKeepAliveSeconds(Integer threadKeepAliveSeconds) {
        this.threadKeepAliveSeconds = threadKeepAliveSeconds;
    }

    public Integer getThreadQueueCapacity() {
        return threadQueueCapacity;
    }

    public void setThreadQueueCapacity(Integer threadQueueCapacity) {
        this.threadQueueCapacity = threadQueueCapacity;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public Boolean getThreadWaitForTasksToCompleteOnShutdown() {
        return threadWaitForTasksToCompleteOnShutdown;
    }

    public void setThreadWaitForTasksToCompleteOnShutdown(Boolean threadWaitForTasksToCompleteOnShutdown) {
        this.threadWaitForTasksToCompleteOnShutdown = threadWaitForTasksToCompleteOnShutdown;
    }

    public Integer getThreadAwaitTerminationSeconds() {
        return threadAwaitTerminationSeconds;
    }

    public void setThreadAwaitTerminationSeconds(Integer threadAwaitTerminationSeconds) {
        this.threadAwaitTerminationSeconds = threadAwaitTerminationSeconds;
    }

    public String getThreadRejectedExecutionHandler() {
        return threadRejectedExecutionHandler;
    }

    public void setThreadRejectedExecutionHandler(String threadRejectedExecutionHandler) {
        this.threadRejectedExecutionHandler = threadRejectedExecutionHandler;
    }
}