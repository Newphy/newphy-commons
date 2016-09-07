package cn.newphy.commons.consistency.util;

public class UUID {

	/**
	 * 生成UUID
	 * 
	 * @return
	 */
	public static String generate() {
		String uuid = java.util.UUID.randomUUID().toString();
		return uuid.replace("-", "");
	}
}
