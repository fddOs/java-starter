package cn.ehai.common.core;

/**
 * 响应结果生成工具
 */
public class ResultGenerator {
	private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

	public static <T> Result<T> genSuccessResult() {
		return result(null, ResultCode.SUCCESS, DEFAULT_SUCCESS_MESSAGE);
	}

	private static <T> Result<T> result(T data, ResultCode resultCode, String message) {
		return result(null, resultCode.getCode(), message);
	}

	public static <T> Result<T> genSuccessResult(T data) {
		return genSuccessResult(data,DEFAULT_SUCCESS_MESSAGE);
	}

	public static <T> Result<T> genSuccessResult(T data,String msg) {
		return result(data, ResultCode.SUCCESS.getCode(), msg);
	}
	public static <T> Result<T> genFailResult(ResultCode errorCode,String message) {
		return result(null, errorCode, message);
	}
	public static <T> Result<T> genFailResult(String message) {
		return result(null, ResultCode.FAIL, message);
	}

	public static <T> Result<T> result(T data, Integer resultCode, String message) {
		Result<T> result = new Result<T>();
		result.setErrorCode(resultCode);
		result.setMessage(message);
		result.setResult(data);
		return result;
	}
}
