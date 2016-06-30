package cn.newphy.commons.hibernate.query;

import java.util.Collection;

public abstract class QueryExpression {

	protected QueryExpression() {
	}

	/**
	 * 等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static QueryExpression eq(String propertyName, Object value) {
		return new OperatorQueryExpression(propertyName, value, "=");
	}

	/**
	 * 不等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static QueryExpression ne(String propertyName, Object value) {
		return new OperatorQueryExpression(propertyName, value, "<>");
	}

	/**
	 * like
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static QueryExpression like(String propertyName, String value) {
		return new OperatorQueryExpression(propertyName, value, "LIKE");
	}

	/**
	 * 大于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static QueryExpression gt(String propertyName, Object value) {
		return new OperatorQueryExpression(propertyName, value, ">");
	}

	/**
	 * 大于等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static QueryExpression ge(String propertyName, Object value) {
		return new OperatorQueryExpression(propertyName, value, ">=");
	}

	/**
	 * 小于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static QueryExpression lt(String propertyName, Object value) {
		return new OperatorQueryExpression(propertyName, value, "<");
	}

	/**
	 * 小于等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public static QueryExpression le(String propertyName, Object value) {
		return new OperatorQueryExpression(propertyName, value, "<=");
	}

	/**
	 * between
	 * 
	 * @param propertyName
	 * @param lo
	 *            低值
	 * @param hi
	 *            高值
	 * @return
	 */
	public static QueryExpression between(String propertyName, Object lo, Object hi) {
		return new BetweenQueryExpression(propertyName, lo, hi);
	}

	/**
	 * in
	 * 
	 * @param propertyName
	 * @param values
	 * @return
	 */
	public static QueryExpression in(String propertyName, Object[] values) {
		return new InQueryExpression(propertyName, values);
	}

	/**
	 * in 操作
	 * 
	 * @param propertyName
	 * @param values
	 * @return
	 */
	public static QueryExpression in(String propertyName, Collection<?> values) {
		return new InQueryExpression(propertyName, values == null ? null : values.toArray());
	}

	/**
	 * 为空
	 * 
	 * @param propertyName
	 * @return
	 */
	public static QueryExpression isNull(String propertyName) {
		return new SimpleQueryExpression(propertyName, " IS NULL");
	}

	/**
	 * 不为空
	 * 
	 * @param propertyName
	 * @return
	 */
	public static QueryExpression notNull(String propertyName) {
		return new SimpleQueryExpression(propertyName, " IS NOT NULL");
	}

	/**
	 * 转化为sql
	 * 
	 * @return
	 */
	public abstract CharSequence toSql(QueryLanguage language);
	
	/**
	 * 获得相关参数值
	 * @return
	 */
	public abstract Object[] getValues();
}
