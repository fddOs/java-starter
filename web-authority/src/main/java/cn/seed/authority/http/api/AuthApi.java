package cn.seed.authority.http.api;

import cn.seed.authority.dto.GroupDataDTO;
import cn.seed.authority.dto.ModuleInfoDTO;
import cn.seed.authority.dto.UserDataDTO;
import cn.seed.common.core.Result;
import feign.Param;
import feign.RequestLine;

import java.util.List;

/**
 * 权限接口
 *
 * @author 方典典
 * @time 2019/11/29 17:22
 */
public interface AuthApi {

    /**
     * 验证权限
     *
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
     *
     * @param userCode
     * @param systemCode
     * @param moduleId
     * @return
     */
    @RequestLine("GET auth/btnAuth?userCode={userCode}&systemCode={systemCode}&moduleId={moduleId}")
    Result<List<ModuleInfoDTO>> btnAuth(@Param("userCode") String userCode,
                                        @Param("systemCode") String systemCode, @Param("moduleId") String moduleId);

    /**
     * 根据用户工号，系统编码，获取用户功能权限
     *
     * @param userCode
     * @param systemCode
     * @return cn.seed.common.core.Result
     * @author 方典典
     * @time 2019/11/29 17:00
     */
    @RequestLine("GET /auth/funAuthTree?userCode={userCode}&systemCode={systemCode}")
    Result<List<ModuleInfoDTO>> getFunAuth(@Param("userCode") String userCode, @Param("systemCode") String systemCode);

    /**
     * 根据用户工号，系统编码，数据类型，返回层级，是否包含离职员工获取组织架构数据权限
     *
     * @param userCode
     * @param systemCode
     * @return
     * @author 方典典
     * @date 2019/11/22 10:27
     */
    @RequestLine("GET /auth/dataAuthTree?userCode={userCode}&systemCode={systemCode}&dataType=A" +
            "&isContainResignUser=N")
    Result<List<GroupDataDTO>> getGroupDataAuthTree(@Param("userCode") String userCode, @Param("systemCode") String
            systemCode);

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
    @RequestLine("GET /auth/dataAuthTree?userCode={userCode}&systemCode={systemCode}&dataType=U" +
            "&isContainResignUser={isContainResignUser}")
    Result<List<UserDataDTO>> getUserDataAuthTree(@Param("userCode") String userCode, @Param("systemCode") String
            systemCode, @Param("isContainResignUser") String isContainResignUser);

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
    @RequestLine("GET /auth/dataAuth?userCode={userCode}&systemCode={systemCode}&groupId={groupId}&dataType=A" +
            "&isContainResignUser=N")
    Result<List<GroupDataDTO>> getGroupDataAuth(@Param("userCode") String userCode, @Param("groupId") Integer groupId,
                                                @Param("systemCode") String systemCode);

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
    @RequestLine("GET /auth/dataAuth?userCode={userCode}&systemCode={systemCode}&groupId={groupId}&dataType=U" +
            "&isContainResignUser={isContainResignUser}")
    Result<List<UserDataDTO>> getUserDataAuth(@Param("userCode") String userCode, @Param("systemCode") String
            systemCode, @Param("groupId") Integer groupId, @Param("isContainResignUser") String isContainResignUser);
}
