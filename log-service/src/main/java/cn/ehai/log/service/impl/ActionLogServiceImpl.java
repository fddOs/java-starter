package cn.ehai.log.service.impl;

import cn.ehai.log.dao.ActionLogMapper;
import cn.ehai.log.entity.ActionLog;
import cn.ehai.log.service.ActionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

}
