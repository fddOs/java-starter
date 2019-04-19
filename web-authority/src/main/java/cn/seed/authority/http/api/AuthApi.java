package cn.seed.authority.http.api;

import cn.seed.authority.model.BtnAuthResult;
import cn.seed.common.core.Result;
import feign.Param;
import feign.RequestLine;

import java.util.List;

/**
 * 权限相关接口
 *
 * @author xianglong.chen
 * @description AuthApi
 * @time 2019/2/21 下午1:42
 */
public interface AuthApi {

    /**
     * 验证权限
     * @param userCode
     * @param systemCode
     * @param moduleId
     * @return
     */
    @RequestLine("GET auth/verifyAuth?userCode={userCode}&systemCode={systemCode}&moduleId={moduleId}")
    Result<Boolean> verifyAuth(@Param("userCode") String userCode,
                               @Param("systemCode") String systemCode, @Param("moduleId") String moduleId);

    /**
     * 根据用户工号，系统编码，模块ID，获取该页面的所有按钮
     * @param userCode
     * @param systemCode
     * @param moduleId
     * @return
     */
    @RequestLine("GET auth/btnAuth?userCode={userCode}&systemCode={systemCode}&moduleId={moduleId}")
    Result<List<BtnAuthResult>> btnAuth(@Param("userCode") String userCode,
                                        @Param("systemCode") String systemCode, @Param("moduleId") String moduleId);


}
