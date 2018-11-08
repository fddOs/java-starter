package cn.ehai.javautils.core;

/**
 * 响应结果生成工具
 */
public class ResultGenerator {
	private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

	public static <T> Result<T> genSuccessResult() {
		return result(null, ResultCode.SUCCESS, DEFAULT_SUCCESS_MESSAGE);
	}

	private static <T> Result<T> result(T data, ResultCode resultCode, String message) {
		Result<T> result = new Result<T>();
		result.setErrorCode(resultCode);
		result.setMessage(message);
		result.setResult(data);
		return result;
	}

	public static <T> Result<T> genSuccessResult(T data) {
		Result<T> result = new Result<>();
		result.setErrorCode(ResultCode.SUCCESS);
		result.setMessage(DEFAULT_SUCCESS_MESSAGE);
		result.setResult(data);
		return result;
	}

	public static <T> Result<T> genFailResult(ResultCode errorCode,String message) {
		return result(null, errorCode, message);
	}
	public static <T> Result<T> genFailResult(String message) {
		return result(null, ResultCode.FAIL, message);
	}
}
