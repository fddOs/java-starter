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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    private static final String HEADER_JWT_USER_ID = "jwt-user-id";
    private static final String NEED_AES_HANDLE = "needAesHandle";
    @Autowired
    private Tracer tracer;
    @Autowired
    private BusinessService businessService;

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
        if ("false".equalsIgnoreCase(ApolloBaseConfig.getServiceCommonLogLevel())) {
            return targetRun(pjp, needAesHandle, sql, statement);
        }
        ActionLog actionLog = getCurrentRequestActionLog();
        // 表名
        List<String> tableList = (List<String>) map.get("tables");
        String tables = StringUtils.arrayToDelimitedString(tableList.toArray(), ",");
        String originalValue;
        String newValue;
        Object result;
        actionLog.setOprTableName(tables);
        if (isReplace) {
            originalValue = "{\"type\": \"replace\"}";
            newValue = "{\"type\": \"" + SQLUtils.formatMySql(sql, new SQLUtils.FormatOption(true, false))
                    + "\"}";
            result = targetRun(pjp, needAesHandle, sql, statement);
        } else {
            // 获取查询老值的sql
            String selectSql = getSelectSQL(map);
            ActionLogService actionLogService = SpringContext.getApplicationContext().getBean(ActionLogService.class);
            List<Map<String, String>> oldParamList = actionLogService.selectBySql(selectSql);
            originalValue = JSONArray.toJSONString(oldParamList);
            result = targetRun(pjp, needAesHandle, sql, statement);
            if (!(boolean) map.get("complex")) {
                // 非复杂SQL
                newValue = JSONObject.toJSONString(map.get("items"));
            } else {
                List<Map<String, String>> newParamList = actionLogService.selectBySql(selectSql);
                newValue = JSONArray.toJSONString(newParamList);
            }
        }
        actionLog.setNewValue(newValue);
        actionLog.setOriginalValue(originalValue);
        insertActionLog(tableList, actionLog);
        return result;
    }

    /**
     * 记录操作
     *
     * @param tableList
     * @param actionLog
     * @return void
     * @author 方典典
     * @time 2019/5/13 10:41
     */
    private void insertActionLog(List<String> tableList, ActionLog actionLog) {
        ActionLogServiceAsync actionLogServiceAsync = SpringContext.getApplicationContext().getBean
                (ActionLogServiceAsync.class);
        if ("true".equalsIgnoreCase(ApolloBaseConfig.getServiceCommonLogLevel())) {
            if (isBusiness(tableList)) {
                //记录business_log_value
                BusinessLogValue
                        businessLogValue = new BusinessLogValue(requestTraceId(), actionLog.getOprTableName(),
                        actionLog.getOriginalValue(), actionLog.getNewValue(), actionLog.getOprNo());
                businessService.insertBusinessLogValue(businessLogValue);
            } else {
                actionLogServiceAsync.insertServiceLogCommonAsync(actionLog);
            }
        }
        if ("business".equalsIgnoreCase(ApolloBaseConfig.getServiceCommonLogLevel())) {
            //记录business_log_value
            BusinessLogValue businessLogValue = new BusinessLogValue(requestTraceId(), actionLog.getOprTableName(),
                    actionLog.getOriginalValue(), actionLog.getNewValue(), actionLog.getOprNo());
            businessService.insertBusinessLogValue(businessLogValue);
        }
    }

    /**
     * 获取当前请求的actionLog
     *
     * @param
     * @return cn.seed.log.entity.ActionLog
     * @author 方典典
     * @time 2019/5/13 10:36
     */
    private ActionLog getCurrentRequestActionLog() {
        // 获取Request
        HttpServletRequest request = null;
        String oprNo = "";
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            oprNo = request.getParameter("oprNo");
            if (StringUtils.isEmpty(oprNo)) {
                oprNo = request.getHeader(HEADER_JWT_USER_ID);
            }
        } catch (Exception e) {
            //IGNORE
        }
        oprNo = OprNoUtils.handlerOprNo(oprNo);
        ActionLog actionLog = new ActionLog();
        actionLog.setTraceId(requestTraceId());
        actionLog.setOprNo(oprNo);
        actionLog.setActionDatetime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return actionLog;
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