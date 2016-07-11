package cn.newphy.data.hibernate.impl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 对HQL做一些缓存措施,避免频繁构建hql
 * 
 * @author Administrator
 * 
 */
public class HibernateHQL {

	private static ConcurrentHashMap<CharSequence, String> hqlCache = new ConcurrentHashMap<CharSequence, String>();

	/**
	 * 获得查询hql
	 * 
	 * @param entityClass
	 * @param propNames
	 * @param orders
	 * @return
	 */
	static String getQueryHQL(Class<?> entityClass, String[] propNames,
			String[] orders) {
		return HqlUtils.createQueryHql(entityClass, propNames, orders).toString();
	}
	
	
	/**
	 * 获得删除hql
	 * 
	 * @param entityClass
	 * @param propNames
	 * @return
	 */
	static String getDeleteHQL(Class<?> entityClass, String[] propNames) {
		return HqlUtils.createDeleteHql(entityClass, propNames);
	}
	
	
	/**
	 * 获得count hql 
	 * @param entityClass
	 * @param propNames
	 * @return
	 */
	static String getCountHQL(Class<?> entityClass, String[] propNames) {
		return HqlUtils.createCountHql(entityClass, propNames);
	}
	

	/**
	 * 获得NamedParamHQL
	 * 
	 * @param hql
	 * @return
	 */
	static String getNamedParamHQL(CharSequence hql) {
		if (!hqlCache.contains(hql.toString())) {
			String namedParamHql = HqlUtils.replaceBindParamWithNamedParam(hql);
			hqlCache.putIfAbsent(hql, namedParamHql);
		}
		return hqlCache.get(hql);
	}
}
