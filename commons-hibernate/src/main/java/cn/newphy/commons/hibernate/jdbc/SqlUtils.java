package cn.newphy.commons.hibernate.jdbc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtils {

	private static final Pattern P_ORDER_BY = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
	@SuppressWarnings("unused")
	private static final Pattern P_BIND_SYMBOL = Pattern.compile("\\?");

	/**
	 * 删除select部分
	 * 
	 * @param hql
	 * @return
	 */
	public static String removeSelect(CharSequence sql) {
		String temp = sql.toString();
		int beginPos = temp.toLowerCase().indexOf("from");
		return temp.substring(beginPos);
	}

	/**
	 * 删除order部分
	 * 
	 * @param hql
	 * @return
	 */
	public static String removeOrders(CharSequence sql) {
		Matcher m = P_ORDER_BY.matcher(sql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

}
