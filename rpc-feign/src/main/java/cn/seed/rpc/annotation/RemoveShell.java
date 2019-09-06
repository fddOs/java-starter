package cn.seed.rpc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 此注解标记在请求的接口的方法上, 目前我们使用的网络请求库为 {@link feign.Feign}
 * 用于帮助使用者脱掉外层统一封装的 {@link cn.seed.common.core.Result}
 * 不使用注解的时候, 你需要如下使用
 * <pre>{@code
 *     Result<User> getUserByUserId(@NotNull String userId);
 * }</pre>
 * 当你使用 {@link RemoveShell} 注解, 你可以这样子使用
 * <pre>{@code
 *      @RemoveShell
 *      User getUserByUserId(@NotNull String userId);
 * }</pre>
 * <p>
 * 使用了此注解可以有两个好处：
 * 1. 在接口声明的时候, 不需要每次都写那个固定的 {@link cn.seed.common.core.Result},
 * 你可以让返回值是你原本的对象 {@link cn.seed.common.core.Result#result}
 * 2. 在使用上, 你可以对比以下两段代码
 * <pre>{@code
 *        // 使用前
 *        Result<User> result = xxx.getUserByUserId(5);
 *        if(result.getErrorCode() == 0){
 *              User user = result.getResult();
 *        }
 *        // 使用后
 *        User user = xxx.getUserByUserId(5);
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {METHOD})
public @interface RemoveShell {

    /**
     * 当用了注解之后, 会在 request 中生成一个名称为 {@link #HEADER_NAME} 的 header
     * response 中就可以判断这个名称可以判断到是否有这个注解
     */
    String HEADER_NAME = "removeShell";

    /**
     * 表示接口的返回值是否允许是 null 的值
     */
    String HEADER_VALUE_ALLOW_EMPTY = "allowEmpty";

    /**
     * 是否允许
     *
     * @return
     */
    boolean allowEmpty() default false;

}
