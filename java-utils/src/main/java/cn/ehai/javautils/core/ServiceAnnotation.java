package cn.ehai.javautils.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description:用于描述业务类型以及该记录唯一编号的注解
 * @author:方典典
 * @time:2017年12月18日 下午4:11:56
 */
@Target(ElementType.METHOD)
@Retention(RUNTIME)
@Documented
public @interface ServiceAnnotation {
	/**
	 * 业务类型枚举
	 */
	/**
	 * 
	 */
	public enum ServiceActionTypeEnum {
		/**
		 * 用户注册
		 */
		REGISTERUSER(1),
		/**
		 * 取消订单
		 */
		CANCELORDER(2),
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

	/**
	 * @Description:业务类型
	 * @return ServiceActionTypeEnum
	 * @exception:
	 * @author: 方典典
	 * @time:2017年12月27日 下午6:28:07
	 */
	ServiceActionTypeEnum value();

}