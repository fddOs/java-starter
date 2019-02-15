package cn.ehai.log.log.buss;

import brave.internal.HexCodec;
import brave.opentracing.BraveSpanContext;
import brave.propagation.TraceContext;
import cn.ehai.common.utils.EHIExceptionLogstashMarker;
import cn.ehai.common.utils.EHIExceptionMsgWrapper;
import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.log.dao.BusinessLogMapper;
import cn.ehai.log.entity.BusinessLog;
import com.alibaba.fastjson.JSONObject;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import io.opentracing.Tracer;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *  业务日志切面
 * @author lixiao
 * @date 2019-02-14 17:59
 */
@Aspect
public class BussLogAspect {
    private static final String HEADER_JWT_USER_ID = "jwt-user-id";

    @Autowired
    private Tracer tracer;

    @Autowired
    private BusinessLogMapper businessLogMapper;

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
        int actionType = bussinessLog.actionType();
        //获取操作人
        String orderID = (String)methodParams(arguments,params,bussinessLog.oprNo(),bussinessLog
            .referNoNum());
        if(StringUtils.isEmpty(orderID)){
            HttpServletRequest request = null;
            try {
                request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
                orderID = request.getHeader(HEADER_JWT_USER_ID);
            }catch (Exception e){
                LoggerUtils.error(getClass(), new Object[]{ arguments, params}, e);
            }
        }
        //关联单号
        String referId = (String)methodParams(arguments,params,bussinessLog.referNo(),
            bussinessLog.referNoNum());
        //用户id
        String userId = (String)methodParams(arguments,params,bussinessLog.userId(),bussinessLog.userIdNum());
        //要记录的表名
        String oprTableName = bussinessLog.oprTableName();
        //附加信息-JSON
        String extend = JSONObject.toJSONString(
            new ExtendsJson(methodParams(arguments,params,bussinessLog.extend(),bussinessLog.extendNum())));

        //traceId
        String traceId=requestTraceId();

        businessLogMapper.insert(createBussnissLog(actionType,orderID,referId,userId,oprTableName,extend,traceId));
    }


    private BusinessLog createBussnissLog(int actionType,String orderId,String referId,String userId,String oprTableName,String extend,String traceId){
        BusinessLog businessLog = new BusinessLog();
        businessLog.setActionType(actionType);
        businessLog.setOprNo(orderId);
        businessLog.setExtendContent(extend);
        businessLog.setOprTableName(oprTableName);
        businessLog.setReferId(referId);
        businessLog.setTraceId(traceId);
        businessLog.setUserId(userId);
        return businessLog;
    }

    /**
     * 获取这次请求的traceid
     * @param
     * @return java.lang.String
     * @author lixiao
     * @date 2019-02-15 16:12
     */
    private String requestTraceId(){
        String traceId="";
        Scope serverSpan = tracer.scopeManager().active();
        if (serverSpan != null) {
            SpanContext spanContext = serverSpan.span().context();
            if (spanContext instanceof BraveSpanContext) {
                TraceContext traceContext = ((BraveSpanContext) spanContext).unwrap();
                traceId = HexCodec.toLowerHex(traceContext.traceId());
            }
        }
        return traceId;
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
                LoggerUtils.error(getClass(), new Object[]{choiceParam, arguments, params}, e);
            }
            return result==null?"":result;
        }


    }

}
