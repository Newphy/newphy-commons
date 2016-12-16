package cn.newphy.data.entitydao.mybatis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.ResultMapping;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.util.Assert;

import cn.newphy.data.domain.Direction;
import cn.newphy.data.domain.Order;
import cn.newphy.data.domain.Page;
import cn.newphy.data.domain.Pageable;
import cn.newphy.data.entitydao.ConditionExpression;
import cn.newphy.data.entitydao.EntityQuery;
import cn.newphy.data.entitydao.mybatis.builder.MybatisSqlBuilder;
import cn.newphy.data.entitydao.mybatis.plugins.paginator.PageConst;

public class MybatisEntityQuery<T> implements EntityQuery<T> {

	private List<ConditionExpression> where = new ArrayList<ConditionExpression>();
	private List<Order> orders = new ArrayList<Order>();
	
	private final MybatisEntityDao<T> entityDao;
	private final EConfiguration configuration;
	

	MybatisEntityQuery(EConfiguration configuration, MybatisEntityDao<T> entityDao) {
		this.configuration = configuration;
		this.entityDao = entityDao;
	}


	@Override
	public EntityQuery<T> eq(String propertyName, Object value) {
		return and(ConditionExpressions.eq(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityQuery<T> ne(String propertyName, Object value) {
		return and(ConditionExpressions.ne(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityQuery<T> gt(String propertyName, Object value) {
		return and(ConditionExpressions.gt(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityQuery<T> ge(String propertyName, Object value) {
		return and(ConditionExpressions.ge(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityQuery<T> lt(String propertyName, Object value) {
		return and(ConditionExpressions.lt(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityQuery<T> le(String propertyName, Object value) {
		return and(ConditionExpressions.le(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityQuery<T> between(String propertyName, Object low, Object high) {
		return and(ConditionExpressions.between(entityDao.getEntityMapping(), propertyName, low, high));
	}

	@Override
	public EntityQuery<T> like(String propertyName, String value) {
		return and(ConditionExpressions.like(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityQuery<T> in(String propertyName, Object[] values) {
		return and(ConditionExpressions.in(entityDao.getEntityMapping(), propertyName, values));
	}

	@Override
	public EntityQuery<T> in(String propertyName, Collection<?> values) {
		return and(ConditionExpressions.in(entityDao.getEntityMapping(), propertyName, values));
	}

	@Override
	public EntityQuery<T> isNull(String propertyName) {
		return and(ConditionExpressions.isNull(entityDao.getEntityMapping(), propertyName));
	}

	@Override
	public EntityQuery<T> notNull(String propertyName) {
		return and(ConditionExpressions.notNull(entityDao.getEntityMapping(), propertyName));
	}

	@Override
	public EntityQuery<T> and(ConditionExpression expression) {
		if (expression != null) {
			where.add(expression);
		}
		return MybatisEntityQuery.this;
	}

	@Override
	public EntityQuery<T> or(ConditionExpression exp1, ConditionExpression exp2, ConditionExpression...  expN) {
		return and(ConditionExpressions.or(exp1, exp2, expN));
	}

	@Override
	public EntityQuery<T> orderAsc(String propertyName) {
		return orderBy(new Order(Direction.ASC, propertyName));
	}

	@Override
	public EntityQuery<T> orderDesc(String propertyName) {
		return orderBy(new Order(Direction.DESC, propertyName));
	}

	private EntityQuery<T> orderBy(Order order) {
		if(order != null) {
			String propertyName = order.getProperty();
			EntityMapping entityMapping = entityDao.getEntityMapping();
			ResultMapping resultMapping = entityMapping.getResultMappingByProperty(propertyName);
			if(resultMapping == null) {
				throw new IllegalArgumentException("ResultMap[" + entityMapping.getSourceId() + "]没有属性名为[" + propertyName + "]的映射");
			}
			order.setColumn(resultMapping.getColumn());
			orders.add(order);
		}
		return MybatisEntityQuery.this;
	}

	@Override
	public List<T> list() {
		return entityDao.selectList(toSql(), getParamMap());
	}

	@Override
	public Page<T> page(Pageable pageable) {
		Assert.notNull(pageable, "分页参数不能为空");
		// 初始化排序
		Map<String, Object> paramMap = getParamMap();
		paramMap.put(PageConst.PARAM_NAME_PAGE, pageable);
		return (Page<T>)entityDao.selectList(toSql(), paramMap);
	}

	@Override
	public T unique() throws IncorrectResultSizeDataAccessException {
		List<T> result = entityDao.selectList(toSql(), getParamMap());
		if(result.size() != 1) {
			throw new IncorrectResultSizeDataAccessException(1, result.size());
		}
		return result.iterator().next();
	}

	@Override
	public T one() {
		List<T> result = entityDao.selectList(toOneSql(), getParamMap());
		return result.size() > 0 ? result.get(0) : null;
	}

	@Override
	public int count() {
		return entityDao.selectObject(toCountSql(), getParamMap());
	}
	
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getParamMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 条件
		for(ConditionExpression expression : where) {
			Object value = expression.getValue();
			if (value == EmptyValue.VALUE) {
				continue;
			}
			if (value instanceof Map) {
				paramMap.putAll((Map<String, Object>) value);
			}			
		}		
		return paramMap;
	}
	
	
	private String toSql() {
		MybatisSqlBuilder sqlBuilder = configuration.getDialect();
		String tableName = entityDao.getEntityMapping().getTableName();
		List<String> selectColumns = getSelectColumns();
		StringBuffer sql = new StringBuffer();
		// select
		sql.append(sqlBuilder.getSelectString(tableName, selectColumns));
		// where
		List<CharSequence> conditions = new ArrayList<CharSequence>();
		for (ConditionExpression expression : where) {
			conditions.add(expression.toSql(sqlBuilder));
		}
		sql.append(sqlBuilder.getWhereString(conditions));
		// order
		if(orders.size() > 0) {
			sql.append(sqlBuilder.getOrderString(orders));
		}
		return sql.toString();
	}
	
	private String toOneSql() {
		MybatisSqlBuilder dialect = configuration.getDialect();
		String sql = toSql();
		if(dialect.supportsLimit()) {
			sql += dialect.getLimitString(0, 1);
		}
		return sql;
	}
	
	
	private String toCountSql() {
		MybatisSqlBuilder dialect = configuration.getDialect();
		String tableName = entityDao.getEntityMapping().getTableName();
		StringBuilder countSql = new StringBuilder();
		// select count
		countSql.append(dialect.getCountString(tableName));
		// where
		List<CharSequence> conditions = new ArrayList<CharSequence>();
		for (ConditionExpression expression : where) {
			conditions.add(expression.toSql(dialect));
		}
		return countSql.toString();
	}
	
	
	private List<String> getSelectColumns() {
		List<String> columns = new ArrayList<String>();
		List<ResultMapping> resultMappings = entityDao.getEntityMapping().getResultMappings();
		for (int i = 0; i < resultMappings.size(); i++) {
			ResultMapping resultMapping = resultMappings.get(i);
			columns.add(resultMapping.getColumn());
		}
		return columns;
	}
	
}
