package cn.ehai.log.log;

import cn.ehai.common.utils.AESUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import org.springframework.util.StringUtils;

/**
 * EHISqlPropertyASTVisitor
 *
 * @author 方典典
 * @time 2019/1/21 17:23
 */
public class EHISqlPropertyASTVisitor extends MySqlASTVisitorAdapter {
    private boolean needAesHandle;

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        String name = x.getName();
        if (!StringUtils.isEmpty(name) && name.endsWith(AESUtils.ENCRYPT_FLAG)) {
            x.setName(SQLUtils.toMySqlString(SqlAesUtils.aesDecryptString(x)));
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
            ((SQLSelectItem) parentSqlObject).setExpr(SqlAesUtils.aesDecryptString(x));
            needAesHandle = true;
        }
        return super.visit(x);
    }

    public boolean isNeedAesHandle() {
        return needAesHandle;
    }
}