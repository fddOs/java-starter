package cn.ehai.javalog.service.impl;

import cn.ehai.javalog.entity.ActionLog;
import cn.ehai.javalog.service.ActionLogService;
import cn.ehai.javautils.utils.LoggerUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

/**
 * @Description:异步处理业务日志
 * @author:方典典
 * @time:2018/9/25 17:59
 */
@Configuration
public class ActionLogServiceAsync {
    @Autowired
    private ActionLogService actionLogService;

    @Async
    public void insertServiceLogAsync(ActionLog actionLog){
        try {
            actionLogService.insert(actionLog);
        } catch (Exception e) {
            LoggerUtils.error(getClass(), ExceptionUtils.getStackTrace(e));
        }
    }
}
