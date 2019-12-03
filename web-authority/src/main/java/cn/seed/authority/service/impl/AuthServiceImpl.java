package cn.seed.authority.service.impl;

import cn.seed.authority.dto.GroupDataDTO;
import cn.seed.authority.dto.ModuleInfoDTO;
import cn.seed.authority.dto.UserDataDTO;
import cn.seed.authority.http.api.AuthApi;
import cn.seed.authority.service.AuthService;
import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.Result;
import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import com.google.common.collect.Lists;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    public Result<List<UserDataDTO>> getUserDataAuth(String userCode, String systemCode, Integer groupId, String
            isContainResignUser) {
        return builder.target(AuthApi.class, getAuthUrl()).getUserDataAuth(userCode, systemCode, groupId,
                isContainResignUser);
    }

    @Override
    public <T extends UserDataDTO> List<T> userDateAuthHandler(String userCode, Integer groupId, String systemCode,
                                                               String isContainResignUser, Supplier<List<T>> wrapper) {
        Result<List<UserDataDTO>> result = getUserDataAuth(userCode, systemCode, groupId, isContainResignUser);
        return userDateAuthHandler(result, wrapper);
    }

    @Override
    public <T extends UserDataDTO> List<T> userDateAuthHandler(String userCode, String systemCode, String
            isContainResignUser, Supplier<List<T>> wrapper) {
        Result<List<UserDataDTO>> result = getUserDataAuthTree(userCode, systemCode, isContainResignUser);
        return userDateAuthHandler(result, wrapper);
    }

    @Override
    public <T extends UserDataDTO> List<T> userDateAuthHandler(String userCode, String isContainResignUser,
                                                               Supplier<List<T>> wrapper) {
        Result<List<UserDataDTO>> result = getUserDataAuthTree(userCode, ApolloBaseConfig.getSystemCode(),
                isContainResignUser);
        return userDateAuthHandler(result, wrapper);
    }

    @Override
    public <T extends UserDataDTO> List<T> userDateAuthHandler(String userCode, Integer groupId, String
            isContainResignUser, Supplier<List<T>> wrapper) {
        return userDateAuthHandler(userCode, groupId, ApolloBaseConfig.getSystemCode(), isContainResignUser, wrapper);
    }

    @Override
    public <T extends GroupDataDTO> List<T> groupDateAuthHandler(String userCode, Integer groupId, String systemCode,
                                                                 Supplier<List<T>> wrapper) {
        return groupDateAuthHandler(getGroupDataAuth(userCode, groupId, systemCode), wrapper);
    }

    @Override
    public <T extends GroupDataDTO> List<T> groupDateAuthHandler(String userCode, String systemCode,
                                                                 Supplier<List<T>> wrapper) {
        return groupDateAuthHandler(getGroupDataAuthTree(userCode, systemCode), wrapper);
    }

    @Override
    public <T extends GroupDataDTO> List<T> groupDateAuthHandler(String userCode, Supplier<List<T>> wrapper) {
        return groupDateAuthHandler(getGroupDataAuthTree(userCode, ApolloBaseConfig.getSystemCode()), wrapper);
    }

    @Override
    public <T extends GroupDataDTO> List<T> groupDateAuthHandler(String userCode, Integer groupId, Supplier<List<T>>
            wrapper) {
        return groupDateAuthHandler(getGroupDataAuth(userCode, groupId, ApolloBaseConfig.getSystemCode()), wrapper);
    }

    /**
     * 检查权限查询Result
     *
     * @param result
     * @return T
     * @author 方典典
     * @time 2019/12/3 9:51
     */
    private <T> T authResult(Result<T> result) {
        if (Objects.isNull(result)) {
            throw new ServiceException(ResultCode.HTTP_FAIL, "获取数据权限失败");
        }
        result.checkSuccess("获取数据权限失败");
        return result.getResult();
    }

    /**
     * 用户数据权限处理
     *
     * @param result
     * @param wrapper
     * @return java.util.List<T>
     * @author 方典典
     * @time 2019/12/3 10:35
     */
    private <T extends UserDataDTO> List<T> userDateAuthHandler(Result<List<UserDataDTO>> result, Supplier<List<T>>
            wrapper) {
        List<UserDataDTO> userDataDTOList = authResult(result);
        if (userDataDTOList.isEmpty()) {
            return Lists.newArrayList();
        }
        List<T> serviceResult = wrapper.get();
        if (Objects.isNull(serviceResult)) {
            return Lists.newArrayList();
        }
        return serviceResult.stream().filter(t ->
                userDataDTOList.stream().anyMatch(userDataDTO -> userDataDTO.getUserCode().equals(t.getUserCode()))
        ).collect(Collectors.toList());
    }

    /**
     * 组织数据权限处理
     *
     * @param result
     * @param wrapper
     * @return java.util.List<T>
     * @author 方典典
     * @time 2019/12/3 10:35
     */
    private <T extends GroupDataDTO> List<T> groupDateAuthHandler(Result<List<GroupDataDTO>> result, Supplier<List<T>>
            wrapper) {
        List<GroupDataDTO> groupDataDTOList = authResult(result);
        if (groupDataDTOList.isEmpty()) {
            return Lists.newArrayList();
        }
        List<T> serviceResult = wrapper.get();
        if (Objects.isNull(serviceResult)) {
            return Lists.newArrayList();
        }
        return serviceResult.stream().filter(t ->
                groupDataDTOList.stream().anyMatch(groupDataDTO -> groupDataDTO.getGroupId().equals(t.getGroupId()))
        ).collect(Collectors.toList());
    }
}
