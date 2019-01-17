package cn.ehai.log.log;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLReplaceStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EHISqlASTVisitor extends MySqlASTVisitorAdapter {
    /**
    *update语句的set项
    */
    private Map<String, String> items = new HashMap<>();
    /**
     *表名
     */
    private List<String> tables = new ArrayList<>();
    /**
     *where语句
     */
    private String where = "";
    /**
     *是否复杂语句
     */
    private boolean complex;
    /**
     *是否为replace语句
     */
    private boolean replace;

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
    public boolean visit(MySqlUpdateStatement x) {
        where = SQLUtils.toMySqlString(x.getWhere());
        return true;
    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        if (!x.getDuplicateKeyUpdate().isEmpty()) {
            replace = true;
        }
        return true;
    }

    @Override
    public boolean visit(SQLReplaceStatement x) {
        replace = true;
        return true;
    }

    @Override
    public boolean visit(SQLUpdateSetItem x) {
        String key = SQLUtils.toMySqlString(x.getColumn());
        String value;
        try {
            value = SQLEvalVisitorUtils.evalExpr(JdbcConstants.MYSQL, SQLUtils.toMySqlString(x.getValue()))
                    .toString();
        } catch (Exception e) {
            //解析失败
            complex = true;
            value = SQLUtils.toMySqlString(x.getValue());
        }
        items.put(key, value);
        return true;
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
}