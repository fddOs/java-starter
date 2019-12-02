/**
 *
 */
package cn.seed.authority.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 组织数据权限出参
 *
 * @author:方典典
 * @time:2018年4月19日 下午4:31:30
 */
@ApiModel(value = "组织数据权限出参")
public class GroupDataDTO {

    /**
     * 人事分组ID，与rs_user表中group_id对应
     */
    @ApiModelProperty("人事分组ID，与rs_user表中group_id对应")
    private Integer groupId;

    /**
     * 组名称
     */
    @ApiModelProperty("组名称")
    private String groupName;

    /**
     * 组分类：Z:高层管理组 Y:区域 X:部门 A:总部普通组 H:城市 D:门店 W:外部修理厂
     */
    @ApiModelProperty("组分类：Z:高层管理组 Y:区域 X:部门 A:总部普通组 H:城市 D:门店 W:外部修理厂")
    private String groupType;

    /**
     * 一嗨的门店ID，如果不是门店，则为0
     */
    @ApiModelProperty("一嗨的门店ID，如果不是门店，则为0")
    @NotNull(message = "store_id参数不能为空")
    private Integer storeId;

    /**
     * 该组织的上级ID
     */
    @ApiModelProperty("该组织的上级ID")
    private Integer parentGroupid;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getParentGroupid() {
        return parentGroupid;
    }

    public void setParentGroupid(Integer parentGroupid) {
        this.parentGroupid = parentGroupid;
    }

    @Override
    public String toString() {
        return "GroupDataDTO{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", groupType='" + groupType + '\'' +
                ", storeId=" + storeId +
                ", parentGroupid=" + parentGroupid +
                '}';
    }
}
