package cn.seed.authority.service;

import cn.seed.authority.model.BtnAuthResult;
import cn.seed.common.core.Result;

import java.util.List;

/**
 * @author xianglong.chen
 * @time 2019/4/15 上午9:46
 */
public interface WebAuthority {

    /**
     * 验证权限
     *
     * @param userCode
     * @param systemCode
     * @param moduleId
     * @return
     */
    Result<Boolean> verifyAuth(String userCode, String systemCode, String moduleId);

    /**
     * 根据用户工号，系统编码，模块ID，获取该页面的所有按钮
     *
     * @param userCode
     * @param systemCode
     * @param moduleId
     * @return
     */
    Result<List<BtnAuthResult>> btnAuth(String userCode, String systemCode, String moduleId);
}
