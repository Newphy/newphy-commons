package cn.newphy.data.entitydao.mybatis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.ResultMapping;
import org.springframework.util.Assert;

import cn.newphy.data.entitydao.ConditionExpression;
import cn.newphy.data.entitydao.EntityUpdate;
import cn.newphy.data.entitydao.UpdateExpression;
import cn.newphy.data.entitydao.mybatis.builder.MybatisSqlBuilder;
import cn.newphy.data.entitydao.mybatis.expression.MybatisUpdateExpression;
import cn.newphy.data.entitydao.mybatis.expression.RawSqlUpdateExpression;
import cn.newphy.data.exception.OptimisticLockException;

public class MybatisEntityUpdate<T> implements EntityUpdate<T> {

	private Map<String, UpdateExpression> update = new LinkedHashMap<String, UpdateExpression>();
	private List<ConditionExpression> where = new ArrayList<ConditionExpression>();

	private final MybatisEntityDao<T> entityDao;
	private final GlobalConfig configuration;

	MybatisEntityUpdate(GlobalConfig configuration, MybatisEntityDao<T> entityDao) {
		this.configuration = configuration;
		this.entityDao = entityDao;
	}

	@Override
	public EntityUpdate<T> set(String propertyName, Object value) {
		EntityMapping entityMapping = entityDao.getEntityMapping();
		ResultMapping resultMapping = entityMapping.getResultMappingByProperty(propertyName);
		if(resultMapping == null) {
			throw new IllegalArgumentException("ResultMap[" + entityMapping.getSourceId() + "]没有属性名为[" + propertyName + "]的映射");
		}
		update.put(propertyName, new MybatisUpdateExpression(resultMapping, value));
		return MybatisEntityUpdate.this;
	}
	

	@Override
	public EntityUpdate<T> set(String[] propertyNames, Object[] values) {
		Assert.notNull(propertyNames, "propertyNames不能为null");
		Assert.notNull(propertyNames, "values不能为null");
		Assert.isTrue(propertyNames.length == values.length, "数组参数长度不一致");
		for(int i = 0; i < propertyNames.length; i++) {
			set(propertyNames[i], values[i]);
		}
		return MybatisEntityUpdate.this;
	}

	@Override
	public EntityUpdate<T> eq(String propertyName, Object value) {
		return and(ConditionExpressions.eq(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityUpdate<T> ne(String propertyName, Object value) {
		return and(ConditionExpressions.ne(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityUpdate<T> gt(String propertyName, Object value) {
		return and(ConditionExpressions.gt(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityUpdate<T> ge(String propertyName, Object value) {
		return and(ConditionExpressions.ge(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityUpdate<T> lt(String propertyName, Object value) {
		return and(ConditionExpressions.lt(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityUpdate<T> le(String propertyName, Object value) {
		return and(ConditionExpressions.le(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityUpdate<T> between(String propertyName, Object low, Object high) {
		return and(ConditionExpressions.between(entityDao.getEntityMapping(), propertyName, low, high));
	}

	@Override
	public EntityUpdate<T> like(String propertyName, String value) {
		return and(ConditionExpressions.like(entityDao.getEntityMapping(), propertyName, value));
	}

	@Override
	public EntityUpdate<T> in(String propertyName, Object[] values) {
		return and(ConditionExpressions.in(entityDao.getEntityMapping(), propertyName, values));
	}

	@Override
	public EntityUpdate<T> in(String propertyName, Collection<?> values) {
		return and(ConditionExpressions.in(entityDao.getEntityMapping(), propertyName, values));
	}

	@Override
	public EntityUpdate<T> isNull(String propertyName) {
		return and(ConditionExpressions.isNull(entityDao.getEntityMapping(), propertyName));
	}

	@Override
	public EntityUpdate<T> notNull(String propertyName) {
		return and(ConditionExpressions.notNull(entityDao.getEntityMapping(), propertyName));
	}

	@Override
	public EntityUpdate<T> and(ConditionExpression expression) {
		if (expression != null) {
			where.add(expression);
		}
		return MybatisEntityUpdate.this;
	}

	@Override
	public EntityUpdate<T> or(ConditionExpression exp1, ConditionExpression exp2, ConditionExpression... expN) {
		return and(ConditionExpressions.or(exp1, exp2, expN));
	}

	@Override
	public int update() {
		return entityDao.update(toSql(), getParamMap());
	}
	
	@Override
	public int updateOptimistic(Serializable id, int currentVersion) throws OptimisticLockException {
		EntityMapping entityMapping = entityDao.getEntityMapping();
		ResultMapping versionMapping = entityMapping.getVersionMapping();
		if(versionMapping == null) {
			throw new IllegalStateException("没有设置版本字段");
		}
		ResultMapping idMapping = entityMapping.getIdMapping();
		// 增加set
		String versionSql = versionMapping.getColumn() + " = " + versionMapping.getColumn() + " + 1";
		update.put(versionMapping.getProperty(), new RawSqlUpdateExpression(versionSql));
		// 增加id条件
		and(ConditionExpressions.eq(entityMapping, idMapping.getProperty(), id));
		and(ConditionExpressions.eq(entityMapping, versionMapping.getProperty(), currentVersion));
		int c = update();
		if(c == 0) {
			throw new OptimisticLockException();
		}
		return c;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getParamMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 更新
		for(String propertyName : update.keySet()) {
			UpdateExpression expression = update.get(propertyName);
			Object value = expression.getValue();
			if (value == EmptyValue.VALUE) {
				continue;
			}
			if (value instanceof Map) {
				paramMap.putAll((Map<String, Object>) value);
			}			
		}
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
		MybatisSqlBuilder dialect = MybatisSqlBuilder.getMybatisSqlBuilder(configuration.getDialectType());;
		String tableName = entityDao.getEntityMapping().getTableName();
		List<CharSequence> updateFragments = new ArrayList<CharSequence>();
		for(String propertyName : update.keySet()) {
			UpdateExpression expression = update.get(propertyName);
			updateFragments.add(expression.toSql(dialect));
		}
		StringBuffer sql = new StringBuffer();
		// update
		sql.append(dialect.getUpdateString(tableName, updateFragments));
		// where
		List<CharSequence> conditions = new ArrayList<CharSequence>();
		for (ConditionExpression expression : where) {
			conditions.add(expression.toSql(dialect));
		}
		sql.append(dialect.getWhereString(conditions));
		return sql.toString();
	}


}
