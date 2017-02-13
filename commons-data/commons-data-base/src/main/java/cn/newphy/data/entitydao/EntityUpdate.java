package cn.newphy.data.entitydao;

import java.io.Serializable;
import java.util.Collection;

import cn.newphy.data.exception.OptimisticLockException;

public interface EntityUpdate<T> {
	
	
	/**
	 * 设置值
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityUpdate<T> set(String propertyName, Object value);
	
	/**
	 * 设置值
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityUpdate<T> set(String[] propertyNames, Object[] value);
	

	/**
	 * 等于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityUpdate<T> eq(String propertyName, Object value);

	/**
	 * 不等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityUpdate<T> ne(String propertyName, Object value);

	/**
	 * 大于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityUpdate<T> gt(String propertyName, Object value);

	/**
	 * 大于等于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityUpdate<T> ge(String propertyName, Object value);

	/**
	 * 小于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityUpdate<T> lt(String propertyName, Object value);

	/**
	 * 小于等于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityUpdate<T> le(String propertyName, Object value);

	/**
	 * 在low和high属性值之间
	 * 
	 * @param propertyName
	 * @param low
	 * @param high
	 * @return
	 */
	EntityUpdate<T> between(String propertyName, Object low, Object high);

	/**
	 * 模糊匹配属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityUpdate<T> like(String propertyName, String value);

	/**
	 * in查询
	 * 
	 * @param propertyName
	 * @param values
	 * @return
	 */
	EntityUpdate<T> in(String propertyName, Object[] values);

	/**
	 * in查询
	 * 
	 * @param propertyName
	 * @param values
	 * @return
	 */
	EntityUpdate<T> in(String propertyName, Collection<?> values);

	/**
	 * is null
	 * 
	 * @param propertyName
	 * @return
	 */
	EntityUpdate<T> isNull(String propertyName);

	/**
	 * not null
	 * 
	 * @param propertyName
	 * @return
	 */
	EntityUpdate<T> notNull(String propertyName);

	/**
	 * and操作
	 * 
	 * @param expression
	 * @return
	 */
	EntityUpdate<T> and(ConditionExpression expression);

	/**
	 * or操作
	 * 
	 * @param expression
	 * @return
	 */
	EntityUpdate<T> or(ConditionExpression exp1, ConditionExpression exp2, ConditionExpression... expN);

	/**
	 * 更新
	 * 
	 * @return
	 */
	int update();
	
	/**
	 * 乐观锁更新
	 * @param id
	 * @param currentVersion
	 * @return
	 * @throws OptimisticLockException
	 */
	int updateOptimistic(Serializable id, int currentVersion) throws OptimisticLockException;

}
