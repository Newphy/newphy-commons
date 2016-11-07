package cn.newphy.commons.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.Transformer;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

	/**
	 * 根据正则表达式查找字符串
	 * 
	 * @param content
	 * @param patter
	 * @param group
	 * @return
	 */
	public static String findAnyByRegex(CharSequence content, Pattern pattern, int group) {
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(group);
		}
		return null;
	}

	/**
	 * 查找字符串
	 * 
	 * @param text
	 *            查询字符串
	 * @param pattern
	 *            正则Pattern
	 * @return
	 */
	public static List<String> findByRegex(CharSequence text, Pattern pattern, int group) {
		List<String> list = new ArrayList<String>();
		if (pattern == null) {
			return list;
		}
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			list.add(matcher.group(group));
		}
		return list;
	}

	/**
	 * 通过函数来计算替换文本
	 * 
	 * @param text
	 *            原文
	 * @param pattern
	 *            正则Pattern
	 * @param group
	 *            正则匹配group
	 * @param replaceFunc
	 *            替换函数
	 * @return
	 */
	public static String replaceByFunc(CharSequence text, Pattern pattern, int group, Transformer<String, String> replaceFunc) {
		if (text == null || pattern == null) {
			return text == null ? null : text.toString();
		}
		Matcher matcher = pattern.matcher(text);
		matcher.reset();
		boolean result = matcher.find();
		if (result) {
			StringBuffer sb = new StringBuffer();
			String src1, src2, replacement1, replacement2;
			do {
				src1 = matcher.group();
				src2 = matcher.group(group);
				replacement2 = replaceFunc.transform(src2);
				if (replacement2 != null) {
					replacement1 = (group == 0) ? replacement2 : src1.replace(src2, replacement2);
					matcher.appendReplacement(sb, replacement1);
				}
				result = matcher.find();
			} while (result);
			matcher.appendTail(sb);
			return sb.toString();
		}
		return text.toString();
	}
	

	/**
	 * 通过函数来计算替换文本
	 * 
	 * @param text
	 *            原文
	 * @param pattern
	 *            正则Pattern
	 * @param replaceFunc
	 *            替换函数
	 * @return
	 */
	public static String replaceByFunc(CharSequence text, Pattern pattern, Transformer<String, String> replaceFunc) {
		return replaceByFunc(text, pattern, 0, replaceFunc);
	}

}
