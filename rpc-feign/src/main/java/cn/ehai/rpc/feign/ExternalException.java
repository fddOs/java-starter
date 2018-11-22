package cn.ehai.rpc.feign;

import cn.ehai.common.core.ResultCode;

public class ExternalException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private ResultCode code = ResultCode.FAIL;

    public ExternalException() {
    }

    public ExternalException(ResultCode code, String message) {
        super(message);
        this.code = code;
    }

    public ExternalException(ResultCode code, String message, Throwable cause) {
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
