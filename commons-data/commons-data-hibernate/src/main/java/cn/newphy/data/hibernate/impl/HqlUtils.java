package cn.newphy.data.hibernate.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

public class HqlUtils {

	private static final Pattern P_ORDER_BY = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
	private static final Pattern P_BIND_SYMBOL = Pattern.compile("\\?");

	/**
	 * 删除select部分
	 * 
	 * @param hql
	 * @return
	 */
	public static String removeSelect(CharSequence hql) {
		String temp = hql.toString();
		int beginPos = temp.toLowerCase().indexOf("from");
		return temp.substring(beginPos);
	}

	/**
	 * 删除order部分
	 * 
	 * @param hql
	 * @return
	 */
	public static String removeOrders(CharSequence hql) {
		Matcher m = P_ORDER_BY.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 创建查询hql
	 * 
	 * @param clazz
	 * @param propNames
	 * @param orders
	 * @return
	 */
	public static String createQueryHql(Class<?> clazz, String[] propNames, String... orders) {
		StringBuffer hql = new StringBuffer("FROM ").append(clazz.getCanonicalName()).append(" t ");
		if (!ArrayUtils.isEmpty(propNames)) {
			for (int i = 0; i < propNames.length; i++) {
				String propName = propNames[i];
				if (i == 0) {
					hql.append("WHERE t.").append(propName).append(" = ? ");
				} else {
					hql.append("AND t.").append(propName).append(" = ? ");
				}
			}
		}
		if (!ArrayUtils.isEmpty(orders)) {
			hql.append("ORDER BY ");
			for (String order : orders) {
				hql.append(order).append(" ");
			}
		}
		return hql.toString();
	}

	/**
	 * 创建count hql
	 * @param clazz
	 * @param propNames
	 * @return
	 */
	public static String createCountHql(Class<?> clazz, String[] propNames) {
		StringBuffer hql = new StringBuffer("SELECT count(1) FROM ").append(clazz.getCanonicalName()).append(" t ");
		if (!ArrayUtils.isEmpty(propNames)) {
			for (int i = 0; i < propNames.length; i++) {
				String propName = propNames[i];
				if (i == 0) {
					hql.append("WHERE t.").append(propName).append(" = ? ");
				} else {
					hql.append("AND t.").append(propName).append(" = ? ");
				}
			}
		}
		return hql.toString();
	}

	/**
	 * 创建查询hql
	 * 
	 * @param clazz
	 * @param orders
	 * @return
	 */
	public static String createDeleteHql(Class<?> clazz, String[] propNames) {
		StringBuffer hql = new StringBuffer("DELETE ").append(clazz.getCanonicalName()).append(" t ");
		if (!ArrayUtils.isEmpty(propNames)) {
			for (int i = 0; i < propNames.length; i++) {
				String propName = propNames[i];
				if (i == 0) {
					hql.append("WHERE t.").append(propName).append(" = ? ");
				} else {
					hql.append("AND t.").append(propName).append(" = ? ");
				}
			}
		}
		return hql.toString();
	}

	/**
	 * 将绑定变量换为名字变量
	 * 
	 * @param hql
	 *            带?的绑定变量hql
	 * @return 带:param{i}的名字变量hql
	 */
	public static String replaceBindParamWithNamedParam(CharSequence hql) {
		StringBuffer newHql = new StringBuffer();
		Matcher matcher = P_BIND_SYMBOL.matcher(hql);
		int i = 1;
		while (matcher.find()) {
			matcher.appendReplacement(newHql, ":param" + (i++));
		}
		matcher.appendTail(newHql);
		return newHql.toString();
	}

	/**
	 * 根据值数组获得名字参数数组
	 * 
	 * @param values
	 * @return
	 */
	public static String[] getNamedParamArray(Object[] values) {
		if (values == null) {
			return new String[0];
		}
		String[] namedParams = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			namedParams[i] = "param" + (i + 1);
		}
		return namedParams;
	}
}
