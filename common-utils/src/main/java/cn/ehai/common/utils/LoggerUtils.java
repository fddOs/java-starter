package cn.ehai.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {
	/**
	 * 是否开启Debug
	 */
	public static boolean isDebug = LoggerFactory.getLogger(LoggerUtils.class).isDebugEnabled();
	/**
	 * 是否开启Info
	 */
	public static boolean isInfo = LoggerFactory.getLogger(LoggerUtils.class).isInfoEnabled();

	/**
	 * Debug 输出
	 * 
	 * @param clazz
	 *            目标.Class
	 * @param message
	 *            输出信息
	 */
	public static void debug(Class<? extends Object> clazz, String message) {
		if (!isDebug)
			return;
		Logger logger = LoggerFactory.getLogger(clazz);
		logger.debug(message);
	}

	/**
	 * Info 输出
	 */
	public static void info(Class<? extends Object> clazz, String message) {
		if (!isInfo)
			return;
		Logger logger = LoggerFactory.getLogger(clazz);
		logger.info(message);
	}

	/**
	 * Debug 输出
	 * 
	 * @param clazz
	 *            目标.Class
	 * @param fmtString
	 *            输出信息key
	 * @param value
	 *            输出信息value
	 */
	public static void fmtDebug(Class<? extends Object> clazz, String fmtString, Object... value) {
		if (!isDebug)
			return;
		if (StringUtils.isBlank(fmtString)) {
			return;
		}
		if (null != value && value.length != 0) {
			fmtString = String.format(fmtString, value);
		}
		debug(clazz, fmtString);
	}

	/**
	 * Info 输出
	 */
	public static void fmtInfo(Class<? extends Object> clazz, String fmtString, Object... value) {
		if (!isInfo)
			return;
		if (StringUtils.isBlank(fmtString)) {
			return;
		}
		if (null != value && value.length != 0) {
			fmtString = String.format(fmtString, value);
		}
		info(clazz, fmtString);
	}

	/**
	 * Error 输出
	 * 
	 * @param clazz
	 *            目标.Class
	 * @param message
	 *            输出信息
	 * @param e
	 *            异常类
	 */
	public static void error(Class<? extends Object> clazz, String message, Exception e) {
		Logger logger = LoggerFactory.getLogger(clazz);
		if (null == e) {
			logger.error(message);
			return;
		}
		logger.error(message, e);
	}

	/**
	 * Error 输出
	 * 
	 * @param clazz
	 *            目标.Class
	 * @param message
	 *            输出信息
	 */
	public static void error(Class<? extends Object> clazz, String message) {
		error(clazz, message, null);
	}

	/**
	 * 异常填充值输出
	 * 
	 * @param clazz
	 *            目标.Class
	 * @param fmtString
	 *            输出信息key
	 * @param e
	 *            异常类
	 * @param value
	 *            输出信息value
	 */
	public static void fmtError(Class<? extends Object> clazz, Exception e, String fmtString, Object... value) {
		if (StringUtils.isBlank(fmtString)) {
			return;
		}
		if (null != value && value.length != 0) {
			fmtString = String.format(fmtString, value);
		}
		error(clazz, fmtString, e);
	}

	/**
	 * 异常填充值输出
	 * 
	 * @param clazz
	 *            目标.Class
	 * @param fmtString
	 *            输出信息key
	 * @param value
	 *            输出信息value
	 */
	public static void fmtError(Class<? extends Object> clazz, String fmtString, Object... value) {
		if (StringUtils.isBlank(fmtString)) {
			return;
		}
		if (null != value && value.length != 0) {
			fmtString = String.format(fmtString, value);
		}
		error(clazz, fmtString);
	}
}
