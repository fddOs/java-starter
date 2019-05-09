/**
 * 
 */
package cn.seed.common.utils;

/**
 * @Description:Redis连接信息
 * @author:方典典
 * @time:2018年5月29日 下午4:47:17
 */
public class RedisInfo {
	private Integer maxTotal;
	private Integer maxIdle;
	private Long maxWaitMillis;
	private String redisConfigUrl;

	public RedisInfo() {
		super();
	}

	public RedisInfo(Integer maxTotal, Integer maxIdle, Long maxWaitMillis, String redisConfigUrl,
			Integer redisConfigPort) {
		super();
		this.maxTotal = maxTotal;
		this.maxIdle = maxIdle;
		this.maxWaitMillis = maxWaitMillis;
		this.redisConfigUrl = redisConfigUrl;
		this.redisConfigPort = redisConfigPort;
	}

	public Integer getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(Integer maxTotal) {
		this.maxTotal = maxTotal;
	}

	public Integer getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}

	public Long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(Long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public String getRedisConfigUrl() {
		return redisConfigUrl;
	}

	public void setRedisConfigUrl(String redisConfigUrl) {
		this.redisConfigUrl = redisConfigUrl;
	}

	public Integer getRedisConfigPort() {
		return redisConfigPort;
	}

	public void setRedisConfigPort(Integer redisConfigPort) {
		this.redisConfigPort = redisConfigPort;
	}

	private Integer redisConfigPort;

}
