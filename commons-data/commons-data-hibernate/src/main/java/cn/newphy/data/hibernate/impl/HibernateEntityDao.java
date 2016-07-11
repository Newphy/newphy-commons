package cn.newphy.data.hibernate.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import cn.newphy.commons.lang.ReflectionUtils;
import cn.newphy.data.Page;
import cn.newphy.data.hibernate.EntityDao;
import cn.newphy.data.hibernate.EntityRowMapper;
import cn.newphy.data.hibernate.jdbc.JdbcHelper;
import cn.newphy.data.hibernate.query.EntityQuery;

public class HibernateEntityDao<T> implements EntityDao<T> {

	protected SessionFactory sessionFactory;
	protected HibernateHelper hibernateHelper;
	protected JdbcHelper jdbcHelper;
	protected Class<T> entityClass;

	public HibernateEntityDao() {
		this.entityClass = ReflectionUtils.getSuperClassGenericType(getClass());
	}

	public HibernateEntityDao(SessionFactory sessionFactory,
			Class<T> entityClass) {
		setSessionFactory(sessionFactory);
		this.entityClass = entityClass;
	}

	@Autowired
	public final void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;

		if (this.hibernateHelper == null
				|| sessionFactory != this.hibernateHelper.getSessionFactory()) {
			this.hibernateHelper = createHibernateHelper(sessionFactory);
		}

		DataSource dataSource = SessionFactoryUtils.getDataSource(sessionFactory);
		if (this.jdbcHelper == null || dataSource != this.jdbcHelper.getDataSource()) {
			this.jdbcHelper = createJdbcHelper(dataSource);
		}
	}

	protected HibernateHelper createHibernateHelper(
			SessionFactory sessionFactory) {
		return new HibernateHelper(sessionFactory);
	}
	
	protected JdbcHelper createJdbcHelper(DataSource dataSource) {
		return new JdbcHelper(dataSource);
	}

	@Override
	public void save(T entity) {
		hibernateHelper.save(entity);
	}

	@Override
	public void batchSave(Collection<T> entities) {
		hibernateHelper.batchSave(entities);
	}

	@Override
	public void batchSave(T[] entities) {
		hibernateHelper.batchSave(entities);
	}

	@Override
	public void update(T entity) {
		hibernateHelper.update(entity);
	}

	@Override
	public void batchUpdate(Collection<T> entities) {
		hibernateHelper.batchUpdate(entities);
	}

	@Override
	public void batchUpdate(T[] entities) {
		hibernateHelper.batchUpdate(entities);
	}

	@Override
	public void delete(T entity) {
		hibernateHelper.delete(entity);
	}

	@Override
	public void deleteAll() {
		hibernateHelper.deleteAll(entityClass);
	}

	@Override
	public void batchDelete(Collection<T> entities) {
		hibernateHelper.batchDelete(entities);
	}

	@Override
	public void batchDelete(T[] entities) {
		hibernateHelper.batchDelete(entities);
	}

	@Override
	public void deleteById(Serializable id) {
		hibernateHelper.deleteById(entityClass, id);
	}

	@Override
	public T get(Serializable id) {
		return hibernateHelper.get(entityClass, id);
	}

	@Override
	public List<T> getAll(String... orders) {
		return hibernateHelper.getAll(entityClass, orders);
	}

	@Override
	public List<T> getBy(String propName, Object value, String... orders) {
		return hibernateHelper.getBy(entityClass, new String[] { propName },
				new Object[] { value }, orders);
	}

	@Override
	public List<T> getBy(String[] propNames, Object[] values, String... orders) {
		return hibernateHelper.getBy(entityClass, propNames, values, orders);
	}
	
	@Override
	public T getAnyBy(String propName, Object value) {
		return hibernateHelper.getAnyBy(entityClass, new String[] { propName },
				new Object[] { value });
	}

	@Override
	public T getAnyBy(String[] propNames, Object[] values) {
		return hibernateHelper.getAnyBy(entityClass, propNames, values);
	}

	@Override
	public Page<T> getPage(Page<T> page, String... orders) {
		return hibernateHelper.getPage(entityClass, page, orders);
	}

	@Override
	public Page<T> getPageBy(Page<T> page, String[] propNames, Object[] values,
			String... orders) {
		return hibernateHelper.getPageBy(entityClass, page, propNames, values,
				orders);
	}
	

	@Override
	public int count() {
		return hibernateHelper.count(entityClass, null, null);
	}

	@Override
	public int count(String[] propNames, Object[] values) {
		return hibernateHelper.count(entityClass, propNames, values);
	}

	@Override
	public EntityQuery<T> query() {
		return new HibernateEntityQuery<T>(entityClass, hibernateHelper);
	}
	

	@Override
	public void flush() {
		hibernateHelper.flush();
	}
	

	public Class<T> getEntityClass() {
		return entityClass;
	}


	@Override
	public String toString() {
		ToStringBuilder.reflectionToString(this);
		return super.toString();
	}
	
	
	/**
	 * 鑾峰緱EntityRowMapper
	 * @param entityClass
	 * @return
	 */
	protected <C> EntityRowMapper<C> getEntityRowMapper(Class<C> entityClass) {
		return HibernateEnityRowMapper.getHibernateEntityRowMapper(sessionFactory, entityClass);
	}

	/**
	 * 鏍规嵁灞炴�鍚嶇О鑾峰緱鏁版嵁搴撳瓧娈靛悕绉�	 * 
	 * @param propertyName
	 * @return
	 */
	protected String[] getColumnNamesByPropertyName(String propertyName) {
		return HibernateUtils.getColumnNamesByPropertyName(entityClass, propertyName,
				sessionFactory);
	}

	/**
	 * 鏍规嵁灞炴�鍚嶇О鑾峰緱鏁版嵁搴撳瓧娈靛悕绉�	 * 
	 * @param entityClass
	 * @param propertyName
	 * @return
	 */
	protected String[] getColumnNamesByPropertyName(Class<?> entityClass, String propertyName) {
		return HibernateUtils.getColumnNamesByPropertyName(entityClass, propertyName,
				sessionFactory);
	}

	/**
	 * 鑾峰緱涓婚敭瀛楁
	 * 
	 * @param entityClass
	 * @return
	 */
	protected String[] getPrimaryKeyColumns(Class<?> entityClass) {
		return HibernateUtils.getPrimaryKeyColumns(entityClass, sessionFactory);
	}

	/**
	 * 鑾峰緱涓婚敭瀛楁
	 * 
	 * @param entityClass
	 * @return
	 */
	protected String[] getPrimaryKeyColumns() {
		return HibernateUtils.getPrimaryKeyColumns(entityClass, sessionFactory);
	}

	/**
	 * 鑾峰緱琛ㄥ悕
	 * 
	 * @param entityClass
	 * @return
	 */
	protected String getTableName(Class<?> entityClass) {
		return HibernateUtils.getTableName(entityClass, sessionFactory);
	}

	/**
	 * 鑾峰緱琛ㄥ悕
	 * 
	 * @return
	 */
	protected String getTableName() {
		return HibernateUtils.getTableName(entityClass, sessionFactory);
	}
	
}
