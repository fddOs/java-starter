package cn.ehai.log.log;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import cn.ehai.common.core.ApolloBaseConfig;
import cn.ehai.common.core.SpringContext;
import cn.ehai.log.entity.ActionLog;
import cn.ehai.log.service.ActionLogService;
import cn.ehai.log.service.impl.ActionLogServiceAsync;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSONArray;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:业务日志记录
 * @author:方典典
 * @time:2017年12月14日 下午5:42:30
 */
@Aspect
@Order(1)
@Component
public class LogAspect {

    private Map<Long, ActionLog> actionLogMap = new HashMap<>();
    private static final String HEADER_JWT_USER_ID="jwt-user-id";
    /**
     * @param pjp void
     * @return
     * @throws Throwable
     * @Description:获取业务日志参数
     * @exception:
     * @author: 方典典
     * @time:2017年12月18日 下午3:01:34
     */
    @Around(value = "execution(* (cn.ehai.*.*.service.impl.* && !cn.ehai.*.actionlog.service.impl.*).*(..)))")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Long key = Thread.currentThread().getId();
        ActionLog actionLog = actionLogMap.get(key);
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String actionDateTime = simpleFormat.format(new Date());
        // 获取方法
        MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) pjp;
        MethodSignature signature = (MethodSignature) mjp.getSignature();
        Method method = signature.getMethod();
        // 获取方法上的注解
        ServiceAnnotation actionLogAnnotation = method.getAnnotation(ServiceAnnotation.class);
        StringBuilder methodName = new StringBuilder();
        methodName.append(method.getName());
        methodName.append("(");
        methodName.append(")");
        // 获取Request
        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception e) {
            return pjp.proceed();
        }
        if (request == null) {
            return pjp.proceed();
        }
        String url = request.getServletPath();
        String oprNo = request.getParameter("oprNo");
        String referId = request.getParameter("referId");
        String userId = request.getParameter("userId");
        if(StringUtils.isEmpty(userId)){
            userId = request.getHeader(HEADER_JWT_USER_ID);
        }
        if (actionLog != null && url.equals(actionLog.getUrl())) {
            return pjp.proceed();
        }
        actionLog = new ActionLog();
        actionLog.setOprNo(oprNo == null ? "" : oprNo);
        actionLog.setReferId(referId == null ? "" : referId);
        actionLog.setUrl(url);
        actionLog.setUserId(userId == null ? "" : userId);
        actionLog.setActionType(ServiceActionTypeEnum.OTHER_RECORD.getActionType());
        if (actionLogAnnotation != null) {
            actionLog.setActionType(actionLogAnnotation.value());
        }
        actionLog.setMethodName(methodName.toString());
        actionLog.setActionDatetime(actionDateTime);
        actionLogMap.put(Thread.currentThread().getId(), actionLog);
        return pjp.proceed();
    }

    /**
     * @Description:将update转成select
     * @params:[map]
     * @return:java.lang.String
     * @exception:
     * @author: 方典典
     * @time:2018/9/28 9:25
     */
    public String getSelectSQL(Map<String, Object> map) {
        Map<String, String> items = (Map<String, String>) map.get("items");
        StringBuffer sql = new StringBuffer("select ");
        String column = StringUtils.arrayToDelimitedString(items.keySet().toArray(), ",");
        sql.append(column);
        sql.append(" from ");
        sql.append(map.get("tables"));
        sql.append(" where ");
        sql.append(map.get("where"));
        return SQLUtils.format(sql.toString(), JdbcConstants.MYSQL);
    }

    /**
     * @Description:解析SQL
     * @params:[sql]
     * @return:java.util.Map<java.lang.String,java.lang.Object>
     * @exception:
     * @author: 方典典
     * @time:2018/9/28 9:26
     */
    public static Map<String, Object> convertSQL(String sql) {
        Map<String, Object> map = new HashMap<>();
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        EHISqlASTVisitor visitor = new EHISqlASTVisitor();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        map.put("items", visitor.getItems());
        map.put("tables", StringUtils.arrayToDelimitedString(visitor.getTables().toArray(), ","));
        map.put("where", visitor.getWhere());
        map.put("complex", visitor.isComplex());
        map.put("replace", visitor.isReplace());
        return map;
    }

    /**
     * @Description:拦截处理SQL
     * @params:[pjp]
     * @return:java.lang.Object
     * @exception:
     * @author: 方典典
     * @time:2018/9/28 9:26
     */
    @Around(value = "execution(* com.alibaba.druid.filter.FilterEventAdapter.preparedStatement_execute(..))")
    public Object druidInterceptNew(ProceedingJoinPoint pjp) throws Throwable {
        Long key = Thread.currentThread().getId();
        PreparedStatementProxy statement = (PreparedStatementProxy) pjp.getArgs()[1];
        // 无sql直接执行目标方法
        if (statement == null || statement.getSqlStat() == null) {
            return pjp.proceed();
        }
        String sql = statement.getSqlStat().getSql();
        // 目标sql的所有参数
        List<JdbcParameter> paramList = new ArrayList<>(statement.getParameters().values());
        sql = SQLUtils.format(sql, JdbcConstants.MYSQL, paramList.stream().map(JdbcParameter::getValue).collect
                (Collectors.toList()));
        Map<String, Object> map = convertSQL(sql);
        boolean isReplace = (boolean) map.get("replace");
        if (StringUtils.isEmpty((String) map.get("where")) && !isReplace) {
//            actionLogMap.remove(key);
            return pjp.proceed();
        }
        ActionLog actionLog = actionLogMap.get(key);
        if (null == actionLog) {
//            actionLogMap.remove(key);
            return pjp.proceed();
        }
        boolean bool = "info".equalsIgnoreCase(ApolloBaseConfig.getServiceLogLevel())
                && (actionLog.getActionType() == null || actionLog.getActionType().intValue() == 7);
        if ("none".equalsIgnoreCase(ApolloBaseConfig.getServiceLogLevel()) || bool) {
            actionLogMap.remove(key);
            return pjp.proceed();
        }
        // 表名
        String tables = (String) map.get("tables");
        // 对业务日志表的更新操作不进行拦截
        if (tables.contains("action_log")) {
//            actionLogMap.remove(key);
            return pjp.proceed();
        }
        Object result;
        actionLog.setIsSuccess(true);
        actionLog.setOprTableName(tables);
        if (isReplace) {
            actionLog.setOriginalValue("{\"type\": \"replace\"}");
            actionLog.setNewValue("{\"type\": \"" + SQLUtils.formatMySql(sql, new SQLUtils.FormatOption(true, false))
                    + "\"}");
            result = pjp.proceed();
        } else {
            // 获取查询老值的sql
            String selectSql = getSelectSQL(map);
            ActionLogService actionLogService = SpringContext.getApplicationContext().getBean(ActionLogService.class);
            List<Map<String, String>> oldParamList = actionLogService.selectBySql(selectSql);
            actionLog.setOriginalValue(JSONArray.toJSONString(oldParamList));
            result = pjp.proceed();
            if (!(boolean) map.get("complex")) {
                // 非复杂SQL
                actionLog.setNewValue(JSONObject.toJSONString(map.get("items")));
            } else {
                List<Map<String, String>> newParamList = actionLogService.selectBySql(selectSql);
                actionLog.setNewValue(JSONArray.toJSONString(newParamList));
            }
        }
        SpringContext.getApplicationContext().getBean(ActionLogServiceAsync.class).insertServiceLogAsync(actionLog);
        actionLogMap.remove(key);
        return result;
    }

}