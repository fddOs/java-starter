package cn.seed.rpc.annotation;

import cn.seed.common.core.Result;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * 此注解标记在请求的接口的方法上, 目前我们使用的网络请求库为 {@link feign.Feign}
 * 用于帮助使用者脱掉外层统一封装的 {@link cn.seed.common.core.Result}
 * 不使用注解的时候, 你需要如下使用
 * <pre>
 *
 *      Result&lt;User&gt; getUserByUserId(@NotNull String userId);
 * </pre>
 * 当你使用 {@link RemoveShell} 注解, 你可以这样子使用
 * <pre>
 *
 *      &#64;RemoveShell
 *      User getUserByUserId(@NotNull String userId);
 * </pre>
 * 使用了此注解可以有两个好处：
 * <ol>
 * <li> 在接口声明的时候, 不需要每次都写那个固定的 {@link cn.seed.common.core.Result},
 * 你可以让返回值是你原本的对象 {@link Result#getResult()}
 * <li> 在使用上, 你可以对比以下两段代码
 * <pre>
 *
 *      // 使用前
 *      Result&lt;User&gt; result = xxx.getUserByUserId(5);
 *      if(result.getErrorCode() == 0){
 *          User user = result.getResult();
 *      }
 *      // 使用后
 *      User user = xxx.getUserByUserId(5);
 * </pre>
 * </ol>
 * 使用此功能, 会有个地方需要关注下, 以前 json 是映射整个 {@link cn.seed.common.core.Result}
 * 现在可以映射内部的 {@link Result#getResult()}, 所以有可能会产生一个情况
 * 当后台返回如下的格式的数据时, 内部的 result 是 null 或者是一个 "", 那么这时候是否允许为空会是一个问题
 * <pre>
 * {
 *      errorCode : 0,
 *      message : "ok",
 *      result : null
 * }
 * </pre>
 * 所以注解中添加了一个参数 {@link RemoveShell#allowEmpty()} 来控制是否允许为空
 * <p>
 * 如下代码就可允许 {@code User} 返回 {@code null}, 否则你将得到一个异常 {@link cn.seed.rpc.feign.RemoveShellException}
 * <pre>
 *      &#64;RemoveShell(allowEmpty = true)
 *      User getUserByUserId(@NotNull String userId);
 * </pre>
 *
 * @author xiaojinzi
 * @since 0.7.6
 */
@Target(value = {METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoveShell {

    /**
     * 当用了注解之后, 会在 request 中生成一个名称为 {@link #HEADER_NAME} 的 header
     * response 中就可以判断这个名称可以判断到是否有这个注解
     */
    String HEADER_NAME = "removeShell";

    /**
     * 我们 Java 组规定最外层返回的 Json 格式符合
     * {@link cn.seed.common.core.Result} 对象的格式.
     * 但是脱壳的时候, 难免会有 {@link Result#getResult()} 为空的时候,
     * 比如一个查询接口, 如果没有查询到是业务上的一种正确的行为,
     * 那么{@link Result#getResult()} 可能就是 null 的, 但是脱壳的时候,
     * 默认 {@link Result#getResult()} 是不能够为空的, 所以增加此字段来区分这两种场景
     *
     * @return 是否允许 {@link Result#getResult()} 为空
     */
    boolean allowEmpty() default false;

}
