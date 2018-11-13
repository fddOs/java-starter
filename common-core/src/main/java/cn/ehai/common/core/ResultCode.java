package cn.ehai.common.core;

/**
 * 返回业务状态码,>0则表示有异常(8位数字,前两位为开发组:自驾前台10,自驾后台11.代驾12,OA组13,报表14,移动15)
 */
public enum ResultCode {
    SUCCESS(0),//成功
    FAIL(400),//失败
    UNAUTHORIZED(401),//未认证（签名错误）
    NOT_FOUND(404),//接口不存在
    INTERNAL_SERVER_ERROR(500),//服务器内部错误
	BAD_REQUEST(403),
	HTTP_FAIL(100),
	URL_ERROR(150000001);//参数错误

    private int code;

    ResultCode(int code) {
        this.code = code;
    }

	public int getCode() {
		return code;
	}

    
}
