package cn.seed.rpc.feign;

import cn.seed.common.core.ExternalException;
import cn.seed.common.core.Result;
import cn.seed.common.core.ResultCode;
import cn.seed.common.utils.LoggerUtils;
import com.alibaba.fastjson.JSONException;
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
        String parseError = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        String bodyString = "";
        try (InputStream in = arg1.body().asInputStream()) {
            while ((length = in.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            bodyString = bos.toString(StandardCharsets.UTF_8.name());
            Result result = JSONObject.parseObject(bodyString, Result.class);
            if (result != null) {
                errorMessage = result.getMessage();
            }
        } catch (JSONException e) {
            parseError = "ResponseBody:" + bodyString + " Exception:" + ExceptionUtils.getStackTrace(e);
        } catch (Exception e) {
            // TODO
            LoggerUtils.error(getClass(), new Object[]{arg0, arg1}, e);
        }
        return new ExternalException(
            ResultCode.INTERNAL_SERVER_ERROR, "外部服务器异常：调用" + arg0 + "接口错误，错误码:" +
                arg1.status() + (StringUtils.isEmpty(parseError) ? "" : " 解析失败:" + parseError) + (StringUtils.isEmpty
                (errorMessage) ? "" : " 错误信息---" + errorMessage));
    }

}
