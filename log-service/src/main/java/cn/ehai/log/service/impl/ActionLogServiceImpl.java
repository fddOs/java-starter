package cn.ehai.log.service.impl;

import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ServiceException;
import cn.ehai.log.dao.master.ActionLogMapper;
import cn.ehai.log.entity.ActionLog;
import cn.ehai.log.entity.BusinessLogValue;
import cn.ehai.log.service.ActionLogService;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

/**
 * @author 方典典
 */

@Service
public class ActionLogServiceImpl implements ActionLogService {
    @Autowired
    private ActionLogMapper actionLogMapper;

    @Override
    public List<Map<String, String>> selectBySql(String sql) {
        return actionLogMapper.selectBySql(sql);
    }

    @Override
    public int insertActionLog(ActionLog actionLog) {
        return actionLogMapper.insertActionLog(actionLog);
    }

    @Override
    public int insertActionLogCommon(ActionLog actionLog) {
        return actionLogMapper.insertActionLogCommon(actionLog);
    }

    @Override public List<BusinessLogValue> selectByBusinessLog(String oprTableName, String traceId) {

        if(StringUtils.isEmpty(traceId)||StringUtils.isEmpty(oprTableName)){
            throw new ServiceException(ResultCode.DATA_ERROR,
                "oprTableName or traceId 不能为空");
        }
        String[] optString = oprTableName.split(",");
        List<String> oprList  = Arrays.asList(optString);
        return actionLogMapper.selectByBusinessLog(oprList,traceId);
    }
}
