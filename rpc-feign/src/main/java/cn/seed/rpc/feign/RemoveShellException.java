package cn.seed.rpc.feign;

import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import cn.seed.rpc.annotation.RemoveShell;

/**
 * 此异常表示脱壳的时候, 如果业务请求失败, 会有此异常
 *
 * @see RemoveShell
 * @see cn.seed.rpc.feign.decoder.RemoveShellDecoder
 * @see cn.seed.rpc.feign.RemoveShellException
 */
public class RemoveShellException extends ServiceException {

    public RemoveShellException() {
    }

    public RemoveShellException(ResultCode code, String message) {
        super(code, message);
    }

    public RemoveShellException(int code, String message) {
        super(code, message);
    }

    public RemoveShellException(ResultCode code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public RemoveShellException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

}
