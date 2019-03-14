package cn.ehai.log.dao;

import cn.ehai.log.entity.BusinessLog;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BusinessLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BusinessLog record);

    BusinessLog selectByPrimaryKey(Integer id);

    List<BusinessLog> selectAll();

    int updateByPrimaryKey(BusinessLog record);

    List<BusinessLog> selectByOrderNoSys(@Param("orderNo") String orderNo,@Param("sysName")String sysName);

    List<BusinessLog> selectByOrderNoAction(@Param("orderNo") String orderNo,@Param("actionType")Integer actionType);

    List<BusinessLog> selectByOrderNo( String orderNo);
}