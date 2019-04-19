package cn.seed.log.log;

import cn.seed.common.utils.AESUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;

/**
 * @Description:SqlAesUtils
 * @author:方典典
 * @time:2019/1/22 15:02
 */
public class SqlAesUtils {
    /**
     * SQL AES加密
     *
     * @param sqlExpr
     * @return com.alibaba.druid.sql.ast.SQLExpr
     * @author 方典典
     * @time 2019/1/22 10:54
     */
    public static SQLExpr aesEncryptString(SQLExpr sqlExpr) {
        SQLMethodInvokeExpr hexMethodInvokeExpr = new SQLMethodInvokeExpr("HEX");
        SQLMethodInvokeExpr aesEncryptMethodInvokeExpr = new SQLMethodInvokeExpr("AES_ENCRYPT");
        aesEncryptMethodInvokeExpr.addParameter(sqlExpr);
        aesEncryptMethodInvokeExpr.addParameter(new SQLCharExpr(AESUtils.KEY));
        hexMethodInvokeExpr.addParameter(aesEncryptMethodInvokeExpr);
        return hexMethodInvokeExpr;
    }

    /**
     * SQL AES解密
     *
     * @param sqlExpr
     * @return com.alibaba.druid.sql.ast.SQLExpr
     * @author 方典典
     * @time 2019/1/22 10:56
     */
    public static SQLExpr aesDecryptString(SQLExpr sqlExpr) {
        SQLMethodInvokeExpr hexMethodInvokeExpr = new SQLMethodInvokeExpr("UNHEX");
        SQLMethodInvokeExpr aesDecryptMethodInvokeExpr = new SQLMethodInvokeExpr("AES_DECRYPT");
        hexMethodInvokeExpr.addParameter(sqlExpr);
        aesDecryptMethodInvokeExpr.addParameter(hexMethodInvokeExpr);
        aesDecryptMethodInvokeExpr.addParameter(new SQLCharExpr(AESUtils.KEY));
        return aesDecryptMethodInvokeExpr;
    }

    /**
     * 判断sqlExpr是否为纯列
     *
     * @param sqlExpr
     * @return boolean
     * @author 方典典
     * @time 2019/1/22 11:14
     */
    public static boolean isProperty(SQLExpr sqlExpr) {
        if (sqlExpr instanceof SQLIdentifierExpr || sqlExpr instanceof SQLPropertyExpr) {
            return true;
        }
        return false;
    }

}
