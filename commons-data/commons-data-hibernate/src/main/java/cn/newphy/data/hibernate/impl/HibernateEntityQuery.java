package cn.newphy.data.hibernate.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import cn.newphy.data.Page1;
import cn.newphy.data.hibernate.query.EntityQuery;
import cn.newphy.data.hibernate.query.QueryExpression;
import cn.newphy.data.hibernate.query.QueryLanguage;

public class HibernateEntityQuery<T> implements EntityQuery<T> {
	private final static String SELECT = "SELECT ";
	private final static String WHERE = " WHERE ";
	private final static String AND = " AND ";
	private final static String OR = " OR ";
	private final static String ASC = "ASC";
	private final static String DESC = "DESC";
	private final static String ORDER_BY = " ORDER BY ";
	
	private HibernateHelper hibernateHelper;
	@SuppressWarnings("unused")
	private Class<T> entityClass;
	private StringBuffer queryHql = new StringBuffer();
	private StringBuffer whereHql = new StringBuffer();
	private StringBuffer orderHql = new StringBuffer();

	private List<Object> values = new ArrayList<Object>();

	public HibernateEntityQuery(Class<T> entityClass, HibernateHelper hibernateHelper) {
		this.entityClass = entityClass;
		this.hibernateHelper = hibernateHelper;
		queryHql.append(HqlUtils.createQueryHql(entityClass, null));
	}

	@Override
	public EntityQuery<T> eq(String propertyName, Object value) {
		return and(QueryExpression.eq(propertyName, value));
	}

	@Override
	public EntityQuery<T> ne(String propertyName, Object value) {
		return and(QueryExpression.ne(propertyName, value));
	}

	@Override
	public EntityQuery<T> gt(String propertyName, Object value) {
		return and(QueryExpression.gt(propertyName, value));
	}

	@Override
	public EntityQuery<T> ge(String propertyName, Object value) {
		return and(QueryExpression.ge(propertyName, value));
	}

	@Override
	public EntityQuery<T> lt(String propertyName, Object value) {
		return and(QueryExpression.lt(propertyName, value));
	}

	@Override
	public EntityQuery<T> le(String propertyName, Object value) {
		return and(QueryExpression.le(propertyName, value));
	}

	@Override
	public EntityQuery<T> between(String propertyName, Object low, Object high) {
		return and(QueryExpression.between(propertyName, low, high));
	}

	@Override
	public EntityQuery<T> like(String propertyName, String value) {
		return and(QueryExpression.like(propertyName, value));
	}

	@Override
	public EntityQuery<T> in(String propertyName, Object[] values) {
		return and(QueryExpression.in(propertyName, values));
	}

	@Override
	public EntityQuery<T> in(String propertyName, Collection<?> values) {
		return and(QueryExpression.in(propertyName, values));
	}

	@Override
	public EntityQuery<T> isNull(String propertyName) {
		return and(QueryExpression.isNull(propertyName));
	}

	@Override
	public EntityQuery<T> notNull(String propertyName) {
		return and(QueryExpression.notNull(propertyName));
	}

	@Override
	public EntityQuery<T> and(QueryExpression expression) {
		if (expression != null) {
			whereHql.append(whereHql.length() == 0 ? WHERE : AND);
			whereHql.append("t.").append(expression.toSql(QueryLanguage.HQL));
			Object[] valueArray = expression.getValues();
			if (valueArray != null) {
				values.addAll(Arrays.asList(valueArray));
			}
		}
		return HibernateEntityQuery.this;
	}

	@Override
	public EntityQuery<T> or(QueryExpression exp1, QueryExpression exp2) {
		if (exp1 != null && exp2 != null) {
			whereHql.append(whereHql.length() == 0 ? WHERE : AND);
			whereHql.append("((t.").append(exp1.toSql(QueryLanguage.HQL)).append(")").append(OR).append("(t.").append(exp2.toSql(QueryLanguage.HQL)).append("))");
			values.addAll(Arrays.asList(exp1.getValues()));
			values.addAll(Arrays.asList(exp2.getValues()));
		}
		return HibernateEntityQuery.this;
	}

	@Override
	public EntityQuery<T> orderAsc(String propertyName) {
		return orderBy(propertyName, ASC);
	}

	@Override
	public EntityQuery<T> orderDesc(String propertyName) {
		return orderBy(propertyName, DESC);
	}


	@Override
	public List<T> list() {
		return hibernateHelper.find(generateHql(), values.toArray());
	}

	@Override
	public Page1<T> page(Page1<T> page) {
		return hibernateHelper.findPage(generateHql(), page, values.toArray());
	}
	
	@Override
	public T unique() throws IncorrectResultSizeDataAccessException {
		return  hibernateHelper.findUnique(generateHql(), values.toArray());
	}

	@Override
	public T any() {
		return  hibernateHelper.findAny(generateHql(), values.toArray());
	}

	@Override
	public int count() {
		return (Integer)hibernateHelper.findAny(generateCountHql(), values.toArray());
	}

	private EntityQuery<T> orderBy(String propertyName, String order) {
		if(StringUtils.isNotBlank(propertyName)) {
			if(orderHql.length() == 0) {
				orderHql.append(ORDER_BY);
			}
			orderHql.append(" t.").append(propertyName).append(" ").append(order);
		}
		return HibernateEntityQuery.this;
	}
	
	private StringBuffer generateHql() {
		StringBuffer hql = new StringBuffer();
		hql.append(queryHql).append(whereHql).append(orderHql);
		return hql;
	}

	private StringBuffer generateCountHql() {
		StringBuffer hql = new StringBuffer();
		hql.append(SELECT).append("count(1) ");
		hql.append(queryHql).append(whereHql).append(orderHql);
		return hql;
	}}
