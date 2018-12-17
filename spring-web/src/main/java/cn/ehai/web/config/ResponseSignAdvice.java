package cn.ehai.web.config;

import cn.ehai.common.utils.SignUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static cn.ehai.web.common.SignConfig.SIGN_HEADER;

/**
 * 返回统一签名
 *
 * @author lixiao
 * @date 2018/12/15 10:38
 */
@RestControllerAdvice(basePackages = "cn.ehai")
public class ResponseSignAdvice implements ResponseBodyAdvice<Object> {


    @Override public boolean supports(MethodParameter methodParameter,
        Class<? extends HttpMessageConverter<?>> aClass) {
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
        Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
        ServerHttpResponse serverHttpResponse) {

        if(body==null){
            return null;
        }
        serverHttpResponse.getHeaders().add(SIGN_HEADER,SignUtils.signResponse(JSONObject.toJSONString(body,
                SerializerFeature.WRITE_MAP_NULL_FEATURES,SerializerFeature.QuoteFieldNames,SerializerFeature.SortField)));
        return body;
    }
}
