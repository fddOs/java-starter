package cn.seed.log.service.impl;

import cn.seed.common.utils.LoggerUtils;
import cn.seed.log.dao.BusinessLogValueMapper;
import cn.seed.log.entity.BusinessLogValue;
import cn.seed.log.service.ActionLogService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

/**
 * 业务日志子线程
 *
 * @author lixiao
 * @date 2019-02-18 14:43
 */
@Configuration
public class BusinessLogAsync {

    @Autowired
    private ActionLogService actionLogService;
    @Autowired
    private BusinessLogValueMapper businessLogMapper;

    /**
     * 根据数据库表名和traceid获取操作前后数据库的值
     * @param oprTableName 要记录的表名 多个表用&分割
     * @param traceId 请求的traceId
     * @return void
     * @author lixiao
     * @date 2019-02-18 14:45
     */
    @Async
    public void handleLogUpdateData(String oprTableName,String traceId){

        try {
            List<BusinessLogValue>
                businessLogValue= actionLogService.selectByBusinessLog(oprTableName,traceId);
            if(businessLogValue != null && !businessLogValue.isEmpty()){
                businessLogMapper.insertList(businessLogValue);
            }
        } catch (Exception e) {
            LoggerUtils.error(getClass(), new Object[]{traceId}, e);
        }

    }

}
