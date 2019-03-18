package cn.ehai.log.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel(value="日志操作对照")
public class ActionItem {
    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private Integer id;

    /**
     * 操作说明
     */
    @ApiModelProperty("操作说明")
    @NotNull(message="action_name参数不能为空")
    @Size(max=15,min=0,message="action_name长度超过")
    private String actionName;

    /**
     * 主动创建时间
     */
    @ApiModelProperty("主动创建时间")
    @NotNull(message="gmt_create参数不能为空")
    private Date gmtCreate;

    /**
     * 被动更新时间
     */
    @ApiModelProperty("被动更新时间")
    private Date gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}