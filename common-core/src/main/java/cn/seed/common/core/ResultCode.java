package cn.seed.common.core;

/**
 * 返回业务状态码,>0则表示有异常(8位数字,前两位为开发组:自驾前台10,自驾后台11.代驾12,OA组13,报表14,移动15)
 */
public enum ResultCode {
    //成功
    SUCCESS(0),
    //失败
    FAIL(15000001),
    //未认证（签名错误）
    UNAUTHORIZED(15000401),
    //接口不存在
    NOT_FOUND(15000404),
    //服务器内部错误
    INTERNAL_SERVER_ERROR(15000500),
	BAD_REQUEST(15000403),
	HTTP_FAIL(15000100),
    //参数错误
	URL_ERROR(15000002),
    //数据异常
    DATA_ERROR(15000003),
    //redis异常
     REDIS_ERROR(15000004);

    private int code;

    ResultCode(int code) {
        this.code = code;
    }

	public int getCode() {
		return code;
	}

    
}
