package cn.seed.log.entity;

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
     * traceId
     */
    private String traceId;


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

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}