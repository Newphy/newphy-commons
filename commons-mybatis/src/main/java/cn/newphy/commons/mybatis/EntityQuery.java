package cn.newphy.commons.mybatis;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

public interface EntityQuery<T> {

	/**
	 * 等于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityQuery<T> eq(String propertyName, Object value);

	/**
	 * 不等于
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityQuery<T> ne(String propertyName, Object value);

	/**
	 * 大于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityQuery<T> gt(String propertyName, Object value);

	/**
	 * 大于等于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityQuery<T> ge(String propertyName, Object value);

	/**
	 * 小于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityQuery<T> lt(String propertyName, Object value);

	/**
	 * 小于等于属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityQuery<T> le(String propertyName, Object value);

	/**
	 * 在low和high属性值之间
	 * 
	 * @param propertyName
	 * @param low
	 * @param high
	 * @return
	 */
	EntityQuery<T> between(String propertyName, Object low, Object high);

	/**
	 * 模糊匹配属性值
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	EntityQuery<T> like(String propertyName, String value);

	/**
	 * in查询
	 * 
	 * @param propertyName
	 * @param values
	 * @return
	 */
	EntityQuery<T> in(String propertyName, Object[] values);

	/**
	 * in查询
	 * 
	 * @param propertyName
	 * @param values
	 * @return
	 */
	EntityQuery<T> in(String propertyName, Collection<?> values);

	/**
	 * is null
	 * 
	 * @param propertyName
	 * @return
	 */
	EntityQuery<T> isNull(String propertyName);

	/**
	 * not null
	 * 
	 * @param propertyName
	 * @return
	 */
	EntityQuery<T> notNull(String propertyName);

	/**
	 * and操作
	 * 
	 * @param expression
	 * @return
	 */
	EntityQuery<T> and(QueryExpression expression);

	/**
	 * or操作
	 * 
	 * @param expression
	 * @return
	 */
	EntityQuery<T> or(QueryExpression exp1, QueryExpression exp2);

	/**
	 * 正排序
	 * 
	 * @param propertyName
	 * @return
	 */
	EntityQuery<T> orderAsc(String propertyName);

	/**
	 * 倒排序
	 * 
	 * @param propertyName
	 * @return
	 */
	EntityQuery<T> orderDesc(String propertyName);

	/**
	 * 查询列表
	 * 
	 * @return
	 */
	List<T> list();

	/**
	 * 分页查询
	 * 
	 * @param page
	 * @return
	 */
	Page<T> page(Page<T> page);

	/**
	 * 获得唯一一个对象
	 * <p>
	 * 如果结果集为空,返回null;如果结果集大于一个,抛IncorrectResultSizeDataAccessException异常
	 * </p>
	 * 
	 * @return
	 */
	T unique() throws IncorrectResultSizeDataAccessException;

	/**
	 * 获得任意一个对象
	 * <p>
	 * 如果结果集为空，返回null；如果结果集大于一个，返回第一个
	 * </p>
	 * 
	 * @return
	 */
	T any();
	
	/**
	 * 获得数量
	 * @return
	 */
	int count();
}
