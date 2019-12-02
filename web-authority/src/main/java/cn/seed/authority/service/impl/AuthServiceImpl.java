package cn.seed.authority.service.impl;

import cn.seed.authority.dto.GroupDataDTO;
import cn.seed.authority.dto.ModuleInfoDTO;
import cn.seed.authority.dto.UserDataDTO;
import cn.seed.authority.http.api.AuthApi;
import cn.seed.authority.service.AuthService;
import cn.seed.common.core.Result;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.seed.common.core.ApolloBaseConfig.getAuthUrl;

/**
 * 权限API 实现
 *
 * @author 方典典
 * @time 2019/11/29 17:27
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private Feign.Builder builder;

    @Override
    public Result<Boolean> verifyAuth(String userCode, String systemCode, String moduleId) {
        return builder.target(AuthApi.class, getAuthUrl()).verifyAuth(userCode, systemCode, moduleId);
    }

    @Override
    public Result<List<ModuleInfoDTO>> btnAuth(String userCode, String systemCode, String moduleId) {
        return builder.target(AuthApi.class, getAuthUrl()).btnAuth(userCode, systemCode, moduleId);
    }

    @Override
    public Result<List<ModuleInfoDTO>> getFunAuth(String userCode, String systemCode) {
        return builder.target(AuthApi.class, getAuthUrl()).getFunAuth(userCode, systemCode);
    }

    @Override
    public Result<List<GroupDataDTO>> getGroupDataAuthTree(String userCode, String systemCode) {
        return builder.target(AuthApi.class, getAuthUrl()).getGroupDataAuthTree(userCode, systemCode);
    }

    @Override
    public Result<List<UserDataDTO>> getUserDataAuthTree(String userCode, String systemCode, String
            isContainResignUser) {
        return builder.target(AuthApi.class, getAuthUrl()).getUserDataAuthTree(userCode, systemCode,
                isContainResignUser);
    }

    @Override
    public Result<List<GroupDataDTO>> getGroupDataAuth(String userCode, Integer groupId, String systemCode) {
        return builder.target(AuthApi.class, getAuthUrl()).getGroupDataAuth(userCode, groupId, systemCode);
    }

    @Override
    public Result<List<UserDataDTO>> getUserDataAuthTree(String userCode, String systemCode, Integer groupId, String
            isContainResignUser) {
        return builder.target(AuthApi.class, getAuthUrl()).getUserDataAuth(userCode, systemCode, groupId,
                isContainResignUser);
    }
}
