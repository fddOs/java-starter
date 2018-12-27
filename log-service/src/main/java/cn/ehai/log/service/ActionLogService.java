package cn.ehai.log.service;

import cn.ehai.log.entity.ActionLog;

import java.util.List;
import java.util.Map;

/**
 * @author 方典典
 */
public interface ActionLogService {

    List<Map<String, String>> selectBySql(String sql);

    int insertActionLog(ActionLog actionLog);

    int insertActionLogCommon(ActionLog actionLog);
}
