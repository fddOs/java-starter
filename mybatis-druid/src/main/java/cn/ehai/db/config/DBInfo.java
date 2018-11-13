/**
 * 
 */
package cn.ehai.db.config;

/**
 * @Description:数据库连接信息
 * @author:方典典
 * @time:2018年5月29日 下午4:47:17
 */
public class DBInfo {
	private String dbConfigUrl;
	private String dbConfigName;
	private String dbConfigKey;

	public DBInfo() {
		super();
	}

	public DBInfo(String dbConfigUrl, String dbConfigName, String dbConfigKey) {
		super();
		this.dbConfigUrl = dbConfigUrl;
		this.dbConfigName = dbConfigName;
		this.dbConfigKey = dbConfigKey;
	}

	public String getDbConfigUrl() {
		return dbConfigUrl;
	}

	public void setDbConfigUrl(String dbConfigUrl) {
		this.dbConfigUrl = dbConfigUrl;
	}

	public String getDbConfigName() {
		return dbConfigName;
	}

	public void setDbConfigName(String dbConfigName) {
		this.dbConfigName = dbConfigName;
	}

	public String getDbConfigKey() {
		return dbConfigKey;
	}

	public void setDbConfigKey(String dbConfigKey) {
		this.dbConfigKey = dbConfigKey;
	}
}
