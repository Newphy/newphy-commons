package cn.newphy.data.entitydao;

import java.util.Collection;

public interface Where {


	/**
	 * 等于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Where eq(String propertyName, Object value);

	/**
	 * 不等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Where ne(String propertyName, Object value);

	/**
	 * 大于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Where gt(String propertyName, Object value);

	/**
	 * 大于等于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Where ge(String propertyName, Object value);

	/**
	 * 小于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Where lt(String propertyName, Object value);

	/**
	 * 小于等于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Where le(String propertyName, Object value);

	/**
	 * 在low和high属性值之间
	 * 
	 * @param propertyName
	 * @param low
	 * @param high
	 * @return
	 */
	Where between(String propertyName, Object low, Object high);

	/**
	 * 模糊匹配属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	Where like(String propertyName, String value);

	/**
	 * in查询
	 * 
	 * @param propertyName
	 * @param values
	 * @return
	 */
	Where in(String propertyName, Object[] values);

	/**
	 * in查询
	 * 
	 * @param propertyName
	 * @param values
	 * @return
	 */
	Where in(String propertyName, Collection<?> values);

	/**
	 * is null
	 * 
	 * @param propertyName
	 * @return
	 */
	Where isNull(String propertyName);

	/**
	 * not null
	 * 
	 * @param propertyName
	 * @return
	 */
	Where notNull(String propertyName);

	/**
	 * and操作
	 * 
	 * @param expression
	 * @return
	 */
	Where and(ConditionExpression expression);

	/**
	 * or操作
	 * 
	 * @param expression
	 * @return
	 */
	Where or(ConditionExpression exp1, ConditionExpression exp2, ConditionExpression... expN);

}
