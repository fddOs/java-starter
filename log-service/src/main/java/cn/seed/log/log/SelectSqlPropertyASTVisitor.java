package cn.seed.log.log;

import cn.seed.common.utils.AESUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import org.springframework.util.StringUtils;

/**
 * SelectSqlPropertyASTVisitor
 *
 * @author 方典典
 * @time 2019/1/21 17:23
 */
public class SelectSqlPropertyASTVisitor extends MySqlASTVisitorAdapter {
    private boolean needAesHandle;

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        String name = x.getName();
        if (!StringUtils.isEmpty(name) && name.endsWith(AESUtils.ENCRYPT_FLAG)) {
            x.setName(SQLUtils.toMySqlString(SqlAesUtils.aesDecryptString(x)) + " AS " + name);
            needAesHandle = true;
        }
        return super.visit(x);
    }

    @Override
    public boolean visit(SQLPropertyExpr x) {
        SQLObject parentSqlObject = x.getParent();
        while (!(parentSqlObject instanceof SQLSelectItem)) {
            parentSqlObject = parentSqlObject.getParent();
        }
        String name = x.getName();
        if (!StringUtils.isEmpty(name) && name.endsWith(AESUtils.ENCRYPT_FLAG)) {
            String aesExprString = SQLUtils.toMySqlString(SqlAesUtils.aesDecryptString(x)) + " AS " + name;
            ((SQLSelectItem) parentSqlObject).setExpr(SQLUtils.toMySqlExpr(aesExprString));
            needAesHandle = true;
        }
        return super.visit(x);
    }

    public boolean isNeedAesHandle() {
        return needAesHandle;
    }
}