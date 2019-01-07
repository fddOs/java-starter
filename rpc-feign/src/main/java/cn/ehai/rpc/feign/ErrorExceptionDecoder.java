package cn.ehai.rpc.feign;

import cn.ehai.common.core.*;
import cn.ehai.common.utils.EHIExceptionLogstashMarker;
import cn.ehai.common.utils.EHIExceptionMsgWrapper;
import cn.ehai.common.utils.LoggerUtils;
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
            LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper(getClass()
                    .getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new Object[]{arg0, arg1},
                    ExceptionUtils.getStackTrace(e))));
        }
        return new ExternalException(ResultCode.INTERNAL_SERVER_ERROR, "外部服务器异常：调用" + arg0 + "接口错误，错误码:" +
                arg1.status() + (StringUtils.isEmpty(parseError) ? "" : " 解析失败:" + parseError) + (StringUtils.isEmpty
                (errorMessage) ? "" : " 错误信息---" + errorMessage));
    }

}
