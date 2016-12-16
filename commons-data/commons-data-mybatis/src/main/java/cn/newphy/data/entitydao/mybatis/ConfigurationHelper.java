package cn.newphy.data.entitydao.mybatis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

public class ConfigurationHelper {

	// 表名前缀
	private String tableNamePrefix = "";

	private static ListValuedMap<Class<?>, ResultMap> resultMaps = new ArrayListValuedHashMap<Class<?>, ResultMap>();

	private static void init(Configuration configuration) {
		if (resultMaps.isEmpty()) {
			synchronized (ConfigurationHelper.class) {
				if (resultMaps.isEmpty()) {
					Collection<ResultMap> rms = configuration.getResultMaps();
					for (ResultMap resultMap : rms) {
						Class<?> type = resultMap.getType();
						resultMaps.put(type, resultMap);
					}
				}
			}
		}

	}

	/**
	 * 根据类型获取ResultMap列表
	 * 
	 * @param type
	 * @return
	 */
	public static List<ResultMap> getResultMapsByType(Configuration configuration, Class<?> type) {
		init(configuration);
		List<ResultMap> rms = resultMaps.get(type);
		if (rms == null) {
			rms = new ArrayList<ResultMap>();
		}
		return rms;
	}

	/**
	 * 根据类型获取缺省ResultMap
	 * 
	 * @param type
	 * @return
	 */
	public static ResultMap getDefaultResultMapsByType(Configuration configuration, Class<?> type) {
		List<ResultMap> rms = getResultMapsByType(configuration, type);
		String suffixName = type.getSimpleName() + "ResultMap";
		String checkName = "." + suffixName;
		Set<ResultMap> result = new HashSet<ResultMap>();
		for (ResultMap resultMap : rms) {
			if (resultMap.getId().endsWith(checkName)) {
				result.add(resultMap);
			}
		}
		if (result.size() == 0) {
			return null;
		}
		if (result.size() > 1) {
			String msg = "类型[" + type.getName() + "]存在多个以" + suffixName + "结尾的ResultMap, 它们为[";
			for (ResultMap resultMap : result) {
				msg += resultMap.getId() + ", ";
			}
			msg += "]";
			throw new IllegalStateException(msg);
		}
		return result.iterator().next();
	}

	/**
	 * 根据ResultMap短名获得ResultMap
	 * 
	 * @param configuration
	 * @param type
	 * @param sid
	 * @return
	 */
	public static ResultMap getResultMapBySimpleId(Configuration configuration, Class<?> type, String sid) {
		List<ResultMap> rms = getResultMapsByType(configuration, type);
		String suffixName = "." + sid;
		for (ResultMap resultMap : rms) {
			if (resultMap.getId().endsWith(suffixName)) {
				return resultMap;
			}
		}
		return null;
	}

	/**
	 * 根据类型获取任意一个ResultMap
	 * 
	 * @param type
	 * @return
	 */
	public static ResultMap getAnyResultMapByType(Configuration configuration, Class<?> type) {
		List<ResultMap> list = getResultMapsByType(configuration, type);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * 注册sql
	 * 
	 * @param configuration
	 * @param sql
	 * @param statementId
	 * @param resultMap
	 */
	public static String registerDynamicSelectSql(String namespace, final Configuration configuration, String sql,
			final ResultMap resultMap) {
		String statementId = getSqlId(namespace, SqlCommandType.SELECT, sql);
		if (!configuration.hasStatement(statementId)) {
			synchronized (ConfigurationHelper.class) {
				if (!configuration.hasStatement(statementId)) {
					LanguageDriver languageDriver = configuration.getDefaultScriptingLanuageInstance();
					SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
					@SuppressWarnings("serial")
					MappedStatement ms = new MappedStatement.Builder(configuration, statementId, sqlSource,
							SqlCommandType.SELECT).resultMaps(new ArrayList<ResultMap>() {
								{
									add(resultMap);
								}
							}).build();
					// 缓存
					configuration.addMappedStatement(ms);
				}
			}
		}
		return statementId;
	}

	/**
	 * 注册sql
	 * 
	 * @param configuration
	 * @param sql
	 * @param statementId
	 * @param resultMap
	 */
	public static String registerDynamicUpdateSql(String namespace, final Configuration configuration, String sql) {
		String statementId = getSqlId(namespace, SqlCommandType.UPDATE, sql);
		if (!configuration.hasStatement(statementId)) {
			synchronized (ConfigurationHelper.class) {
				if (!configuration.hasStatement(statementId)) {
					LanguageDriver languageDriver = configuration.getDefaultScriptingLanuageInstance();
					SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
					@SuppressWarnings("serial")
					MappedStatement ms = new MappedStatement.Builder(configuration, statementId, sqlSource,
							SqlCommandType.UPDATE).resultMaps(new ArrayList<ResultMap>() {
								{
									add(new ResultMap.Builder(configuration, "defaultResultMap", int.class,
											new ArrayList<ResultMapping>(0)).build());
								}
							}).build();
					// 缓存
					configuration.addMappedStatement(ms);
				}
			}
		}
		return statementId;
	}

	public static MappedStatement.Builder createMappedStatementBuilder(MappedStatement origin, String id, SqlSource sqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(origin.getConfiguration(), id,
				sqlSource, origin.getSqlCommandType());
		builder.resource(origin.getResource());
		builder.fetchSize(origin.getFetchSize());
		builder.statementType(origin.getStatementType());
		builder.keyGenerator(origin.getKeyGenerator());
		if (origin.getKeyProperties() != null && origin.getKeyProperties().length != 0) {
			StringBuffer keyProperties = new StringBuffer();
			for (String keyProperty : origin.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}

		// setStatementTimeout()
		builder.timeout(origin.getTimeout());

		// setStatementResultMap()
		builder.parameterMap(origin.getParameterMap());

		// setStatementResultMap()
		builder.resultMaps(origin.getResultMaps());
		builder.resultSetType(origin.getResultSetType());

		// setStatementCache()
		builder.cache(origin.getCache());
		builder.flushCacheRequired(origin.isFlushCacheRequired());
		builder.useCache(origin.isUseCache());
		return builder;
	}
	
	
	public static ParameterMapping copyOf(Configuration configuration, ParameterMapping pm, String newProperty) {
		ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, newProperty, pm.getJavaType());
		builder.jdbcType(pm.getJdbcType());
		builder.expression(pm.getExpression());
		builder.jdbcTypeName(pm.getJdbcTypeName());
		builder.mode(pm.getMode());
		builder.numericScale(pm.getNumericScale());
		builder.resultMapId(pm.getResultMapId());
		builder.typeHandler(pm.getTypeHandler());
		return builder.build();
	}

	public static String getSqlId(String namespace, SqlCommandType sqlType, String sql) {
		String statementId = namespace + "[" + sqlType + "." + sql.length() + "." + hashCode(sql) + "]";
		return statementId;
	}

	private static long hashCode(String sql) {
		long h = 0L;
		if (sql == null) {
			return h;
		}
		char[] val = sql.toCharArray();
		if (h == 0 && val.length > 0) {
			h = 17L;
			for (int i = 0; i < val.length; i++) {
				h = 31L * h + (long) val[i];
			}
		}
		return Math.abs(h);
	}

	/**
	 * @return the tableNamePrefix
	 */
	public String getTableNamePrefix() {
		return tableNamePrefix;
	}

	/**
	 * @param tableNamePrefix
	 *            the tableNamePrefix to set
	 */
	public void setTableNamePrefix(String tableNamePrefix) {
		this.tableNamePrefix = tableNamePrefix;
	}

}
