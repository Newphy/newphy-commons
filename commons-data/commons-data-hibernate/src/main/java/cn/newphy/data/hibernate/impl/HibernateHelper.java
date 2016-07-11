package cn.newphy.data.hibernate.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import cn.newphy.data.Page;

/**
 * Created by Administrator on 14-3-11.
 */
public class HibernateHelper {

	private HibernateTemplate hibernateTemplate;

	public HibernateHelper() {
		super();
	}

	public HibernateHelper(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	/**
	 * 淇濆瓨瀹炰綋瀵硅薄
	 * 
	 * @param entity
	 */
	public void save(Object entity) {
		getHibernateTemplate().save(entity);
	}

	/**
	 * 鏇存柊瀹炰綋瀵硅薄
	 * 
	 * @param entity
	 */
	public void update(Object entity) {
		getHibernateTemplate().update(entity);
	}

	/**
	 * 鍒犻櫎瀹炰綋瀵硅薄
	 * 
	 * @param entity
	 */
	public void delete(Object entity) {
		getHibernateTemplate().delete(entity);
	}

	/**
	 * 鏍规嵁ID鍒犻櫎瀹炰綋瀵硅薄
	 * 
	 * @param entityClass
	 * @param id
	 */
	public <T> void deleteById(Class<T> entityClass, Serializable id) {
		Object entity = get(entityClass, id);
		delete(entity);
	}

	/**
	 * 鍒犻櫎瀹炰綋瀵硅薄
	 * 
	 * @param entityClass
	 */
	public void deleteAll(Class<?> entityClass) {
		String hql = HibernateHQL.getDeleteHQL(entityClass, null);
		bulkUpdate(hql);
	}

	/**
	 * 鎵归噺淇濆瓨瀹炰綋瀵硅薄
	 * 
	 * @param entities
	 */
	public void batchSave(Collection<?> entities) {
		if (entities != null) {
			for (Object entity : entities) {
				save(entity);
			}
		}
	}

	/**
	 * 鎵归噺淇濆瓨瀹炰綋瀵硅薄
	 * 
	 * @param entities
	 */
	public void batchSave(Object[] entities) {
		if (entities != null) {
			for (Object entity : entities) {
				save(entity);
			}
		}
	}

	/**
	 * 鎵归噺鏇存柊瀹炰綋瀵硅薄
	 * 
	 * @param entities
	 */
	public void batchUpdate(Collection<?> entities) {
		if (entities != null) {
			for (Object entity : entities) {
				update(entity);
			}
		}
	}

	/**
	 * 鎵归噺鏇存柊瀹炰綋瀵硅薄
	 * 
	 * @param entities
	 */
	public void batchUpdate(Object[] entities) {

		if (entities != null) {
			for (Object entity : entities) {
				update(entity);
			}
		}
	}

	/**
	 * 鎵归噺鍒犻櫎瀹炰綋
	 * 
	 * @param entities
	 */
	public void batchDelete(Collection<?> entities) {
		getHibernateTemplate().deleteAll(entities);
	}

	/**
	 * 鎵归噺鍒犻櫎瀹炰綋
	 * 
	 * @param entities
	 */
	public void batchDelete(Object[] entities) {
		if (entities != null) {
			getHibernateTemplate().deleteAll(Arrays.asList(entities));
		}
	}

	/**
	 * update 鎿嶄綔
	 * 
	 * @param hql
	 * @param values
	 * @return 褰卞搷鐨勮褰曟暟
	 */
	public int bulkUpdate(final CharSequence hql, final Object... values) {
		return getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.createQuery(HibernateHQL.getNamedParamHQL(hql));
				prepareQuery(queryObject);
				String[] paramNames = HqlUtils.getNamedParamArray(values);
				applyNamedParametersToQuery(queryObject, paramNames, values);
				return queryObject.executeUpdate();
			}
		});
	}

	/**
	 * 鑾峰緱瀹炰綋瀵硅薄
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public <T> T get(Class<T> entityClass, Serializable id) {
		return (T) getHibernateTemplate().get(entityClass, id);
	}

	/**
	 * 鑾峰緱鎵�湁瀹炰綋锛屽苟鎺掑簭
	 * 
	 * @param entityClass
	 * @param orders
	 *            鎺掑簭灞炴�锛屽锛氣�userName asc"
	 * @return
	 */
	public <T> List<T> getAll(Class<T> entityClass, String... orders) {
		return find(HibernateHQL.getQueryHQL(entityClass, null, orders));
	}

	/**
	 * 鏍规嵁灞炴�鑾峰緱瀹炰綋鍒楄〃
	 * 
	 * @param entityClass
	 * @param propNames
	 * @param values
	 * @param orders
	 * @return
	 */
	public <T> List<T> getBy(Class<T> entityClass, String[] propNames, Object[] values, String... orders) {
		String hql = HibernateHQL.getQueryHQL(entityClass, propNames, orders);
		return find(hql, values);
	}

	/**
	 * 鏍规嵁灞炴�鑾峰緱瀹炰綋
	 * 
	 * @param entityClass
	 * @param propertyNames
	 * @param values
	 * @param orders
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAnyBy(Class<T> entityClass, String[] propertyNames, Object[] values, String... orders) {
		String hql = HibernateHQL.getQueryHQL(entityClass, propertyNames, orders);
		return (T) findAny(hql, values);
	}

	/**
	 * 鑾峰緱鍒嗛〉鏁版嵁
	 * 
	 * @param entityClass
	 * @param page
	 *            鍒嗛〉鍙傛暟
	 * @param orders
	 *            鎺掑簭灞炴�锛屽锛氣�userName asc"
	 * @return
	 */
	public <T> Page<T> getPage(Class<T> entityClass, Page<T> page, String... orders) {
		String hql = HibernateHQL.getQueryHQL(entityClass, null, orders);
		return findPage(hql, page);
	}

	/**
	 * 鏍规嵁鍚嶅瓧缁戝畾鏌ヨ鍒楄〃
	 * 
	 * @param hql
	 * @param paramNames
	 * @param values
	 * @return
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> findNP(final CharSequence hql, final String[] paramNames, final Object[] values) throws DataAccessException {
		return (List<T>) getHibernateTemplate().findByNamedParam(hql.toString(), paramNames, values);
	}

	/**
	 * 鏍规嵁鍚嶅瓧缁戝畾鏌ヨ鍒楄〃
	 * 
	 * @param hql
	 * @param paramMap
	 * @return
	 * @throws DataAccessException
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> findNP(final CharSequence hql, Map<String, Object> paramMap) throws DataAccessException {
		if (paramMap == null) {
			paramMap = new HashMap<String, Object>();
		}
		String[]	 names = new String[paramMap.size()];
		Object[] values = new Object[paramMap.size()];
		int i = 0;
		for (String name : paramMap.keySet()) {
			names[i] = name;
			values[i] = paramMap.get(name);
			i++;
		}
		return (List<T>) getHibernateTemplate().findByNamedParam(hql.toString(), names, values);
	}

	/**
	 * 鏍规嵁缁戝畾鍙橀噺鏌ヨ鍒楄〃
	 * 
	 * @param hql
	 * @param values
	 * @return
	 * @throws DataAccessException
	 */
	public <T> List<T> find(final CharSequence hql, final Object... values) throws DataAccessException {
		String namedParamHQL = HibernateHQL.getNamedParamHQL(hql);
		return findNP(namedParamHQL, HqlUtils.getNamedParamArray(values), values);
	}

	/**
	 * 鏍规嵁灞炴�鑾峰緱瀹炰綋鍒嗛〉鍒楄〃
	 * 
	 * @param entityClass
	 * @param propNames
	 * @param values
	 * @param orders
	 * @return
	 */
	public <T> Page<T> getPageBy(Class<T> entityClass, Page<T> page, String[] propNames, Object[] values, String... orders) {
		String hql = HibernateHQL.getQueryHQL(entityClass, propNames, orders);
		return findPage(hql, page, values);
	}

	/**
	 * 鏍规嵁灞炴�鑾峰緱瀹炰綋鏁伴噺
	 * 
	 * @param entityClass
	 * @param propNames
	 * @param values
	 * @return
	 */
	public int count(Class<?> entityClass, String[] propNames, Object[] values) {
		String hql = HibernateHQL.getCountHQL(entityClass, propNames);
		return (Integer)findAny(hql, values);
	}

	/**
	 * 鏌ユ壘鍞竴瀹炰綋
	 * <p>
	 * 濡傛灉缁撴灉闆嗕负绌�杩斿洖null;濡傛灉缁撴灉闆嗗ぇ浜庝竴涓�鎶汭ncorrectResultSizeDataAccessException寮傚父
	 * </p>
	 * 
	 * @param hql
	 * @param values
	 * @return
	 * @throws IncorrectResultSizeDataAccessException
	 */
	@SuppressWarnings("unchecked")
	public <T> T findUnique(CharSequence hql, Object... values) throws IncorrectResultSizeDataAccessException {
		return (T) DataAccessUtils.uniqueResult(find(hql, values));
	}

	/**
	 * 鏌ユ壘鍞竴瀹炰綋
	 * <p>
	 * 濡傛灉缁撴灉闆嗕负绌�杩斿洖null;濡傛灉缁撴灉闆嗗ぇ浜庝竴涓�鎶汭ncorrectResultSizeDataAccessException寮傚父
	 * </p>
	 * 
	 * @param hql
	 * @param paramMap
	 * @return
	 * @throws IncorrectResultSizeDataAccessException
	 */
	@SuppressWarnings("unchecked")
	public <T> T findNPUnique(CharSequence hql, Map<String, Object> paramMap) throws IncorrectResultSizeDataAccessException {
		return (T) DataAccessUtils.uniqueResult(findNP(hql, paramMap));
	}

	/**
	 * 鏌ユ壘鍞竴瀹炰綋
	 * <p>
	 * 濡傛灉缁撴灉闆嗕负绌�杩斿洖null;濡傛灉缁撴灉闆嗗ぇ浜庝竴涓�鎶汭ncorrectResultSizeDataAccessException寮傚父
	 * </p>
	 * 
	 * @param hql
	 * @param paramNames
	 * @param values
	 * @return
	 * @throws IncorrectResultSizeDataAccessException
	 */
	@SuppressWarnings("unchecked")
	public <T> T findNPUnique(CharSequence hql, final String[] paramNames, final Object[] values) throws IncorrectResultSizeDataAccessException {
		return (T) DataAccessUtils.uniqueResult(findNP(hql, paramNames, values));
	}

	/**
	 * 鏌ユ壘闅忔剰涓�釜
	 * <p>
	 * 濡傛灉缁撴灉闆嗕负绌猴紝杩斿洖null锛涘鏋滅粨鏋滈泦澶т簬涓�釜锛岃繑鍥炵涓�釜
	 * </p>
	 * 
	 * @param hql
	 * @param values
	 * @return
	 */
	public <T> T findAny(CharSequence hql, Object... values) {
		List<T> list = find(hql, values);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	/**
	 * 鏌ユ壘闅忔剰涓�釜
	 * <p>
	 * 濡傛灉缁撴灉闆嗕负绌猴紝杩斿洖null锛涘鏋滅粨鏋滈泦澶т簬涓�釜锛岃繑鍥炵涓�釜
	 * </p>
	 * 
	 * @param hql
	 * @param paramMap
	 * @return
	 */
	public <T> T findNPAny(CharSequence hql, Map<String, Object> paramMap) {
		List<T> list = findNP(hql, paramMap);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	/**
	 * 鏌ユ壘闅忔剰涓�釜
	 * <p>
	 * 濡傛灉缁撴灉闆嗕负绌猴紝杩斿洖null锛涘鏋滅粨鏋滈泦澶т簬涓�釜锛岃繑鍥炵涓�釜
	 * </p>
	 * 
	 * @param hql
	 * @param paramNames
	 * @param values
	 * @return
	 */
	public <T> T findNPAny(CharSequence hql, final String[] paramNames, final Object[] values) {
		List<T> list = findNP(hql, paramNames, values);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	/**
	 * 鏌ヨ鍒嗛〉
	 * 
	 * @param hql
	 * @param page
	 * @param values
	 * @return
	 */
	public <T> Page<T> findPage(final CharSequence hql, final Page<T> page, final Object... values) {
		String namedParamHql = HibernateHQL.getNamedParamHQL(hql);
		String[] paramNames = HqlUtils.getNamedParamArray(values);
		return findNPPage(namedParamHql, page, paramNames, values);
	}

	/**
	 * 鏌ヨ鍒嗛〉
	 * 
	 * @param hql
	 * @param page
	 * @param paramMap
	 * @return
	 */
	public <T> Page<T> findNPPage(final CharSequence hql, final Page<T> page, Map<String, Object> paramMap) {
		if (paramMap == null) {
			paramMap = new HashMap<String, Object>();
		}
		String[]	 names = new String[paramMap.size()];
		Object[] values = new Object[paramMap.size()];
		int i = 0;
		for (String name : paramMap.keySet()) {
			names[i] = name;
			values[i] = paramMap.get(name);
			i++;
		}
		return findNPPage(hql, page, names, values);
	}

	/**
	 * 鏌ヨ鍒嗛〉
	 * 
	 * @param hql
	 * @param page
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> Page<T> findNPPage(final CharSequence hql, final Page<T> page, final String[] paramNames, final Object[] values) {
		return (Page<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback<Page<T>>() {
			@Override
			public Page<T> doInHibernate(Session session) throws HibernateException {
				if (page.isNeedTotal()) {
					// [1] total size
					String countHql = "select count(1) " + HqlUtils.removeSelect(HqlUtils.removeOrders(hql));
					Integer totalCount = findNPUnique(countHql, paramNames, values);
					if (totalCount == null) {
						return page;
					}
					page.setTotal(totalCount);
				}
				// [2] page elements
				Query query = session.createQuery(hql.toString());
				applyNamedParametersToQuery(query, paramNames, values);
				List<T> list = query.setFirstResult(page.getStartIndex()).setMaxResults(page.getPageSize()).list();
				page.setData(list);
				return page;
			}
		});
	}

	/**
	 * flush
	 */
	public void flush() {
		getHibernateTemplate().flush();
	}

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public SessionFactory getSessionFactory() {
		return hibernateTemplate.getSessionFactory();
	}

	private void applyNamedParametersToQuery(Query queryObject, String[] paramNames, Object[] values) throws HibernateException {
		for (int i = 0; i < values.length; i++) {
			Object value = values[i];
			String paramName = paramNames[i];
			if (value instanceof Collection) {
				queryObject.setParameterList(paramName, (Collection<?>) value);
			} else if (value instanceof Object[]) {
				queryObject.setParameterList(paramName, (Object[]) value);
			} else {
				queryObject.setParameter(paramName, value);
			}
		}
	}

	protected void prepareQuery(Query queryObject) {
		SessionFactoryUtils.applyTransactionTimeout(queryObject, getSessionFactory());
	}

}
