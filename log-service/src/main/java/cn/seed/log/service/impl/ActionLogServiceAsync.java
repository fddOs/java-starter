package cn.seed.log.service.impl;

import cn.seed.common.utils.LoggerUtils;
import cn.seed.log.entity.ActionLog;
import cn.seed.log.service.ActionLogService;
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
    public void insertServiceLogCommonAsync(ActionLog actionLog) {
        try {
            actionLogService.insertActionLogCommon(actionLog);
        } catch (Exception e) {
            LoggerUtils.error(getClass(), new Object[]{actionLog}, e);
        }
    }
}
