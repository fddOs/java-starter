package cn.ehai.log.log.business;

import brave.internal.HexCodec;
import brave.opentracing.BraveSpanContext;
import brave.propagation.TraceContext;
import cn.ehai.common.utils.LoggerUtils;
import cn.ehai.common.utils.ProjectInfoUtils;
import cn.ehai.log.dao.BusinessLogMapper;
import cn.ehai.log.log.OprNoUtils;
import com.alibaba.fastjson.JSONObject;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import java.lang.reflect.Method;
import java.util.Objects;
import io.opentracing.Tracer;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *  业务日志切面
 * @author lixiao
 * @date 2019-02-14 17:59
 */
@Aspect
@Component
public class BusLogAspect {
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
    @AfterReturning(value = "@annotation(cn.ehai.log.log.business.BusinessLog)")
    public void bussLogAction(JoinPoint pjp) throws Throwable {
        //获取且面方法的参数信息
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Object[] arguments = pjp.getArgs();
        String[] params = ((MethodSignature) pjp.getSignature()).getParameterNames();
        recordLog(method,arguments,params);
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

        BusinessLog businessLog =  method.getAnnotation(BusinessLog.class);
        int actionType = businessLog.actionType();
        //获取操作人
        String oprNo = String.valueOf(methodParams(arguments,params,businessLog.oprNo(),businessLog
            .referNoNum()));
        if(StringUtils.isEmpty(oprNo)){
            HttpServletRequest request = null;
            try {
                request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
                oprNo = request.getHeader(HEADER_JWT_USER_ID);
            }catch (Exception e){
                LoggerUtils.error(getClass(), new Object[]{ arguments, params}, e);
            }finally {
                oprNo=OprNoUtils.handlerOprNo(oprNo);
            }
        }

        //关联单号
        String referId = String.valueOf(methodParams(arguments,params,businessLog.referNo(),
            businessLog.referNoNum()));
        //用户id
        String userId = String.valueOf(methodParams(arguments,params,businessLog.userId(),businessLog.userIdNum()));
        //要记录的表名
        String oprTableName = businessLog.oprTableName();
        //附加信息-JSON
        String extend = JSONObject.toJSONString(
            new ExtendsJson(methodParams(arguments,params,businessLog.extend(),businessLog.extendNum())));

        //traceId
        String traceId=requestTraceId();
        try{
            businessLogMapper.insert(createBusinessLog(actionType,oprNo,referId,userId,oprTableName,extend,traceId));
        }catch (Exception e){
            LoggerUtils.error(BusLogAspect.class,new Object[]{method.getName()},e);
        }
    }


    private cn.ehai.log.entity.BusinessLog createBusinessLog(int actionType,String optNo,String referId,String userId,String oprTableName,String extend,String traceId){
        cn.ehai.log.entity.BusinessLog businessLog = new cn.ehai.log.entity.BusinessLog();
        businessLog.setActionType(actionType);
        businessLog.setOprNo(handleString(optNo));
        businessLog.setExtendContent(extend);
        businessLog.setOprTableName(handleString(oprTableName));
        businessLog.setReferId(handleString(referId));
        businessLog.setTraceId(handleString(traceId));
        businessLog.setUserId(handleString(userId));
        businessLog.setSysName(handleString(ProjectInfoUtils.getProjectContext()));
        return businessLog;
    }


    private String handleString(String string){
        return string==null?"":string;
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
