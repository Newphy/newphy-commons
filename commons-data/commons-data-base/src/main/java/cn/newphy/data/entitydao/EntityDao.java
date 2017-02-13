package cn.newphy.data.entitydao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import cn.newphy.data.domain.Order;
import cn.newphy.data.domain.Page;
import cn.newphy.data.domain.Pageable;
import cn.newphy.data.exception.OptimisticLockException;

/**
 * Created by Administrator on 14-3-11.
 */
public interface EntityDao<T> {

	/**
	 * 构建查询器
	 * 
	 * @return
	 */
	EntityQuery<T> createQuery();

	/**
	 * 构建更新器
	 * 
	 * @return
	 */
	EntityUpdate<T> createUpdate();

	/**
	 * 保存实体
	 * 
	 * @param entity
	 */
	int save(T entity);

	/**
	 * 批量保存实体
	 * 
	 * @param entities
	 */
	int batchSave(Collection<T> entities);

	/**
	 * 批量保存实体
	 * 
	 * @param entities
	 */
	int batchSave(T[] entities);

	/**
	 * 修改实体
	 * 
	 * @param entity
	 */
	int update(T entity);

	/**
	 * 乐观更新
	 * 
	 * @param entity
	 * @param versionProperty
	 * @return
	 */
	int updateOptimistic(T entity) throws OptimisticLockException;

	/**
	 * 批量更新实体
	 * 
	 * @param entities
	 */
	int batchUpdate(Collection<T> entities);

	/**
	 * 批量更新实体
	 * 
	 * @param entities
	 */
	int batchUpdate(T[] entities);

	/**
	 * 删除实体
	 * 
	 * @param entity
	 */
	int delete(T entity);

	/**
	 * 删除所有实体
	 */
	int deleteAll();

	/**
	 * 批量删除实体
	 * 
	 * @param entities
	 */
	int batchDelete(Collection<T> entities);

	/**
	 * 批量删除实体
	 * 
	 * @param entities
	 */
	int batchDelete(T[] entities);

	/**
	 * 删除实体
	 * 
	 * @param id
	 */
	int deleteById(Serializable id);

	/**
	 * 获得实体
	 * 
	 * @param id
	 * @return
	 */
	T get(Serializable id);

	/**
	 * 获得所有实体列表
	 * 
	 * @param orders
	 * @return
	 */
	List<T> getAll(Order... orders);

	/**
	 * 根据属性获得实体对象列表
	 * 
	 * @param propName
	 * @param value
	 * @param orders
	 * @return
	 */
	List<T> getBy(String propName, Object value, Order... orders);

	/**
	 * 根据属性获得实体对象列表
	 * 
	 * @param propNames
	 * @param values
	 * @param orders
	 * @return
	 */
	List<T> getBy(String[] propNames, Object[] values, Order... orders);

	/**
	 * 根据模板获得列表
	 * 
	 * @param template
	 * @param orders
	 * @return
	 */
	List<T> getByTemplate(T template, Order... orders);

	/**
	 * 根据属性获得实体对象
	 * 
	 * @param propName
	 * @param value
	 * @return
	 */
	T getOneBy(String propName, Object value);

	/**
	 * 根据属性获得实体对象
	 * 
	 * @param propNames
	 * @param values
	 * @return
	 */
	T getOneBy(String[] propNames, Object[] values);

	/**
	 * 获得任意一个
	 * 
	 * @param template
	 * @return
	 */
	T getOneByTemplate(T template);

	/**
	 * 获得实体的分页列表
	 * 
	 * @param page
	 * @param orders
	 * @return
	 */
	Page<T> getPage(Pageable pageable);

	/**
	 * 根据模板获得分页数据
	 * 
	 * @param page
	 * @param template
	 * @param orders
	 * @return
	 */
	Page<T> getPageByTemplate(Pageable pageable, T template);

	/**
	 * 根据属性获得实体对象分页列表
	 * 
	 * @param page
	 * @param propNames
	 * @param values
	 * @param orders
	 * @return
	 */
	Page<T> getPageBy(Pageable pageable, String[] propNames, Object[] values);

	/**
	 * 获得所有实体对象数量
	 * 
	 * @return
	 */
	int count();

	/**
	 * 根据属性获得记录数量
	 * 
	 * @param propName
	 * @param value
	 * @return
	 */
	int count(String propName, Object value);

	/**
	 * 根据属性获得实体对象数量
	 * 
	 * @param propNames
	 * @param values
	 * @return
	 */
	int count(String[] propNames, Object[] values);

	/**
	 * 根据模板获得记录数量
	 * 
	 * @param template
	 * @return
	 */
	int countByTemplate(T template);

	/**
	 * flush
	 */
	void flush();

}
