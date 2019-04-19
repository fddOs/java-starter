package cn.seed.email.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Description:邮件参数类
 * @author:方典典
 * @time:2017年12月25日 上午10:38:34
 */
public class EmailKeyValue {
	@JsonProperty("Key")
	private String key;
	@JsonProperty("Value")
	private String value;

	public EmailKeyValue(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public EmailKeyValue() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
