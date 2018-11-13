package cn.ehai.rpc.feign;

import cn.ehai.common.core.ResultCode;
import cn.ehai.common.core.ServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class ErrorExceptionDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String arg0, Response arg1) {
        return new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "调用" + arg0 + "接口错误，错误码-" + arg1.status());
    }

}
