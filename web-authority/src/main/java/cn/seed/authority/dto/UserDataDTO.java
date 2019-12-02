/**
 *
 */
package cn.seed.authority.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 人员数据权限出参
 *
 * @author:方典典
 * @time:2018年4月19日 下午4:31:30
 */
@ApiModel(value = "人员数据权限出参")
public class UserDataDTO {

    /**
     * 用户工号
     */
    @ApiModelProperty("用户工号")
    private String userCode;

    /**
     * 用户姓名
     */
    @ApiModelProperty("用户姓名")
    private String userName;

    /**
     * 用户组的ID
     */
    @ApiModelProperty("用户组的ID")
    private Integer groupId;

    /**
     * 用户当前状态 P:待离职 L:已离职 Y:正常入职 S:试用期 F:放弃录用 D:待录用
     */
    @ApiModelProperty("用户当前状态 P:待离职 L:已离职 Y:正常入职 S:试用期 F:放弃录用 D:待录用")
    private String userStatus;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    @Override
    public String toString() {
        return "UserDataDTO{" +
                "userCode='" + userCode + '\'' +
                ", userName='" + userName + '\'' +
                ", groupId=" + groupId +
                ", userStatus='" + userStatus + '\'' +
                '}';
    }
}
