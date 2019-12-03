package cn.seed.authority.service;

import cn.seed.authority.dto.GroupDataDTO;
import cn.seed.authority.dto.ModuleInfoDTO;
import cn.seed.authority.dto.UserDataDTO;
import cn.seed.common.core.Result;

import java.util.List;
import java.util.function.Supplier;

/**
 * 权限API 接口
 *
 * @author 方典典
 * @time 2019/11/29 17:28
 */
public interface AuthService {

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
    Result<List<ModuleInfoDTO>> btnAuth(String userCode, String systemCode, String moduleId);

    /**
     * 根据用户工号，系统编码，获取用户功能权限
     *
     * @param userCode
     * @param systemCode
     * @return cn.seed.common.core.Result
     * @author 方典典
     * @time 2019/11/29 17:00
     */
    Result<List<ModuleInfoDTO>> getFunAuth(String userCode, String systemCode);

    /**
     * 根据用户工号，系统编码，数据类型，返回层级，是否包含离职员工获取组织架构数据权限
     *
     * @param userCode
     * @param systemCode
     * @return
     * @author 方典典
     * @date 2019/11/22 10:27
     */
    Result<List<GroupDataDTO>> getGroupDataAuthTree(String userCode, String systemCode);

    /**
     * 根据用户工号，系统编码，数据类型，返回层级，是否包含离职员工获取人员信息数据权限
     *
     * @param userCode
     * @param systemCode
     * @param isContainResignUser
     * @return
     * @author 方典典
     * @date 2019/11/22 10:27
     */
    Result<List<UserDataDTO>> getUserDataAuthTree(String userCode, String systemCode, String isContainResignUser);

    /**
     * 根据用户工号，系统编码，groupID，数据类型，是否包含离职员工，获取该群组下所有末级节点上的组织架构信息
     *
     * @param userCode
     * @param systemCode
     * @param groupId
     * @return
     * @author 方典典
     * @date 2019/11/22 10:27
     */
    Result<List<GroupDataDTO>> getGroupDataAuth(String userCode, Integer groupId, String systemCode);

    /**
     * 根据用户工号，系统编码，groupID，数据类型，是否包含离职员工，获取该群组下所有末级节点上的人员信息
     *
     * @param userCode
     * @param systemCode
     * @param groupId
     * @param isContainResignUser
     * @return
     * @author 方典典
     * @date 2019/11/22 10:27
     */
    Result<List<UserDataDTO>> getUserDataAuth(String userCode, String systemCode, Integer groupId, String
            isContainResignUser);

    /**
     * 用户数据权限处理
     *
     * @param userCode
     * @param groupId
     * @param systemCode
     * @param isContainResignUser
     * @param wrapper
     * @return java.util.List<cn.seed.authority.dto.UserDataDTO>
     * @author 方典典
     * @time 2019/12/3 9:35
     */
    <T extends UserDataDTO> List<T> userDateAuthHandler(String userCode, Integer groupId, String systemCode, String
            isContainResignUser, Supplier<List<T>> wrapper);

    /**
     * 用户数据权限处理
     *
     * @param userCode
     * @param systemCode
     * @param isContainResignUser
     * @param wrapper
     * @return java.util.List<cn.seed.authority.dto.UserDataDTO>
     * @author 方典典
     * @time 2019/12/3 9:35
     */
    <T extends UserDataDTO> List<T> userDateAuthHandler(String userCode, String systemCode, String
            isContainResignUser, Supplier<List<T>> wrapper);

    /**
     * 用户数据权限处理
     *
     * @param userCode
     * @param isContainResignUser
     * @param wrapper
     * @return java.util.List<cn.seed.authority.dto.UserDataDTO>
     * @author 方典典
     * @time 2019/12/3 9:35
     */
    <T extends UserDataDTO> List<T> userDateAuthHandler(String userCode, String isContainResignUser,
                                                        Supplier<List<T>> wrapper);

    /**
     * 用户数据权限处理 systemCode为本系统
     *
     * @param userCode
     * @param groupId
     * @param isContainResignUser
     * @param wrapper
     * @return java.util.List<cn.seed.authority.dto.UserDataDTO>
     * @author 方典典
     * @time 2019/12/3 9:35
     */
    <T extends UserDataDTO> List<T> userDateAuthHandler(String userCode, Integer groupId, String
            isContainResignUser, Supplier<List<T>> wrapper);

    /**
     * 用户数据权限处理
     *
     * @param userCode
     * @param groupId
     * @param systemCode
     * @param wrapper
     * @return java.util.List<cn.seed.authority.dto.UserDataDTO>
     * @author 方典典
     * @time 2019/12/3 9:35
     */
    <T extends GroupDataDTO> List<T> groupDateAuthHandler(String userCode, Integer groupId, String systemCode,
                                                          Supplier<List<T>> wrapper);

    /**
     * 组织数据权限处理
     *
     * @param userCode
     * @param systemCode
     * @param wrapper
     * @return java.util.List<cn.seed.authority.dto.UserDataDTO>
     * @author 方典典
     * @time 2019/12/3 9:35
     */
    <T extends GroupDataDTO> List<T> groupDateAuthHandler(String userCode, String systemCode, Supplier<List<T>>
            wrapper);

    /**
     * 组织数据权限处理
     *
     * @param userCode
     * @param wrapper
     * @return java.util.List<cn.seed.authority.dto.UserDataDTO>
     * @author 方典典
     * @time 2019/12/3 9:35
     */
    <T extends GroupDataDTO> List<T> groupDateAuthHandler(String userCode, Supplier<List<T>> wrapper);

    /**
     * 组织数据权限处理 systemCode为本系统
     *
     * @param userCode
     * @param groupId
     * @param wrapper
     * @return java.util.List<cn.seed.authority.dto.UserDataDTO>
     * @author 方典典
     * @time 2019/12/3 9:35
     */
    <T extends GroupDataDTO> List<T> groupDateAuthHandler(String userCode, Integer groupId, Supplier<List<T>> wrapper);
}
