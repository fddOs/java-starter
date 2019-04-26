package cn.seed.common.core;

/**
 * 服务（业务）异常如“ 账号或密码错误 ”，该异常只做INFO级别的日志记录 @see WebMvcConfigurer
 */
public class ServiceException extends RuntimeException {

	/**
	 * @Description:TODO
	 * @author:lixiao
	 * @time:2018年1月9日 上午11:12:06
	 */
	private static final long serialVersionUID = 1L;
	private int resultCode = ResultCode.FAIL.getCode();

	public ServiceException() {
	}

	public ServiceException(ResultCode code, String message) {
		super(message);
		this.resultCode = code.getCode();
	}

	public ServiceException(int code, String message) {
		super(message);
		this.resultCode = code;
	}
	public ServiceException(ResultCode code, String message, Throwable cause) {
		super(message, cause);
		this.resultCode = code.getCode();
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
}
