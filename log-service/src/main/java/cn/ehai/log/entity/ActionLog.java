package cn.ehai.log.entity;

import javax.validation.constraints.*;

public class ActionLog {
    /**
     * 自增，主键
     */
    private Integer id;

    /**
     * 日志记录时间
     */
    @NotNull(message = "action_datetime参数不能为空")
    private String actionDatetime;

    /**
     * 操作类型
     */
    private Integer actionType;

    /**
     * 操作人登录帐号
     */
    private String oprNo;

    /**
     * 操作的表名
     */
    @NotNull(message = "opr_table_name参数不能为空")
    @Size(max = 20, min = 0, message = "opr_table_name长度超过")
    private String oprTableName;

    /**
     * 操作前的值
     */
    private String originalValue;

    /**
     * 操作后的新值
     */
    private String newValue;

    /**
     * 该修改记录的唯一标记订单号
     */
    private String referId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 业务sql是否执行成功
     */
    @NotNull(message = "is_success参数不能为空")
    private Boolean isSuccess;

    /**
     * 请求url
     */
    private String url;

    /**
     * 方法名字
     */
    private String methodName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActionDatetime() {
        return actionDatetime;
    }

    public void setActionDatetime(String actionDatetime) {
        this.actionDatetime = actionDatetime;
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

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

}