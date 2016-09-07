package cn.newphy.data.mybatis.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class CamelCaseUtils {

	private static final char SEPARATOR = '_';

	/**
	 * 驼峰转下划线
	 * 
	 * @param str
	 * @return
	 */
	public static String camelCase2Underline(String str) {
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
	public static String underline2CamelCase(String str) {
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

	public static void main(String[] args) {
		try {
			String file = "D:/1.txt";
			List<String> lines = FileUtils.readLines(new File(file));
			for (String line : lines) {
				System.out.println(CamelCaseUtils.underline2CamelCase(line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		System.out.println(CamelCaseUtils.camelCase2Underline("ISOCertifiedStaff"));
//		System.out.println(CamelCaseUtils.camelCase2Underline("CertifiedStaff"));
//		System.out.println(CamelCaseUtils.camelCase2Underline("UserID"));
//		System.out.println(CamelCaseUtils.underline2CamelCase("iso_certified_staff"));
//		System.out.println(CamelCaseUtils.underline2CamelCase("certified_staff"));
//		System.out.println(CamelCaseUtils.underline2CamelCase("user_id"));
	}
}
