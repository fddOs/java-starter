package cn.seed.log.log;

import brave.internal.HexCodec;
import brave.opentracing.BraveSpanContext;
import brave.propagation.TraceContext;
import cn.seed.common.core.ApolloBaseConfig;
import cn.seed.common.core.SpringContext;
import cn.seed.common.utils.AESUtils;
import cn.seed.log.entity.ActionLog;
import cn.seed.log.entity.BusinessLogValue;
import cn.seed.log.log.business.BusinessTableUtils;
import cn.seed.log.service.ActionLogService;
import cn.seed.log.service.BusinessService;
import cn.seed.log.service.impl.ActionLogServiceAsync;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.JDBC4PreparedStatement;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    private static final String HEADER_JWT_USER_ID = "jwt-user-id";
    private static final String NEED_AES_HANDLE = "needAesHandle";
    @Autowired
    private Tracer tracer;
    @Autowired
    private BusinessService businessService;

    /**
     * @param pjp void
     * @return
     * @throws Throwable
     * @Description:获取业务日志参数
     * @exception:
     * @author: 方典典
     * @time:2017年12月18日 下午3:01:34 cn.ehai.report.api.service.impl.ReportServiceSettingServiceImpl
     */
    @Around(value = "execution(* (cn.*.*.*.service.impl.* && !cn.seed.log.service.impl.*).*(..)))")
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
        ServiceParamsAnnotation serviceParamsAnnotation = method.getAnnotation(ServiceParamsAnnotation.class);
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
        if (serviceParamsAnnotation != null) {
            Object[] params = pjp.getArgs();
            int referIdIndex = serviceParamsAnnotation.referIdIndex();
            int userIdIndex = serviceParamsAnnotation.userIdIndex();
            if (referIdIndex < params.length) {
                referId = (String) params[referIdIndex];
            }
            if (userIdIndex < params.length) {
                userId = (String) params[userIdIndex];
            }
        }
        if (StringUtils.isEmpty(oprNo)) {
            oprNo = request.getHeader(HEADER_JWT_USER_ID);
        }
        oprNo = OprNoUtils.handlerOprNo(oprNo);
        if (actionLog != null && url.equals(actionLog.getUrl())) {
            return pjp.proceed();
        }
        actionLog = new ActionLog();
        actionLog.setOprNo(oprNo == null ? "" : oprNo);
        actionLog.setReferId(referId == null ? "" : referId);
        actionLog.setUrl(url);
        actionLog.setUserId(userId == null ? "" : userId);
        actionLog.setActionType(ServiceActionTypeEnum.OTHER_RECORD.getActionType());
        actionLog.setTraceId(requestTraceId());
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
        sql.append(StringUtils.arrayToDelimitedString(((List<String>) map.get("tables")).toArray(), ","));
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
        CustomizeSqlASTVisitor visitor = new CustomizeSqlASTVisitor();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
        map.put("items", visitor.getItems());
        map.put("tables", visitor.getTables());
        map.put("where", visitor.getWhere());
        map.put("complex", visitor.isComplex());
        map.put("replace", visitor.isReplace());
        map.put("sql", visitor.getSql());
        map.put("needAesHandle", visitor.isNeedAesHandle());
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
        if (sql.contains(AESUtils.KEY)) {
            return pjp.proceed();
        }
        // 目标sql的所有参数
        List<JdbcParameter> paramList = new ArrayList<>(statement.getParameters().values());
        sql = SQLUtils.format(sql, JdbcConstants.MYSQL, paramList.stream().map(JdbcParameter::getValue).collect
                (Collectors.toList()));
        Map<String, Object> map = convertSQL(sql);
        sql = StringUtils.isEmpty((String) map.get("sql")) ? sql : (String) map.get("sql");
        boolean isReplace = (boolean) map.get("replace");
        boolean needAesHandle = (boolean) map.get(NEED_AES_HANDLE);
        if (StringUtils.isEmpty(map.get("where")) && !isReplace) {
            return targetRun(pjp, needAesHandle, sql, statement);
        }
        ActionLog actionLog = actionLogMap.get(key);
        if (null == actionLog) {
            return targetRun(pjp, needAesHandle, sql, statement);
        }
        boolean closeCommonServiceLog = "false".equalsIgnoreCase(ApolloBaseConfig.getServiceCommonLogLevel());
        boolean closeAnnotationServiceLog = (!"true".equalsIgnoreCase(ApolloBaseConfig.getServiceAnnotationLogLevel()
        ) || ("true".equalsIgnoreCase(ApolloBaseConfig.getServiceAnnotationLogLevel()) && (actionLog.getActionType()
                == null || actionLog.getActionType().intValue() == 7)));
        if (closeCommonServiceLog && closeAnnotationServiceLog) {
            actionLogMap.remove(key);
            return targetRun(pjp, needAesHandle, sql, statement);
        }
        // 表名
        List<String> tableList = (List<String>) map.get("tables");
        String tables = StringUtils.arrayToDelimitedString(tableList.toArray(), ",");
        String originalValue;
        String newValue;
        Object result;
        actionLog.setIsSuccess(true);
        actionLog.setOprTableName(tables);
        if (isReplace) {
//            actionLog.setOriginalValue("{\"type\": \"replace\"}");
            originalValue = "{\"type\": \"replace\"}";
//            actionLog.setNewValue("{\"type\": \"" + SQLUtils.formatMySql(sql, new SQLUtils.FormatOption(true, false))
//                    + "\"}");
            newValue = "{\"type\": \"" + SQLUtils.formatMySql(sql, new SQLUtils.FormatOption(true, false))
                    + "\"}";
            result = targetRun(pjp, needAesHandle, sql, statement);
        } else {
            // 获取查询老值的sql
            String selectSql = getSelectSQL(map);
            ActionLogService actionLogService = SpringContext.getApplicationContext().getBean(ActionLogService.class);
            List<Map<String, String>> oldParamList = actionLogService.selectBySql(selectSql);
//            actionLog.setOriginalValue(JSONArray.toJSONString(oldParamList));
            originalValue = JSONArray.toJSONString(oldParamList);
            result = targetRun(pjp, needAesHandle, sql, statement);
            if (!(boolean) map.get("complex")) {
                // 非复杂SQL
//                actionLog.setNewValue(JSONObject.toJSONString(map.get("items")));
                newValue = JSONObject.toJSONString(map.get("items"));
            } else {
                List<Map<String, String>> newParamList = actionLogService.selectBySql(selectSql);
//                actionLog.setNewValue(JSONArray.toJSONString(newParamList));
                newValue = JSONArray.toJSONString(newParamList);
            }
        }
        ActionLogServiceAsync actionLogServiceAsync = SpringContext.getApplicationContext().getBean
                (ActionLogServiceAsync.class);
        if (!closeAnnotationServiceLog) {
            actionLog.setNewValue(newValue);
            actionLog.setOriginalValue(originalValue);
            actionLogServiceAsync.insertServiceLogAsync(actionLog);
        }
        if ("true".equalsIgnoreCase(ApolloBaseConfig.getServiceCommonLogLevel())) {
            if (isBusiness(tableList)) {
                //记录business_log_value
                BusinessLogValue
                        businessLogValue = new BusinessLogValue(requestTraceId(), tables, originalValue,
                        newValue, actionLog.getActionDatetime(), actionLog.getOprNo());
                businessService.insertBusinessLogValue(businessLogValue);
            } else {
                actionLog.setNewValue(newValue);
                actionLog.setOriginalValue(originalValue);
                actionLogServiceAsync.insertServiceLogCommonAsync(actionLog);
            }
        }
        if ("business".equalsIgnoreCase(ApolloBaseConfig.getServiceCommonLogLevel())) {
            //记录business_log_value
            BusinessLogValue businessLogValue = new BusinessLogValue(requestTraceId(), tables, originalValue,
                    newValue, actionLog.getActionDatetime(), actionLog.getOprNo());
            businessService.insertBusinessLogValue(businessLogValue);
        }
