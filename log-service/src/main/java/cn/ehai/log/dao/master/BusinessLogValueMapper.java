package cn.ehai.log.dao.master;

import cn.ehai.log.entity.BusinessLogValue;

import java.util.List;

public interface BusinessLogValueMapper {

    int insertList(List<BusinessLogValue> record);

    int insertBusinessLogValue(BusinessLogValue record);
}