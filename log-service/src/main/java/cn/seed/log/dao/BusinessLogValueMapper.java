package cn.seed.log.dao;

import cn.seed.log.entity.BusinessLogValue;

import java.util.List;

public interface BusinessLogValueMapper {

    int insertList(List<BusinessLogValue> record);

    int insertBusinessLogValue(BusinessLogValue record);
}