package cn.ehai.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import cn.ehai.javautils.core.DBInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlHelperUtils {
	private static final Logger log = LoggerFactory.getLogger(SqlHelperUtils.class);

	/**
	 * Sqlserver 连接测试类
	 * 
	 * @param dbInfo
	 *            信息对象
	 * @return 是否正常连接
	 */
	public static boolean connectTest(DBInfo dbInfo) {
		if (dbInfo == null) {
			log.debug("DBInfo =null");
			return false;
		}
		// 加载JDBC驱动
		String driverName = "com.mysql.jdbc.Driver";
		// 连接服务器和数据库sample
		String dbURL = dbInfo.getDbConfigUrl();// 连接服务器和数据库
		String userName = dbInfo.getDbConfigName(); // 默认用户名
		String userPwd = dbInfo.getDbConfigKey();// 密码
		Connection dbConn = null;
		try {
			Class.forName(driverName);
			dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
			log.error("mysql - ConnectionSuccess");
			return true;
		} catch (Exception e) {
			log.error("mysql - Connectionfail", e);
		} finally {
			if (dbConn != null) {
				try {
					dbConn.close();
				} catch (SQLException e) {
					log.debug(e.getMessage());
				}
			}
		}
		return false;
	}
}
