package cn.newphy.data.mybatis;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;

public class ConfigurationHelper {

	private Configuration configuration;
	
	// 表名前缀
	private String tableNamePrefix = "";
	
	private static ListValuedMap<Class<?>, ResultMap> resultMaps = new ArrayListValuedHashMap<Class<?>, ResultMap>();
	
	
	private void init() {
		Collection<ResultMap> rms = configuration.getResultMaps();
		for (ResultMap resultMap : rms) {
			Class<?> type = resultMap.getType();
			resultMaps.put(type, resultMap);
		}
	}
	
	/**
	 * 根据类型获取ResultMap列表
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<ResultMap> getResultMapsByType(Class<?> type) {
		List<ResultMap> rms = resultMaps.get(type);
		if(rms == null) {
			rms = Collections.EMPTY_LIST;
		}
		return rms;
	}
	
	/**
	 * 根据类型获取任意一个ResultMap
	 * @param type
	 * @return
	 */
	public static ResultMap getAnyResultMapByType(Class<?> type) {
		List<ResultMap> list = getResultMapsByType(type);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * @return the tableNamePrefix
	 */
	public String getTableNamePrefix() {
		return tableNamePrefix;
	}

	/**
	 * @param tableNamePrefix the tableNamePrefix to set
	 */
	public void setTableNamePrefix(String tableNamePrefix) {
		this.tableNamePrefix = tableNamePrefix;
	}
	
	
}
