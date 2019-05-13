package cn.seed.log.service.impl;

import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.LoggerUtils;
import cn.seed.log.dao.ActionLogMapper;
import cn.seed.log.entity.ActionLog;
import cn.seed.log.entity.BusinessLogValue;
import cn.seed.log.service.ActionLogService;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        try {
            return actionLogMapper.selectBySql(sql);
        } catch (Exception e) {
            LoggerUtils.error(getClass(), new Object[]{sql}, e);
            Map<String, String> map = new HashMap();
            map.put("error", "获取值失败,错误信息:" + e.getMessage());
            List<Map<String, String>> list = new ArrayList();
            list.add(map);
            return list;
        }

    }

    @Override
    public int insertActionLogCommon(ActionLog actionLog) {
        return actionLogMapper.insertActionLogCommon(actionLog);
    }

    @Override
    public List<BusinessLogValue> selectByBusinessLog(String oprTableName, String traceId) {

        if (StringUtils.isEmpty(traceId) || StringUtils.isEmpty(oprTableName)) {
            throw new ServiceException(ResultCode.DATA_ERROR,
                    "oprTableName or traceId 不能为空");
        }
        String[] optString = oprTableName.split(",");
        List<String> oprList = Arrays.asList(optString);
        return actionLogMapper.selectByBusinessLog(oprList, traceId);
    }
}
