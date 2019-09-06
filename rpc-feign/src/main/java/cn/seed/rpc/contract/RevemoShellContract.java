package cn.seed.rpc.contract;

import cn.seed.rpc.annotation.RemoveShell;
import feign.Contract;
import feign.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 用于在 {@link feign.Request} 中生成一个名称为 {@link RemoveShell#HEADER_NAME} 的 header
 * 用于标记这个请求的 response 是需要脱壳的
 *
 * @see RemoveShell
 * @see cn.seed.rpc.feign.decoder.RemoveShellDecoder
 * @see cn.seed.rpc.feign.RemoveShellException
 */
public class RevemoShellContract extends Contract.Default {

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
        if (methodAnnotation.annotationType() == RemoveShell.class) {
            RemoveShell removeShellAnno = RemoveShell.class.cast(methodAnnotation);
            data.template().header(RemoveShell.HEADER_NAME, String.valueOf(removeShellAnno.allowEmpty()));
        }
        super.processAnnotationOnMethod(data, methodAnnotation, method);
    }

}
