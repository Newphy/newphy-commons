package cn.newphy.data.entitydao.mybatis;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import cn.newphy.data.domain.Order;
import cn.newphy.data.domain.Page;
import cn.newphy.data.domain.Pageable;
import cn.newphy.data.entitydao.EntityDao;
import cn.newphy.data.entitydao.EntityQuery;
import cn.newphy.data.entitydao.EntityUpdate;
import cn.newphy.data.entitydao.mybatis.plugins.paginator.PageConst;
import cn.newphy.data.exception.OptimisticLockException;

public class MybatisEntityDao<T> implements EntityDao<T> {
	private Logger logger = LoggerFactory.getLogger(MybatisEntityDao.class);
	
	private static final String PARAM_NAME_ENTITY = "__entity";
	private static final String PARAM_NAME_VERSION = "__version";
	private static final String PARAM_NAME_ORDERS = "__orders";
	
	private final SqlSessionTemplate sqlSessionTemplate;
	private final EConfiguration configuration;
	private final EntityMapping entityMapping;

	public MybatisEntityDao(EConfiguration configuration, SqlSessionFactory sqlSessionFactory, Class<T> entityClass) {
		this.configuration = configuration;
		this.sqlSessionTemplate = createSqlSessionTemplate(sqlSessionFactory);
		ResultMap resultMap = ConfigurationHelper
				.getDefaultResultMapsByType(sqlSessionFactory.getConfiguration(), entityClass);
		if (resultMap == null) {
			throw new IllegalStateException("找不到实体类[" + entityClass.getName() + "]的缺省ResultMap");
		}
		this.entityMapping = new EntityMapping(sqlSessionFactory.getConfiguration(), resultMap);
	}

	public MybatisEntityDao(EConfiguration configuration, SqlSessionFactory sqlSessionFactory, Class<T> entityClass, String id) {
		this.configuration = configuration;
		this.sqlSessionTemplate = createSqlSessionTemplate(sqlSessionFactory);
		ResultMap resultMap = ConfigurationHelper.getResultMapBySimpleId(sqlSessionFactory.getConfiguration(),
				entityClass, id);
		if (resultMap == null) {
			throw new IllegalStateException("找不到实体类[" + entityClass.getName() + "]id为[" + id + "]的ResultMap");
		}
		this.entityMapping = new EntityMapping(sqlSessionFactory.getConfiguration(), resultMap);
	}

	/**
	 * 创建SqlSessionTemplate
	 * 
	 * @param sqlSessionFactory
	 * @return
	 */
	private SqlSessionTemplate createSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		if (sqlSessionFactory == null) {
			throw new IllegalArgumentException("sqlSessionFactory为空");
		}
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	/**
	 * 获得实体映射
	 * @return
	 */
	EntityMapping getEntityMapping() {
		return this.entityMapping;
	}


	@Override
	public EntityQuery<T> createQuery() {
		return new MybatisEntityQuery<>(configuration, this);
	}
	

	@Override
	public EntityUpdate<T> createUpdate() {
		return new MybatisEntityUpdate<>(configuration, this);
	}

	/**
	 * 通过原始sql进行查询
	 * @param rawSql
	 * @param paramMap
	 * @return
	 */
	public List<T> selectList(String rawSql, Map<String, Object> paramMap) {
		String statementId = ConfigurationHelper.registerDynamicSelectSql(entityMapping.getSourceId(), sqlSessionTemplate.getConfiguration(), rawSql, entityMapping.getResultMap());
		logger.info("select sql: " + rawSql); 
		return sqlSessionTemplate.selectList(statementId, paramMap);
	}
	
	/**
	 * 查询对象
	 * @param rawSql
	 * @return
	 */
	public <K> K selectObject(String rawSql, Map<String, Object> paramMap) {
		String statementId = ConfigurationHelper.registerDynamicSelectSql(entityMapping.getSourceId(), sqlSessionTemplate.getConfiguration(), rawSql, entityMapping.getResultMap());
		logger.info("select sql: " + rawSql); 
		return sqlSessionTemplate.selectOne(statementId, paramMap);
	}
	
	
	/**
	 * 更新对象
	 * @param rawSql
	 * @param paramMap
	 * @return
	 */
	public int update(String rawSql, Map<String, Object> paramMap) {
		String statementId = ConfigurationHelper.registerDynamicUpdateSql(entityMapping.getSourceId(), sqlSessionTemplate.getConfiguration(), rawSql);
		logger.info("update sql: " + rawSql); 
		return sqlSessionTemplate.update(statementId, paramMap);		
	}
	

	@Override
	public int save(T entity) {
		return sqlSessionTemplate.insert(getStatementId("save"), entity);
	}

	@Override
	public int batchSave(Collection<T> entities) {
		return sqlSessionTemplate.insert(getStatementId("batchSave"), entities);
	}

	@Override
	public int batchSave(T[] entities) {
		return sqlSessionTemplate.insert(getStatementId("batchSave"), Arrays.asList(entities));
	}

	@Override
	public int update(T entity) {
		return sqlSessionTemplate.update(getStatementId("update"), entity);
	}
	

	@Override
	public int updateOptimistic(T entity) throws OptimisticLockException {
		return 0;
	}

	@Override
	public int batchUpdate(Collection<T> entities) {
		return sqlSessionTemplate.update(getStatementId("batchUpdate"), entities);
	}

	@Override
	public int batchUpdate(T[] entities) {
		return batchUpdate(Arrays.asList(entities));
	}

	@Override
	public int delete(T entity) { 
		return sqlSessionTemplate.delete(getStatementId("delete"), entity);
	}

	@Override
	public int deleteAll() {
		return sqlSessionTemplate.delete(getStatementId("deleteAll"));
	}