//        if (!closeCommonServiceLog) {
//            actionLogServiceAsync.insertServiceLogCommonAsync(actionLog);
//        }
        actionLogMap.remove(key);
        return result;
    }


    /**
     * statement处理
     *
     * @param statement
     * @param sql
     * @return com.alibaba.druid.proxy.jdbc.PreparedStatementProxyImpl
     * @author 方典典
     * @time 2019/1/30 18:15
     */
    private PreparedStatementProxy statementHandle(PreparedStatementProxy statement, String sql) throws
            Exception {
        // 获取JDBC4PreparedStatement
        Field statementField = statement.getClass().getDeclaredField("statement");
        statementField.setAccessible(true);
        JDBC4PreparedStatement jdbc4PreparedStatement = (JDBC4PreparedStatement) statementField.get(statement);
        jdbc4PreparedStatement.clearParameters();
        // 获取PreparedStatement
        Class<PreparedStatement> preparedStatementClass = (Class<PreparedStatement>) jdbc4PreparedStatement.getClass
                ().getSuperclass().getSuperclass();
        Field parameterValuesField = preparedStatementClass.getDeclaredField("parameterValues");
        parameterValuesField.setAccessible(true);
        parameterValuesField.set(jdbc4PreparedStatement, new byte[][]{});
        Field staticSqlStringsField = preparedStatementClass.getDeclaredField("staticSqlStrings");
        staticSqlStringsField.setAccessible(true);
        staticSqlStringsField.set(jdbc4PreparedStatement, new byte[][]{sql.getBytes(), new byte[]{}});
        return statement;
    }

    /**
     * 获取这次请求的traceid
     *
     * @param
     * @return java.lang.String
     * @author lixiao
     * @date 2019-02-15 16:12
     */
    private String requestTraceId() {
        String traceId = "";
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
     * 执行目标方法
     *
     * @param pjp
     * @param needAesHandle
     * @param sql
     * @param statement
     * @return java.lang.Object
     * @author 方典典
     * @time 2019/1/30 18:22
     */
    private Object targetRun(ProceedingJoinPoint pjp, boolean needAesHandle, String sql, PreparedStatementProxy
            statement) throws
            Throwable {
        if (!needAesHandle) {
            return pjp.proceed();
        } else {
            statement = statementHandle(statement, sql);
            return pjp.proceed(new Object[]{pjp.getArgs()[0], statement});
        }
    }

    /**
     * 是否插入Business_log_value表
     *
     * @param tableList
     * @return boolean
     * @author 方典典
     * @time 2019/3/14 16:07
     */
    private boolean isBusiness(List<String> tableList) {
        for (String table : tableList) {
            if (BusinessTableUtils.getBusiTables().contains(table)) {
                return true;
            }
        }
        return false;
    }

}