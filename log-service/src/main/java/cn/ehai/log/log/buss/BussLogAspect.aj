package cn.ehai.log.log.buss;

import cn.ehai.common.utils.EHIExceptionLogstashMarker;
import cn.ehai.common.utils.EHIExceptionMsgWrapper;
import cn.ehai.common.utils.LoggerUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

/**
 *  业务日志切面
 * @author lixiao
 * @date 2019-02-14 17:59
 */
@Aspect
public class BussLogAspect {


    /**
     * // TODO:
     * @param pjp
     * @return void
     * @author lixiao
     * @date 2019-02-14 14:47
     */
    @Around(value = "execution(* ((cn.ehai.*.*.controller.*)||(cn.ehai.*.*.service.impl.* && !cn.ehai.*.actionlog.service.impl.*)).*(..)))&& @annotation(cn.ehai.log.log.buss.BussinessLog)")
    public void bussLogAction(ProceedingJoinPoint pjp) throws Throwable {
        pjp.proceed();
        //获取且面方法的参数信息
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Object[] arguments = pjp.getArgs();
        String[] params = ((MethodSignature) pjp.getSignature()).getParameterNames();

    }


    /**
     * 记录业务日志
     * @param method 方法
     * @param arguments 方法的参数
     * @param params 方法参数名称
     * @return void
     * @author lixiao
     * @date 2019-02-14 15:31
     */
    private void recordLog(Method method,Object[] arguments,String[] params){

        Objects.requireNonNull(method);

        BussinessLog bussinessLog =  method.getAnnotation(BussinessLog.class);

        //获取操作人
    }

    /**
     * 获取方法里面的参数
     * @param arguments 方法参数值数组
     * @param params 方法参数数组
     * @param choiceParam 注解中指定的参数名称
     * @param index 参数的位置
     * @return java.lang.Object 获取的方法参数的值
     * @author lixiao
     * @date 2019-02-14 15:51
     */
    private Object methodParams(Object[] arguments,String[] params,String choiceParam,int index){
        //如果注解上未指定要换取的参数名称 返回""
        if(StringUtils.isEmpty(choiceParam)){
            return "";
        }
        //index默认为0
        if(index<0||index>params.length){
            index=0;
        }
        //如果当前index的参数名称和指定的参数名称相等，则直接返回参数的值
        if(choiceParam.equalsIgnoreCase(params[index])){
            return arguments[index];
        }else{
            //如果不相等、则获取参数对象里面是否有指定的参数模型
            Object result = null;
            try {
                result = PropertyUtils.getProperty(arguments[index], choiceParam);
            } catch  (Exception e) {
                //LoggerUtils.error(getClass(), new EHIExceptionLogstashMarker(new EHIExceptionMsgWrapper(getClass()
                //    .getName(), Thread.currentThread().getStackTrace()[1].getMethodName(), new
                //    Object[]{choiceParam, arguments, params}, ExceptionUtils.getStackTrace(e))));
            }
            return result==null?"":result;
        }


    }

}
