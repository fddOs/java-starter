package cn.seed.rpc.feign;

import cn.seed.common.core.ResultCode;
import cn.seed.common.core.ServiceException;
import feign.Contract;
import feign.MethodMetadata;
import org.springframework.http.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * SeedContract
 *
 * @author:方典典
 * @time:2019/8/1 17:46
 */
@Deprecated
public class SeedContract extends Contract.Default {
    private boolean vailParam = true;
    private String methodInfo;

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation,
                                             Method method) {
        super.processAnnotationOnMethod(data, methodAnnotation, method);
        methodInfo = method.toString();
    }

    @Override
    protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations,
                                                    int paramIndex) {
        boolean bool = super.processAnnotationsOnParameter(data, annotations, paramIndex);
        if (data.template().body() != null && data.template().method().equalsIgnoreCase(HttpMethod.GET.name())) {
            for (Annotation a : annotations) {
                if (a instanceof ParamFromBody) {
                    vailParam = false;
                }
            }
            if (vailParam) {
                throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR, "api[" + methodInfo + "] " +
                        "参数异常");
            }
        }
        return bool;
    }
}
