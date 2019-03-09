package cn.ehai.log.dao;

import cn.ehai.log.entity.BusinessLog;
import java.util.List;

public interface BusinessLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BusinessLog record);

    BusinessLog selectByPrimaryKey(Integer id);

    List<BusinessLog> selectAll();

    int updateByPrimaryKey(BusinessLog record);

    List<BusinessLog> selectByOrderNo(String orderNo);
}