package cn.ehai.log.service;

import cn.ehai.log.entity.ActionLog;

import cn.ehai.log.entity.BusinessLogValue;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * @author 方典典
 */
public interface ActionLogService {

    List<Map<String, String>> selectBySql(String sql);

    int insertActionLog(ActionLog actionLog);

    int insertActionLogCommon(ActionLog actionLog);

    /**
     * 获取业务日志修改的数据的值
     * @param oprTableName 操作的数据库的名称
     * @param traceId 请求的标记
     * @return cn.ehai.log.entity.BusinessLogValue
     * @author lixiao
     * @date 2019-02-18 14:56
     */
    List<BusinessLogValue> selectByBusinessLog(String oprTableName,
         String traceId);
}
