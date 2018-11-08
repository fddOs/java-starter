package cn.ehai.javautils.core;

import com.alibaba.fastjson.JSON;

/**
 * 统一API响应结果封装
 */
public class Result<T> {
	/**
	 * 返回业务状态码,>0则表示有异常(8位数字,前两位为开发组:自驾前台10,自驾后台11.代驾12,OA组13,报表14,移动15)
	 */
	private int errorCode;
	private String message;
	private T result;
	
	public Result() {
		
	}

    public Result<T> setErrorCode(ResultCode resultCode) {
        this.errorCode = resultCode.getCode();
        return this;
    }
	public int getErrorCode() {
		return errorCode;
	}

	public Result<T> setErrorCode(int errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public Result<T> setMessage(String message) {
		this.message = message;
		return this;
	}


	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
