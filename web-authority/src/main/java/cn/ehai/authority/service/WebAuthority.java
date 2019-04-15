package cn.ehai.authority.service;

import cn.ehai.common.core.Result;
import feign.Param;

import java.util.concurrent.Future;

/**
 * @author xianglong.chen
 * @time 2019/4/15 上午9:46
 */
public interface WebAuthority {

    /**
     * 验证权限
     * @param userCode
     * @param systemCode
     * @param moduleId
     * @return
     */
    Result<Boolean> verifyAuth(String userCode, String systemCode, String moduleId);
}