	@Override
	public int batchDelete(Collection<T> entities) {
		return sqlSessionTemplate.delete(getStatementId("batchDelete"), entities);
	}

	@Override
	public int batchDelete(T[] entities) {
		return batchDelete(Arrays.asList(entities));
	}

	@Override
	public int deleteById(Serializable id) {
		return sqlSessionTemplate.delete(getStatementId("deleteById"), id);
	}

	@Override
	public T get(Serializable id) {
		return sqlSessionTemplate.selectOne(getStatementId("get"), id);
	}

	@Override
	public List<T> getAll(Order... orders) {
		Map<String, Object> param = concreteParamMap(null, null, null, orders);
		return sqlSessionTemplate.selectList(getStatementId("getAll"), param);
	}

	@Override
	public List<T> getByTemplate(T template, Order... orders) {
		Map<String, Object> param = concreteParamMap(template, null, null, orders);
		return sqlSessionTemplate.selectList(getStatementId("getByTemplate"), param);
	}

	@Override
	public T getOneByTemplate(T template) {
		Map<String, Object> param = concreteParamMap(template, null, null, null);
		return sqlSessionTemplate.selectOne(getStatementId("getOneByTemplate"), param);
	}

	@Override
	public List<T> getBy(String propName, Object value, Order... orders) {
		Map<String, Object> param = concreteParamMap(null, new String[]{propName}, new Object[]{value}, orders);
		return sqlSessionTemplate.selectList(getStatementId("getBy"), param);
	}

	@Override
	public List<T> getBy(String[] propNames, Object[] values, Order... orders) {
		Map<String, Object> param = concreteParamMap(null, propNames, values, orders);
		return sqlSessionTemplate.selectList(getStatementId("getBy"), param);
	}

	@Override
	public T getOneBy(String propName, Object value) {
		Map<String, Object> param = concreteParamMap(null, new String[]{propName}, new Object[]{value}, null);
		return sqlSessionTemplate.selectOne(getStatementId("getOneBy"), param);
	}

	@Override
	public T getOneBy(String[] propNames, Object[] values) {
		Map<String, Object> param = concreteParamMap(null, propNames, values, null);
		return sqlSessionTemplate.selectOne(getStatementId("getOneBy"), param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> getPage(Pageable pageable) {
		return (Page<T>)sqlSessionTemplate.selectList(getStatementId("getAll"), pageable);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> getPageByTemplate(Pageable pageable, T template) {
		Map<String, Object> param = concreteParamMap(template, null, null, null);
		param.put(PageConst.PARAM_NAME_PAGE, pageable);
		return (Page<T>)sqlSessionTemplate.selectList(getStatementId("getByTemplate"), param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> getPageBy(Pageable pageable, String[] propNames, Object[] values) {
		Map<String, Object> param = concreteParamMap(null, propNames, values, null);
		param.put(PageConst.PARAM_NAME_PAGE, pageable);
		return (Page<T>)sqlSessionTemplate.selectList(getStatementId("getByTemplate"), param);
	}

	@Override
	public int count() {
		return 0;
	}

	@Override
	public int count(String[] propNames, Object[] values) {
		return 0;
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException();
	}
	
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> concreteParamMap(T template, String[] propNames, Object[] propValues, Order[] orders) {
		Map<String, Object> param = new HashMap<>();
		T entity = null;
		if(template != null) {
			entity = template;
		}
		// 设置属性值
		if(propNames != null || propValues != null) {
			if(propNames == null || propValues == null || propNames.length != propValues.length) {
				throw new IllegalStateException("参数名称和参数值传值不正确");
			}
			if(entity == null) {
				Class<?> clazz = entityMapping.getEntityType();
				try {
					entity = (T)clazz.newInstance();
				} catch (Exception e) {
					logger.error("构造[" + clazz.getName() + "]对象出错", e);
					throw new IllegalStateException("构造[" + clazz.getName() + "]对象出错,请确保是否有缺省构造器");
				}
			}
			for(int i = 0; i < propNames.length; i++) {
				if(entityMapping.getResultMappingByProperty(propNames[i]) == null) {
					throw new IllegalStateException("ResultMap[" + entityMapping.getSourceId() + "]找不到属性[" + propNames[i] + "]");
				}
				try {
					PropertyUtils.setProperty(entity, propNames[i], propValues[i]);
				} catch (Exception e) {
					throw new IllegalStateException("设置对象属性[" + propNames[i] + "]值出错", e);
				}
			}
		}
		if(template != null) {
			param.put(PARAM_NAME_ENTITY, template);
		}
		
		// 设置排序
		if(orders != null) {
			initOrders(orders);
			param.put(PARAM_NAME_ORDERS, orders);
		}
		return param;
	}
	
	
	private void initOrders(Order[] orders) {
		if(orders != null) {
			for(int i = 0; i < orders.length; i++) {
				String propertyName = orders[i].getProperty();
				if(StringUtils.hasText(propertyName)) {
					ResultMapping resultMapping = checkAndGetResultMapping(propertyName);
					orders[i].setColumn(resultMapping.getColumn());
				}
			}
		}
	}
	
	private ResultMapping checkAndGetResultMapping(String propertyName) {
		ResultMapping resultMapping = entityMapping.getResultMappingByProperty(propertyName);
		if(resultMapping == null) {
			throw new IllegalStateException("ResultMap[" + entityMapping.getResultMap().getId() + "]找不到属性[" + propertyName + "]");
		}
		return resultMapping;
	}

	private String getStatementId(String methodName) {
		String statementId = entityMapping.getNamespace() + "." + methodName;
		return statementId;
	}

}
