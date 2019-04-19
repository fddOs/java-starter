package cn.seed.log.log;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.springframework.util.StringUtils;

import java.util.*;

import static cn.seed.common.utils.AESUtils.ENCRYPT_FLAG;
import static cn.seed.log.log.SqlAesUtils.aesEncryptString;
import static cn.seed.log.log.SqlAesUtils.isProperty;

/**
 * EHISqlASTVisitor
 * 加密字段作为参数时暂认定为其不会被函数处理
 *
 * @author 方典典
 * @time 2019/1/21 17:23
 */
public class EHISqlASTVisitor extends MySqlASTVisitorAdapter {
    /**
     * update语句的set项
     */
    private Map<String, String> items = new HashMap<>();
    /**
     * 表名
     */
    private List<String> tables = new ArrayList<>();
    /**
     * where语句
     */
    private String where = "";
    /**
     * 是否复杂语句
     */
    private boolean complex;
    /**
     * 是否为replace语句
     */
    private boolean replace;
    /**
     * sql
     */
    private String sql = "";

    private boolean hasVisitWhere;

    private boolean needAesHandle;

    @Override
    public boolean visit(SQLBetweenExpr x) {
        SQLExpr testExpr = x.getTestExpr();
        if (!isProperty(testExpr)) {
            return super.visit(x);
        }
        String key = SQLUtils.toMySqlString(testExpr);
        if (!key.endsWith(ENCRYPT_FLAG)) {
            return super.visit(x);
        }
        x.setBeginExpr(aesEncryptString(x.getBeginExpr()));
        x.setEndExpr(aesEncryptString(x.getEndExpr()));
        needAesHandle = true;
//        if(testExpr instanceof SQLMethodInvokeExpr){
//            List<SQLExpr> sqlExprList = ((SQLMethodInvokeExpr) testExpr).getParameters();
//            List<SQLExpr> identifierList = sqlExprList.stream().filter(sqlExpr -> (sqlExpr instanceof
// SQLIdentifierExpr&& ((SQLIdentifierExpr) sqlExpr).getName().endsWith(ENCRYPT_FLAG))).collect(Collectors.toList());
//            identifierList.forEach(identifier->{
//                SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr)identifier;
//                identifierExpr.setName();
//            });
//        }
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        SQLObject sqlObject = x.getParent();
        boolean noParam = isProperty(x.getLeft()) && isProperty(x.getRight());
        boolean notProperty = !isProperty(x.getLeft()) && !isProperty(x.getRight());
        if (x.getOperator().getPriority() != 110 || notProperty || noParam) {
            return super.visit(x);
        }
        String value = SQLUtils.toMySqlString(x.getLeft());
        if (!StringUtils.isEmpty(value) && value.endsWith(ENCRYPT_FLAG)) {
            x.setRight(aesEncryptString(x.getRight()));
            needAesHandle = true;
            while (sqlObject.getParent() != null) {
                sqlObject = sqlObject.getParent();
            }
            if (sqlObject instanceof MySqlUpdateStatement) {
                hasVisitWhere = true;
                this.visit((MySqlUpdateStatement) sqlObject);
            }
            sql = SQLUtils.toMySqlString(sqlObject);
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        String alia = x.getAlias();
        String table = x.getName().getSimpleName();
        if (!StringUtils.isEmpty(alia)) {
            table = table + " " + alia;
        }
        tables.add(table);
        return true;
    }

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        List<SQLSelectItem> selectList = x.getSelectList();
        EHISqlPropertyASTVisitor ehiSqlPropertyASTVisitor = new EHISqlPropertyASTVisitor();
        selectList.forEach(sqlSelectItem -> {
            sqlSelectItem.accept(ehiSqlPropertyASTVisitor);
            needAesHandle = ehiSqlPropertyASTVisitor.isNeedAesHandle();
        });
        sql = SQLUtils.toMySqlString(x);
        return super.visit(x);
    }

    @Override
    public boolean visit(MySqlUpdateStatement x) {
        where = SQLUtils.toMySqlString(x.getWhere());
        if (hasVisitWhere) {
            return true;
        }
        List<SQLUpdateSetItem> itemList = x.getItems();
        itemList.forEach(item -> {
            String key = SQLUtils.toMySqlString(item.getColumn());
            if (key.endsWith(ENCRYPT_FLAG)) {
                item.setValue(aesEncryptString((item.getValue())));
                needAesHandle = true;
            }
            String value;
            try {
                value = SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, SQLUtils.toMySqlString(item.getValue()))
                        .toString();
            } catch (Exception e) {
                //解析失败
                complex = true;
                value = SQLUtils.toMySqlString(item.getValue());
            }
            items.put(key, value);
        });
        sql = SQLUtils.toMySqlString(x);
        return true;
    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        insertItemsHandle(x.getColumns(), x.getValuesList());
        sql = SQLUtils.toMySqlString(x);
        if (!x.getDuplicateKeyUpdate().isEmpty()) {
            replace = true;
        }
        return true;
    }

    @Override
    public boolean visit(SQLReplaceStatement x) {
        insertItemsHandle(x.getColumns(), x.getValuesList());
        sql = SQLUtils.toMySqlString(x);
        replace = true;
        return true;
    }

    /**
     * insertItemsHandle
     *
     * @param columns
     * @param valuesClauseList
     * @return void
     * @author 方典典
     * @time 2019/1/21 17:05
     */
    private void insertItemsHandle(List<SQLExpr> columns, List<SQLInsertStatement.ValuesClause> valuesClauseList) {
        for (int i = 0; i < columns.size(); i++) {
            String column = SQLUtils.toMySqlString(columns.get(i));
            if (column.endsWith(ENCRYPT_FLAG)) {
                for (SQLInsertStatement.ValuesClause valuesClause : valuesClauseList) {
                    valuesClause.getValues().set(i, aesEncryptString(valuesClause.getValues().get(i)));
                    needAesHandle = true;
                }
            }
        }
    }

    public static void main(String[] args) {
        String sql = "select t.name_encrypt,sex_encrypt,id from test t left join b on t.a=b.a and t.a_encrypt = '1'  " +
                "where id = '1' and SUBSTR" +
                "(time_encrypt,2) between '2' and '5'";
        String sql1 = "update test t set t.name_encrypt = 12,age_encrypt='23',sex='man' where t.id_encrypt = 1";
        String sql2 = "insert test(name_encrypt) values('fdd')";
        String sql3 = "select id,name from test where name = HEX(AES_ENCRYPT('a','fdd'));";
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
        map.put("sql", visitor.getSql());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.err.println(entry.getKey() + ":" + entry.getValue().toString());
        }
    }

    public List<String> getTables() {
        return tables;
    }

    public String getWhere() {
        return where;
    }

    public boolean isComplex() {
        return complex;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public boolean isReplace() {
        return replace;
    }

    public String getSql() {
        return sql;
    }

    public boolean isHasVisitWhere() {
        return hasVisitWhere;
    }

    public boolean isNeedAesHandle() {
        return needAesHandle;
    }
}