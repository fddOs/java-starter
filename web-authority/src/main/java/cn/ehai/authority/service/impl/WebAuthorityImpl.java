package cn.ehai.authority.service.impl;

import cn.ehai.authority.http.api.AuthApi;
import cn.ehai.authority.model.BtnAuthResult;
import cn.ehai.authority.service.WebAuthority;
import cn.ehai.common.core.ApolloBaseConfig;
import cn.ehai.common.core.Result;
import feign.Feign;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xianglong.chen
 * @time 2019/4/15 上午9:46
 */
@Service
public class WebAuthorityImpl implements WebAuthority {

    private AuthApi authApi;

    public WebAuthorityImpl(Feign.Builder builder) {
        String authUrl = ApolloBaseConfig.getAuthUrl();
        if (StringUtils.isEmpty(authUrl)) {
            throw new IllegalArgumentException("authUrl参数获取失败，请在ApolloCommonConfig中检查该配置项");
        }
        authApi = builder.target(AuthApi.class, authUrl);
    }

    @Override
    public Result<Boolean> verifyAuth(String userCode, String systemCode, String moduleId) {
        return authApi.verifyAuth(userCode, systemCode, moduleId);
    }

    @Override
    public Result<List<BtnAuthResult>> btnAuth(String userCode, String systemCode, String moduleId) {
        return authApi.btnAuth(userCode, systemCode, moduleId);
    }
}