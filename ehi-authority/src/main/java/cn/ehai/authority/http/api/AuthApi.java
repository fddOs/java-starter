package cn.ehai.authority.http.api;

import cn.ehai.common.core.Result;
import feign.Param;
import feign.RequestLine;

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
}
