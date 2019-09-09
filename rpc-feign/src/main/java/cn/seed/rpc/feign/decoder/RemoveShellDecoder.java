package cn.seed.rpc.feign.decoder;

import cn.seed.common.core.ResultCode;
import cn.seed.rpc.annotation.RemoveShell;
import cn.seed.rpc.feign.RemoveShellException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 这个类用于解码 java 统一的 {@link cn.seed.common.core.Result}
 * 如果业务请求失败的情况下, 会抛出一个异常 {@link cn.seed.rpc.feign.RemoveShellException}
 *
 * @see cn.seed.rpc.annotation.RemoveShell
 * @see cn.seed.rpc.feign.decoder.RemoveShellDecoder
 * @see cn.seed.rpc.feign.RemoveShellException
 */
public class RemoveShellDecoder implements Decoder {

    private final Decoder nextDecoder;

    public RemoveShellDecoder(@NotNull Decoder nextDecoder) {
        this.nextDecoder = nextDecoder;
    }

    /**
     * 新的是 {@link cn.seed.common.core.Result}
     * 老的是 {code, message, data}
     */
    @Override
    public Object decode(Response response, Type type) throws IOException {

        // 如果方法上没有注解
        Collection<String> headerValues = response.request().headers().get(RemoveShell.HEADER_NAME);
        if (headerValues == null || headerValues.isEmpty()) {
            return nextDecoder.decode(response, type);
        }

        // Result 中的 result 是否允许为空
        boolean isAllowEmpty = headerValues.stream()
                .anyMatch(item -> "true".equalsIgnoreCase(item));

        try {
            String jsonBody = Util.toString(response.body().asReader());
            JSONObject jb = JSON.parseObject(jsonBody);
            // 兼容两种, 新的老的
            Integer errorCode = jb.getInteger("errorCode");
            Integer code = jb.getInteger("code");
            // 如果两个都是没有, 说明数据结构不符合逻辑, 直接抛出异常
            if (errorCode == null && code == null) {
                throw new RemoveShellException(ResultCode.HTTP_FAIL, "返回的 json 数据结构不满足 @RemoveShell 注解的脱壳要求");
            }
            // 错误信息, 可能为空
            String message = jb.getString("message");
            // 用户关心的数据
            String realData = null;
            // 表示成功
            if (errorCode != null && errorCode == ResultCode.SUCCESS.getCode()) {
                realData = jb.getString("result");
            } else if (code != null && code == 200) {
                realData = jb.getString("data");
            } else { // 其他的情况都是异常情况
                throw new RemoveShellException(errorCode == null ? code : errorCode, message);
            }

            // ================= 到这里业务请求是正确的, 现在开始处理 Result 中的 result 为空的情况 =================

            // 如果 result 中的 result 是空
            if ((realData == null || realData.isEmpty())) {
                // 如果允许为空, 并且正好result 中的 result 也是空, 那么返回 null
                if (isAllowEmpty) {
                    return null;
                } else {
                    throw new RemoveShellException(ResultCode.HTTP_FAIL, "Json 中的 result 不允许为空, 如果允许返回空, 请使用 @RemoveShell(allowEmpty = true)");
                }
            }

            // 到这里就拿到了真实的用户关心的数据
            Response nextResponse = response
                    .toBuilder()
                    .body(realData, Util.UTF_8)
                    .build();
            // 脱壳完成
            return nextDecoder.decode(nextResponse, type);
        } catch (Exception e) {
            throw new RemoveShellException(ResultCode.HTTP_FAIL, "Response 脱壳失败", e);
        }

    }

}
