package cn.ehai.rpc.feign;

import cn.ehai.common.core.Result;
import cn.ehai.common.core.ResultCode;
import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.common.utils.UuidUtils;
import com.alibaba.fastjson.JSONObject;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ErrorExceptionDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String arg0, Response arg1) {
        String errorMessage = null;
        if (arg1.status() == HttpCodeEnum.CODE_516.getCode() || arg1.status() == HttpCodeEnum.CODE_518.getCode()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            try (InputStream in = arg1.body().asInputStream()) {
                while ((length = in.read(buffer)) != -1) {
                    bos.write(buffer, 0, length);
                }
                Result result = JSONObject.parseObject(bos.toString(StandardCharsets.UTF_8.name()), Result.class);
                errorMessage = result.getMessage();
            } catch (Exception e) {
                // TODO
                LoggerUtils.error(getClass(), ExceptionUtils.getStackTrace(e));
            }
        }
        return new ExternalException(ResultCode.INTERNAL_SERVER_ERROR, "外部服务器异常：调用" + arg0 + "接口错误，错误码-517" +
                arg1.status() + (StringUtils.isEmpty(errorMessage) ? "" : " 错误信息-" + errorMessage));
    }

}
