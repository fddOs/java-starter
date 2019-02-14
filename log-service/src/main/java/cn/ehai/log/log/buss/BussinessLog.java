package cn.ehai.log.log.buss;

/**
 * 业务日志注解
 *
 * @author lixiao
 * @date 2019-02-13 17:08
 */
public @interface BussinessLog {

    /**
     * 操作人
     * @param
     * @return java.lang.String
     * @author lixiao
     * @date 2019-02-13 17:09
     */
    String oprNo() default "";

    /**
     * 记录的动作  在actiontype定义
     * @param
     * @return int
     * @author lixiao
     * @date 2019-02-13 17:13
     */
    int action() default 0;

    /**
     * 关联单号
     * @param
     * @return java.lang.String
     * @author lixiao
     * @date 2019-02-13 17:14
     */
    String referNo() default "";

    /**
     * 用户id--对外
     * @param
     * @return java.lang.String
     * @author lixiao
     * @date 2019-02-13 17:14
     */
    String userId() default "";

    /**
     * 业务日志 对应的操作数据库表
     * @param
     * @return java.lang.String
     * @author lixiao
     * @date 2019-02-13 17:14
     */
    String oprTableName() default "";

    /**
     * 拓展字段JSON
     * @param
     * @return java.lang.String
     * @author lixiao
     * @date 2019-02-13 17:15
     */
    String extend() default "";

    /**
     *  操作人取值参数在方法参数里面的位置 默认为0
     * @param
     * @return int
     * @author lixiao
     * @date 2019-02-13 17:16
     */
    int oprNoNum() default 0;

    /**
     *  关联单号取值参数在方法参数里面的位置 默认为0
     * @param
     * @return int
     * @author lixiao
     * @date 2019-02-13 17:16
     */
    int referNoNum() default 0;
    /**
     *  用户id取值参数在方法参数里面的位置 默认为0
     * @param
     * @return int
     * @author lixiao
     * @date 2019-02-13 17:16
     */
    int userIdNum() default 0;
    /**
     *  扩展字段取值参数在方法参数里面的位置 默认为0
     * @param
     * @return int
     * @author lixiao
     * @date 2019-02-13 17:16
     */
    int extendNum() default 0;

}
