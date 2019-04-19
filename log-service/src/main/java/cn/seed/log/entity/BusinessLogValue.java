package cn.seed.log.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel(value = "业务日志更新前后的值")
public class BusinessLogValue {
    /**
     * 自增，主键
     */
    @ApiModelProperty("自增，主键")
    private Integer id;

    /**
     * 这次请求的traceid（自动获取）
     */
    @ApiModelProperty("这次请求的traceid（自动获取）")
    @NotNull(message = "trace_id参数不能为空")
    @Size(max = 32, min = 0, message = "trace_id长度超过")
    private String traceId;

    /**
     * 操作的表名
     */
    @ApiModelProperty("操作的表名")
    @NotNull(message = "opr_table_name参数不能为空")
    @Size(max = 50, min = 0, message = "opr_table_name长度超过")
    private String oprTableName;

    /**
     * 操作前的值
     */
    @ApiModelProperty("操作前的值")
    private String originalValue;

    /**
     * 操作后的新值
     */
    @ApiModelProperty("操作后的新值")
    private String newValue;

    /**
     * 主动创建时间
     */
    @ApiModelProperty("主动创建时间")
    @NotNull(message = "gmt_create参数不能为空")
    private Date gmtCreate;

    /**
     * 被动更新时间
     */
    @ApiModelProperty("被动更新时间")
    private Date gmtModified;
    @ApiModelProperty("日志记录时间")
    private String actionDatetime;
    @ApiModelProperty("操作人登录帐号")
    private String oprNo;

    public BusinessLogValue() {
    }

    public BusinessLogValue(String traceId, String oprTableName, String originalValue, String newValue, String
            actionDatetime, String oprNo) {
        this.traceId = traceId;
        this.oprTableName = oprTableName;
        this.originalValue = originalValue;
        this.newValue = newValue;
        this.actionDatetime = actionDatetime;
        this.oprNo = oprNo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getOprTableName() {
        return oprTableName;
    }

    public void setOprTableName(String oprTableName) {
        this.oprTableName = oprTableName;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
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

    public String getActionDatetime() {
        return actionDatetime;
    }

    public void setActionDatetime(String actionDatetime) {
        this.actionDatetime = actionDatetime;
    }

    public String getOprNo() {
        return oprNo;
    }

    public void setOprNo(String oprNo) {
        this.oprNo = oprNo;
    }
}