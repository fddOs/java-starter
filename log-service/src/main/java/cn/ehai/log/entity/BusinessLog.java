package cn.ehai.log.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel(value="业务日志表")
public class BusinessLog {
    /**
     * 自增，主键
     */
    @ApiModelProperty("自增，主键")
    private Integer id;

    /**
     * 操作类型
     */
    @ApiModelProperty("操作类型")
    @NotNull(message="action_type参数不能为空")
    private Integer actionType;

    /**
     * 操作人登录帐号
     */
    @ApiModelProperty("操作人登录帐号")
    @NotNull(message="opr_no参数不能为空")
    @Size(max=10,min=0,message="opr_no长度超过")
    private String oprNo;

    /**
     * 该修改记录的唯一标记订单号
     */
    @ApiModelProperty("该修改记录的唯一标记订单号")
    @NotNull(message="refer_id参数不能为空")
    @Size(max=15,min=0,message="refer_id长度超过")
    private String referId;

    /**
     * 用户id或者订单号
     */
    @ApiModelProperty("用户id或者订单号")
    @NotNull(message="user_id参数不能为空")
    @Size(max=15,min=0,message="user_id长度超过")
    private String userId;

    /**
     * 这次请求的traceid（自动获取）
     */
    @ApiModelProperty("这次请求的traceid（自动获取）")
    @NotNull(message="trace_id参数不能为空")
    @Size(max=32,min=0,message="trace_id长度超过")
    private String traceId;

    /**
     * 需要记录信息的表名（选填）需要根据这个名称获取数据库操作数据数据
     */
    @ApiModelProperty("需要记录信息的表名（选填）需要根据这个名称获取数据库操作数据数据")
    @NotNull(message="opr_table_name参数不能为空")
    @Size(max=50,min=0,message="opr_table_name长度超过")
    private String oprTableName;

    /**
     * 日志记录时间
     */
    @ApiModelProperty("日志记录时间")
    @NotNull(message="action_datetime参数不能为空")
    private Date actionDatetime;

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

    /**
     * 扩展字段，格式为JSON
     */
    @ApiModelProperty("扩展字段，格式为JSON")
    private String extendContent;
    /**
     * 系统名称
     */
    @ApiModelProperty("系统名称")
    private String sysName;

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public String getOprNo() {
        return oprNo;
    }

    public void setOprNo(String oprNo) {
        this.oprNo = oprNo;
    }

    public String getReferId() {
        return referId;
    }

    public void setReferId(String referId) {
        this.referId = referId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Date getActionDatetime() {
        return actionDatetime;
    }

    public void setActionDatetime(Date actionDatetime) {
        this.actionDatetime = actionDatetime;
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

    public String getExtendContent() {
        return extendContent;
    }

    public void setExtendContent(String extendContent) {
        this.extendContent = extendContent;
    }
}