package cn.ehai.log.service;

import cn.ehai.log.entity.ActionLog;

import java.util.List;
import java.util.Map;

/**
 * Created by lx on 2017/12/27.
 */
public interface ActionLogService {

    /**
     * @Description:将日志发送到ELK
     * @param logELK
     *            void
     * @exception:
     * @author: 方典典
     * @time:2017年12月29日 下午3:45:31
     */
//	void sendELK(LogELK logELK);

    List<Map<String, String>> selectBySql(String sql);

    int insert(ActionLog actionLog);
}
