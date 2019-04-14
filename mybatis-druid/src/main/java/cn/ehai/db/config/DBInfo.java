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
	private String url;
	private String userName;
	private String password;

	public DBInfo() {
		super();
	}

	public DBInfo(String dbConfigUrl, String dbConfigName, String password) {
		super();
		this.url = dbConfigUrl;
		this.userName = dbConfigName;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
