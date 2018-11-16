package cn.ehai.log.log;

/**
 * 业务类型枚举
 */
public enum ServiceActionTypeEnum {
    /**
     * 没有登记的业务日志异常都是其他记录
     */
    OTHER_RECORD(7);


    private int actionType;

    ServiceActionTypeEnum(int actionType) {
        this.actionType = actionType;
    }

    public int getActionType() {
        return actionType;
    }
}