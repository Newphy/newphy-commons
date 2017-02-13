package cn.newphy.commons.data.generator.utils;

public class CamelCaseUtils {

	private static final char SEPARATOR = '_';

	/**
	 * 驼峰转下划线
	 * 
	 * @param str
	 * @return
	 */
	public static String camelToUnderline(String str) {
		if (str == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean upperCase = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			boolean nextUpperCase = true;
			if (i < (str.length() - 1)) {
				nextUpperCase = Character.isUpperCase(str.charAt(i + 1));
			}
			if ((i >= 0) && Character.isUpperCase(c)) {
				if (!upperCase || !nextUpperCase) {
					if (i > 0)
						sb.append(SEPARATOR);
				}
				upperCase = true;
			} else {
				upperCase = false;
			}
			sb.append(Character.toLowerCase(c));
		}
		return sb.toString();
	}

	/**
	 * 下划线转驼峰
	 * @param str
	 * @return
	 */
	public static String underlineToCamel(String str) {
		if (str == null) {
			return null;
		}
		str = str.toLowerCase();
		StringBuilder sb = new StringBuilder(str.length());
		boolean upperCase = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == SEPARATOR) {
				upperCase = true;
			} else if (upperCase) {
				sb.append(Character.toUpperCase(c));
				upperCase = false;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 下划线转驼峰
	 * @param str
	 * @return
	 */
	public static String underlineToCamelUpper(String str) {
		String camel = underlineToCamel(str);
		if(camel != null && camel.length() > 0) {
			return Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
		}
		return camel;
	}
}
