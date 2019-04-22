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
	private ResultCode code = ResultCode.FAIL;

	public ServiceException() {
	}

	public ServiceException(ResultCode code, String message) {
		super(message);
		this.code = code;
	}

	public ServiceException(ResultCode code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public ResultCode getCode() {
		return code;
	}

	public void setCode(ResultCode code) {
		this.code = code;
	}

}
