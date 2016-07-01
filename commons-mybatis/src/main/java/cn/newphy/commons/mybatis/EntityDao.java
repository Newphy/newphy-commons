package cn.newphy.commons.mybatis;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 14-3-11.
 */
public interface EntityDao<T> {

	EntityQuery<T> query();

	/**
	 * 保存实体
	 * 
	 * @param entity
	 */
	void save(T entity);

	/**
	 * 批量保存实体
	 * 
	 * @param entities
	 */
	void batchSave(Collection<T> entities);

	/**
	 * 批量保存实体
	 * 
	 * @param entities
	 */
	void batchSave(T[] entities);

	/**
	 * 修改实体
	 * 
	 * @param entity
	 */
	void update(T entity);

	/**
	 * 批量更新实体
	 * 
	 * @param entities
	 */
	void batchUpdate(Collection<T> entities);

	/**
	 * 批量更新实体
	 * 
	 * @param entities
	 */
	void batchUpdate(T[] entities);

	/**
	 * 删除实体
	 * 
	 * @param entity
	 */
	void delete(T entity);

	/**
	 * 删除所有实体
	 */
	void deleteAll();

	/**
	 * 批量删除实体
	 * 
	 * @param entities
	 */
	void batchDelete(Collection<T> entities);

	/**
	 * 批量删除实体
	 * 
	 * @param entities
	 */
	void batchDelete(T[] entities);

	/**
	 * 删除实体
	 * 
	 * @param id
	 */
	void deleteById(Serializable id);

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
	List<T> getAll(String... orders);

	/**
	 * 根据属性获得实体对象列表
	 * 
	 * @param propName
	 * @param value
	 * @param orders
	 * @return
	 */
	List<T> getBy(String propName, Object value, String... orders);

	/**
	 * 根据属性获得实体对象列表
	 * 
	 * @param propNames
	 * @param values
	 * @param orders
	 * @return
	 */
	List<T> getBy(String[] propNames, Object[] values, String... orders);
	
	
	/**
	 * 根据属性获得实体对象
	 * 
	 * @param propName
	 * @param value
	 * @return
	 */
	T getAnyBy(String propName, Object value);

	/**
	 * 根据属性获得实体对象
	 * 
	 * @param propNames
	 * @param values
	 * @return
	 */
	T getAnyBy(String[] propNames, Object[] values);		

	/**
	 * 获得实体的分页列表
	 * 
	 * @param page
	 * @param orders
	 * @return
	 */
	Page<T> getPage(Page<T> page, String... orders);

	/**
	 * 根据属性获得实体对象分页列表
	 * 
	 * @param page
	 * @param propNames
	 * @param values
	 * @param orders
	 * @return
	 */
	Page<T> getPageBy(Page<T> page, String[] propNames, Object[] values, String... orders);

	/**
	 * 获得所有实体对象数量
	 * 
	 * @return
	 */
	int count();

	/**
	 * 根据属性获得实体对象数量
	 * 
	 * @param propNames
	 * @param values
	 * @return
	 */
	int count(String[] propNames, Object[] values);
	
	/**
	 * flush
	 */
	void flush();

}
