package cn.newphy.data.entitydao.mybatis.plugins.paginator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import cn.newphy.data.domain.Page;
import cn.newphy.data.domain.Pageable;
import cn.newphy.data.entitydao.mybatis.ConfigurationHelper;
import cn.newphy.data.entitydao.mybatis.EConfiguration;

public abstract class PageProcessor {

	protected static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<>(0);
	protected static Map<String, MappedStatement> stmtMap = new HashMap<>();

	protected final Invocation invocation;
	protected final Pageable pageable;
	protected final EConfiguration globalConfig;

	public PageProcessor(Invocation invocation, Pageable pageable, EConfiguration globalConfig) {
		this.invocation = invocation;
		this.pageable = pageable;
		this.globalConfig = globalConfig;
	}

	public abstract Page<?> process() throws InvocationTargetException, IllegalAccessException;

	protected MappedStatement getMappedStatement() {
		return (MappedStatement) invocation.getArgs()[PageConst.IDX_MAPPED_STATEMENT];
	}

	protected Object getParameterObject() {
		return invocation.getArgs()[PageConst.IDX_PARAMETER_OBJECT];
	}

	protected List<ResultMap> getCountResultMaps(MappedStatement stmt) {
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		ResultMap resultMap = new ResultMap.Builder(stmt.getConfiguration(), stmt.getId(), long.class,
				EMPTY_RESULTMAPPING).build();
		resultMaps.add(resultMap);
		return resultMaps;

	}

	/**
	 * 处理分页参数，将pageable对象放到map参数中
	 * 
	 * @param configuration
	 * @param boundSql
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Object getParameterObjectWithPage(Configuration configuration, Pageable pageable) {
		Object parameterObject = getParameterObject();
		Map<String, Object> paramMap = new LinkedHashMap<>();
		// 增加分页参数
		paramMap.putAll(pageable.getParamMap());
		// 增加自带参数
		if (parameterObject instanceof Map) {
			paramMap.putAll((Map<String, Object>) parameterObject);
		} else {
			MetaObject metaObject = configuration.newMetaObject(parameterObject);
			for (String name : metaObject.getGetterNames()) {
				paramMap.put(name, metaObject.getValue(name));
			}
		}
		// 增加分页对象
		paramMap.put(PageConst.PARAM_NAME_PAGE, pageable);
		return paramMap;
	}

	@SuppressWarnings("unchecked")
	protected long queryTotal(BoundSql boundSql, Object parameterObject, Pageable pageable)
			throws InvocationTargetException, IllegalAccessException {
		MappedStatement stmt = getMappedStatement();
		String sql = boundSql.getSql();
		PageSqlParser sqlParser = new PageSqlParser(sql, globalConfig.getDialectType());
		String countSql = sqlParser.getCountSql();
		StaticSqlSource countSqlSource = new StaticSqlSource(stmt.getConfiguration(), countSql,
				boundSql.getParameterMappings());
		MappedStatement.Builder countStmtBuilder = ConfigurationHelper.createMappedStatementBuilder(stmt,
				stmt.getId() + "_Count", countSqlSource);
		countStmtBuilder.resultMaps(getCountResultMaps(stmt));
		invocation.getArgs()[PageConst.IDX_MAPPED_STATEMENT] = countStmtBuilder.build();
		invocation.getArgs()[PageConst.IDX_PARAMETER_OBJECT] = parameterObject;
		Object result = invocation.proceed();
		long total = ((List<Long>) result).get(0);
		return total;
	}

	protected List<?> queryPage(BoundSql boundSql, Object parameterObject, Pageable pageable)
			throws InvocationTargetException, IllegalAccessException {
		MappedStatement stmt = getMappedStatement();
		String sql = boundSql.getSql();
		PageSqlParser sqlParser = new PageSqlParser(sql, globalConfig.getDialectType());
		String pageSql = sqlParser.getPageSql(pageable);
		List<ParameterMapping> parameterMappings = getParameterMappingWithPage(stmt.getConfiguration(), boundSql,
				pageable);
		StaticSqlSource pageSqlSource = new StaticSqlSource(stmt.getConfiguration(), pageSql, parameterMappings);
		MappedStatement.Builder pageStmtBuilder = ConfigurationHelper.createMappedStatementBuilder(stmt,
				stmt.getId() + "_Page", pageSqlSource);
		invocation.getArgs()[PageConst.IDX_MAPPED_STATEMENT] = pageStmtBuilder.build();
		invocation.getArgs()[PageConst.IDX_PARAMETER_OBJECT] = parameterObject;
		Object result = invocation.proceed();
		List<?> content = (List<?>) result;
		return content;
	}

	protected List<ParameterMapping> getParameterMappingWithPage(Configuration configuration, BoundSql boundSql,
			Pageable pageable) {
		List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
		parameterMappings.addAll(boundSql.getParameterMappings());
		// 增加分页参数的parameterMapping
		ParameterMapping offsetParameterMapping = new ParameterMapping.Builder(configuration,
				PageConst.PLACEHOLDER_OFFSET, Integer.class).build();
		parameterMappings.add(offsetParameterMapping);

		ParameterMapping limitParameterMapping = new ParameterMapping.Builder(configuration,
				PageConst.PLACEHOLDER_LIMIT, Integer.class).build();
		parameterMappings.add(limitParameterMapping);
		return parameterMappings;
	}

}
