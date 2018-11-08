package cn.ehai.javalog.service.impl;

import cn.ehai.javalog.dao.ActionLogMapper;
import cn.ehai.javalog.entity.ActionLog;
import cn.ehai.javalog.service.ActionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by lx on 2017/12/27.
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
    public int insert(ActionLog actionLog) {
        return actionLogMapper.insert(actionLog);
    }

}
