package cn.seed.log.service.impl;

import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.common.utils.ProjectInfoUtils;
import cn.seed.log.dao.BusinessLogMapper;
import cn.seed.log.entity.BusinessLog;
import cn.seed.log.service.BusinessService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author lixiao
 * @date 2019-03-12 20:17
 */
@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private BusinessLogMapper businessLogMapper;

    @Override public List<BusinessLog> selectByOrderNo(String orderNo) {
        if(StringUtils.isEmpty(orderNo)){
            throw  new ServiceException(ResultCode.DATA_ERROR,"订单号不能为空");
        }
        return businessLogMapper.selectByOrderNoSys(orderNo, ProjectInfoUtils.getProjectContext());
    }

    @Override public List<BusinessLog> selectByOrderNoAction(String orderNo, Integer actionType) {
        if(StringUtils.isEmpty(orderNo)){
            throw  new ServiceException(ResultCode.DATA_ERROR,"订单号不能为空");
        }
        if(actionType==null){
            throw  new ServiceException(ResultCode.DATA_ERROR,"actionType不能为null");
        }
        return businessLogMapper.selectByOrderNoAction(orderNo,actionType);
    }
}
