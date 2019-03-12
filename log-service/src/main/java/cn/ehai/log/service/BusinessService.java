package cn.ehai.log.service;

import cn.ehai.log.entity.BusinessLog;
import java.util.List;

/**
 * 业务日志
 *
 * @author lixiao
 * @date 2019-03-12 20:14
 */
public interface BusinessService {

    /**
     * 根据订单号获取日志
     * @param orderNo
     * @return java.util.List<cn.ehai.log.entity.BusinessLog>
     * @author lixiao
     * @date 2019-03-12 20:15
     */
    List<BusinessLog>  selectByOrderNo(String orderNo);

    /**
     *  根据订单号和actiontype获取日志记录
     * @param orderNo
     * @param actionType
     * @return java.util.List<cn.ehai.log.entity.BusinessLog>
     * @author lixiao
     * @date 2019-03-12 20:16
     */
    List<BusinessLog> selectByOrderNoAction(String orderNo,Integer actionType);


}
