package cn.ehai.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * 签名工具 SHA256
 * 
 * @author lixiao
 *
 */
public class SignUtils {

	public static final String BODY_KEY = "payload";

	public static final String SECRET = "3c6fa384648ffd5cf229ddf5ac82c480";

	/**
	 * 请求签名
	 * 
	 * @param query
	 *            url中？后面的内容
	 * @param body
	 *            body信息
	 * @return 签名之后的内容
	 */
	public static String sign(String query, String body) {
		Map<String, String> signMap = new HashMap();
		if (!StringUtils.isEmpty(query)) {
			String[] params = query.split("&");
			for (String param : params) {
				String[] string = param.split("=");
				if (string.length == 2 && !StringUtils.isEmpty(string[1])) {
					signMap.put(string[0], string[1]);
				}
			}
		}
		return sign(signMap,body);
	}
	public static String sign(Map<String, String> signMap, String body) {
		if (!StringUtils.isEmpty(body)) {
			signMap.put(BODY_KEY, body);
		}
		String second = EncryptUtils.HMACSHA256(SECRET + getSignContent(signMap) + SECRET, SECRET);
		String result = Base64Utils.encryptBASE64(second);
		return result;
	}

	/**
	 * 返回值 body 加密  HMAC256
	 * @param body
	 * @return java.lang.String
	 * @author lixiao
	 * @date 2018/12/15 11:02
	 */
	public static String signResponse(String body) {
		String second = EncryptUtils.HMACSHA256(SECRET + body + SECRET, SECRET);
		String result = Base64Utils.encryptBASE64(second);
		return result;

	}

	/**
	 * obj to map
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> objectToMap(Object obj) throws Exception {
		if (obj == null) {
			return null;
		}
		// 获取关联的所有类，本类以及所有父类
		boolean ret = true;
		Class oo = obj.getClass();
		List<Class> clazzs = new ArrayList<Class>();
		while (ret) {
			clazzs.add(oo);
			oo = oo.getSuperclass();
			if (oo == null || oo == Object.class)
				break;
		}

		Map<String, Object> map = new HashMap<String, Object>();

		for (int i = 0; i < clazzs.size(); i++) {
			Field[] declaredFields = clazzs.get(i).getDeclaredFields();
			for (Field field : declaredFields) {
				int mod = field.getModifiers();
				// 过滤 static 和 final 类型
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
					continue;
				}
				field.setAccessible(true);
				map.put(field.getName(), field.get(obj));
			}
		}

		return map;
	}

	/**
	 * 获取待签名字符串
	 * 
	 * @param sortedParams
	 * @return
	 */
	public static String getSignContent(Map<String, String> sortedParams) {
		StringBuffer content = new StringBuffer();
		List<String> keys = new ArrayList<String>(sortedParams.keySet());
		Collections.sort(keys);
		int index = 0;
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = sortedParams.get(key);
			if (areNotEmpty(key, value)) {
				content.append((index == 0 ? "" : "&") + key + "=" + value);
				index++;
			}
		}
		return content.toString();
	}

	/**
	 * 检查指定的字符串列表是否不为空。
	 */
	public static boolean areNotEmpty(String... values) {
		boolean result = true;
		if (values == null || values.length == 0) {
			result = false;
		} else {
			for (String value : values) {
				result &= !StringUtils.isEmpty(value);
			}
		}
		return result;
	}
}
