package cn.newphy.data.mybatis.sql.expression;

import java.util.Collection;

public abstract class SQLExpression {

	protected SQLExpression() {
	}
//
//	/**
//	 * 等于
//	 * 
//	 * @param propertyName
//	 * @param value
//	 * @return
//	 */
//	public static SQLExpression eq(String propertyName, Object value) {
//		return new OperatorSQLExpression(propertyName, value, "=");
//	}
//
//	/**
//	 * 不等于
//	 * 
//	 * @param propertyName
//	 * @param value
//	 * @return
//	 */
//	public static SQLExpression ne(String propertyName, Object value) {
//		return new OperatorSQLExpression(propertyName, value, "<>");
//	}
//
//	/**
//	 * like
//	 * 
//	 * @param propertyName
//	 * @param value
//	 * @return
//	 */
//	public static SQLExpression like(String propertyName, String value) {
//		return new OperatorSQLExpression(propertyName, value, "LIKE");
//	}
//
//	/**
//	 * 大于
//	 * 
//	 * @param propertyName
//	 * @param value
//	 * @return
//	 */
//	public static SQLExpression gt(String propertyName, Object value) {
//		return new OperatorSQLExpression(propertyName, value, ">");
//	}
//
//	/**
//	 * 大于等于
//	 * 
//	 * @param propertyName
//	 * @param value
//	 * @return
//	 */
//	public static SQLExpression ge(String propertyName, Object value) {
//		return new OperatorSQLExpression(propertyName, value, ">=");
//	}
//
//	/**
//	 * 小于
//	 * 
//	 * @param propertyName
//	 * @param value
//	 * @return
//	 */
//	public static SQLExpression lt(String propertyName, Object value) {
//		return new OperatorSQLExpression(propertyName, value, "<");
//	}
//
//	/**
//	 * 小于等于
//	 * 
//	 * @param propertyName
//	 * @param value
//	 * @return
//	 */
//	public static SQLExpression le(String propertyName, Object value) {
//		return new OperatorSQLExpression(propertyName, value, "<=");
//	}
//
//	/**
//	 * between
//	 * 
//	 * @param propertyName
//	 * @param lo
//	 *            低值
//	 * @param hi
//	 *            高值
//	 * @return
//	 */
//	public static SQLExpression between(String propertyName, Object lo, Object hi) {
//		return new BetweenSQLExpression(propertyName, lo, hi);
//	}
//
//	/**
//	 * in
//	 * 
//	 * @param propertyName
//	 * @param values
//	 * @return
//	 */
//	public static SQLExpression in(String propertyName, Object[] values) {
//		return new InSQLExpression(propertyName, values);
//	}
//
//	/**
//	 * in 操作
//	 * 
//	 * @param propertyName
//	 * @param values
//	 * @return
//	 */
//	public static SQLExpression in(String propertyName, Collection<?> values) {
//		return new InSQLExpression(propertyName, values == null ? null : values.toArray());
//	}
//
//	/**
//	 * 为空
//	 * 
//	 * @param propertyName
//	 * @return
//	 */
//	public static SQLExpression isNull(String propertyName) {
//		return new SimpleSQLExpression(propertyName, " IS NULL");
//	}
//
//	/**
//	 * 不为空
//	 * 
//	 * @param propertyName
//	 * @return
//	 */
//	public static SQLExpression notNull(String propertyName) {
//		return new SimpleSQLExpression(propertyName, " IS NOT NULL");
//	}

	/**
	 * 转化为sql
	 * 
	 * @return
	 */
	public abstract CharSequence toSql();
	
	/**
	 * 获得相关参数值
	 * @return
	 */
	public abstract Object[] getValues();
}
