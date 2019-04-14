package cn.ehai.log.dao.master;

import cn.ehai.log.entity.ActionLog;
import cn.ehai.log.entity.BusinessLogValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ActionLogMapper {
    int insertActionLog(ActionLog record);

    int insertActionLogCommon(ActionLog record);

    List<Map<String, String>> selectBySql(@Param("sql") String sql);
    /**
     * 获取业务日志修改的数据的值
     * @param list 操作的数据库的名称
     * @param traceId 请求的标记
     * @return cn.ehai.log.entity.BusinessLogValue
     * @author lixiao
     * @date 2019-02-18 14:56
     */
    List<BusinessLogValue> selectByBusinessLog(@Param("oprTableNameList")List<String> list,
        @Param("traceId") String traceId);

}