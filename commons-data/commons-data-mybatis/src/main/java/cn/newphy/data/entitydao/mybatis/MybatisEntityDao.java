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
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.newphy.data.domain.Order;
import cn.newphy.data.domain.Page;
import cn.newphy.data.domain.Pageable;
import cn.newphy.data.entitydao.EntityDao;
import cn.newphy.data.entitydao.EntityQuery;
import cn.newphy.data.entitydao.EntityUpdate;
import cn.newphy.data.exception.OptimisticLockException;

public class MybatisEntityDao<T> implements EntityDao<T> {
	private Logger logger = LoggerFactory.getLogger(MybatisEntityDao.class);
	
	private final SqlSessionTemplate sqlSessionTemplate;
	private final GlobalConfig globalConfig;
	private final EntityMapping entityMapping;

	public MybatisEntityDao(GlobalConfig globalConfig, SqlSessionFactory sqlSessionFactory, Class<T> entityClass) {
		this.globalConfig = globalConfig;
		this.sqlSessionTemplate = createSqlSessionTemplate(sqlSessionFactory);
		ResultMap resultMap = ConfigurationHelper
				.getDefaultResultMapsByType(sqlSessionFactory.getConfiguration(), entityClass);
		if (resultMap == null) {
			throw new IllegalStateException("找不到实体类[" + entityClass.getName() + "]的缺省ResultMap");
		}
		this.entityMapping = new EntityMapping(sqlSessionFactory.getConfiguration(), globalConfig, resultMap);
	}

	public MybatisEntityDao(GlobalConfig globalConfig, SqlSessionFactory sqlSessionFactory, Class<T> entityClass, String id) {
		this.globalConfig = globalConfig;
		this.sqlSessionTemplate = createSqlSessionTemplate(sqlSessionFactory);
		ResultMap resultMap = ConfigurationHelper.getResultMapBySimpleId(sqlSessionFactory.getConfiguration(),
				entityClass, id);
		if (resultMap == null) {
			throw new IllegalStateException("找不到实体类[" + entityClass.getName() + "]id为[" + id + "]的ResultMap");
		}
		this.entityMapping = new EntityMapping(sqlSessionFactory.getConfiguration(), globalConfig, resultMap);
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
		return new MybatisEntityQuery<>(globalConfig, this);
	}
	

	@Override
	public EntityUpdate<T> createUpdate() {
		return new MybatisEntityUpdate<>(globalConfig, this);
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
		if(entity == null) {
			return 0;
		}
		initId(entity);
		return sqlSessionTemplate.insert(getStatementId("save"), entity);
	}

	@Override
	public int batchSave(Collection<T> entities) {
		if(entities == null || entities.size() == 0) {
			return 0;
		}
		for (T entity : entities) {
			initId(entity);
		}
		return sqlSessionTemplate.insert(getStatementId("batchSave"), entities);
	}

	@Override
	public int batchSave(T[] entities) {
		if(entities == null) {
			return 0;
		}
		return sqlSessionTemplate.insert(getStatementId("batchSave"), Arrays.asList(entities));
	}

	@Override
	public int update(T entity) {
		if(entity == null) {
			return 0;
		}
		return sqlSessionTemplate.update(getStatementId("update"), entity);
	}
	

	@Override
	public int updateOptimistic(T entity) throws OptimisticLockException {
		if(entity == null) {
			return 0;
		}
		if(entityMapping.getVersionMapping() == null) {
			throw new IllegalStateException("没有设置版本字段");
		}
		int c = sqlSessionTemplate.update(getStatementId("updateOptimistic"), entity);
		if(c == 0) {
			throw new OptimisticLockException();
		}
		return c;
	}

	@Override
	public int batchUpdate(Collection<T> entities) {
		if(entities == null || entities.size() == 0) {
			return 0;
		}
		return sqlSessionTemplate.update(getStatementId("batchUpdate"), entities);
	}

	@Override
	public int batchUpdate(T[] entities) {
		if(entities == null) {
			return 0;
		}
		return batchUpdate(Arrays.asList(entities));
	}

	@Override
	public int delete(T entity) { 
		if(entity == null) {
			return 0;
		}
		return sqlSessionTemplate.delete(getStatementId("delete"), entity);
	}

	@Override
	public int deleteAll() {
		return sqlSessionTemplate.delete(getStatementId("deleteAll"));
	}

	@Override
	public int batchDelete(Collection<T> entities) {
		if(entities == null || entities.size() == 0) {
			return 0;
		}
		return sqlSessionTemplate.delete(getStatementId("batchDelete"), entities);
	}

	@Override
	public int batchDelete(T[] entities) {
		if(entities == null) {
			return 0;
		}
		return batchDelete(Arrays.asList(entities));
	}

	@Override
	public int deleteById(Serializable id) {
		return sqlSessionTemplate.delete(getStatementId("deleteById"), id);
	}

	@Override
	public T get(Serializable id) {
		if(id == null) {
			return null;
		}
		return sqlSessionTemplate.selectOne(getStatementId("get"), id);
	}

	@Override
	public List<T> getAll(Order... orders) {
		Map<String, Object> param = concreteParamMap(null, null, null, orders, null);
		return sqlSessionTemplate.selectList(getStatementId("getAll"), param);
	}

	@Override
	public List<T> getBy(String propName, Object value, Order... orders) {
		Assert.isTrue(propName != null && propName.length() > 0 , "参数名称不能为空");
		return getBy(new String[]{propName}, new Object[]{value}, orders);
	}

	@Override
	public List<T> getBy(String[] propNames, Object[] values, Order... orders) {
		Map<String, Object> param = concreteParamMap(null, propNames, values, orders, null);
		return sqlSessionTemplate.selectList(getStatementId("getBy"), param);
	}
	
	
	

	@Override
	public List<T> getByTemplate(T template, Order... orders) {
		Assert.notNull(template, "模板对象不能为空");
		Map<String, Object> param = concreteParamMap(template, null, null, orders, null);
		return sqlSessionTemplate.selectList(getStatementId("getBy"), param);
	}

	@Override
	public T getOneBy(String propName, Object value) {
		Assert.isTrue(propName != null && propName.length() > 0 , "参数名称不能为空");
		return getOneBy(new String[]{propName}, new Object[]{value});
	}

	@Override
	public T getOneBy(String[] propNames, Object[] values) {
		Map<String, Object> param = concreteParamMap(null, propNames, values, null, null);
		return sqlSessionTemplate.selectOne(getStatementId("getOneBy"), param);
	}
	
	@Override
	public T getOneByTemplate(T template) {
		Assert.notNull(template, "模板对象不能为空");
		Map<String, Object> param = concreteParamMap(template, null, null, null, null);
		return sqlSessionTemplate.selectOne(getStatementId("getOneBy"), param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> getPage(Pageable pageable) {
		Assert.notNull(pageable, "分页参数不能为空");
		Map<String, Object> param = concreteParamMap(null, null, null, null, pageable);
		return (Page<T>)sqlSessionTemplate.selectList(getStatementId("getBy"), param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> getPageByTemplate(Pageable pageable, T template) {
		Assert.notNull(template, "模板对象不能为空");
		Map<String, Object> param = concreteParamMap(template, null, null, null, pageable);
		return (Page<T>)sqlSessionTemplate.selectList(getStatementId("getBy"), param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> getPageBy(Pageable pageable, String[] propNames, Object[] values) {
		Map<String, Object> param = concreteParamMap(null, propNames, values, null, pageable);
		return (Page<T>)sqlSessionTemplate.selectList(getStatementId("getBy"), param);
	}

	@Override
	public int count() {
		return (int)sqlSessionTemplate.selectOne(getStatementId("count"), new HashMap<String, Object>());
	}
	

	@Override
	public int countByTemplate(T template) {
		Assert.notNull(template, "模板对象不能为空");
		Map<String, Object> param = concreteParamMap(template, null, null, null, null);
		return (int)sqlSessionTemplate.selectOne(getStatementId("count"), param);
	}


	@Override
	public int count(String propName, Object value) {
		Assert.isTrue(propName != null && propName.length() > 0 , "参数名称不能为空");
		return count(new String[]{propName}, new Object[]{value});
	}

	@Override
	public int count(String[] propNames, Object[] values) {
		Map<String, Object> param = concreteParamMap(null, propNames, values, null, null);
		return (int)sqlSessionTemplate.selectOne(getStatementId("count"), param);
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException();
	}
	
	
	private Map<String, Object> concreteParamMap(T template, String[] propNames, Object[] propValues, Order[] orders, Pageable pageable) {
		Map<String, Object> param = new HashMap<>();
		Configuration configuration = sqlSessionTemplate.getConfiguration();
		if(template != null) {
			MetaObject metaObject = configuration.newMetaObject(template);
			for (String propertyName : metaObject.getGetterNames()) {
				param.put(propertyName, metaObject.getValue(propertyName));
			}
		}

		// 设置属性值
		if(propNames != null || propValues != null) {
			if(propNames == null || propValues == null || propNames.length != propValues.length) {
				throw new IllegalStateException("参数名称和参数值传值不正确");
			}
			for(int i = 0; i < propNames.length; i++) {
				if(propNames[i] != null) {
					param.put(propNames[i], propValues[i]);
				}
			}
		}
		
		// 设置排序
		if(orders != null) {
			initOrders(orders);
			param.put(ParamConst.PARAM_NAME_ORDERS, orders);
		}
		// 设置分页
		if(pageable != null) {
			if(pageable.getSort() != null) {
				initOrders(pageable.getSort().getOrders().toArray(new Order[0]));
			}
			param.put(ParamConst.PARAM_NAME_PAGE, pageable);
		}
		return param;
	}
	
	
	private void initId(T entity) {
		if(entity == null) {
			return;
		}
		IdGenerator idGenerator = entityMapping.getIdGenerator();
		if(idGenerator != null) {
			Serializable id = idGenerator.generate(globalConfig, entity);
			String idName = entityMapping.getIdMapping().getProperty();
			try {
				PropertyUtils.setProperty(entity, idName, id);
			} catch (Exception e) {
				throw new IllegalStateException("设置主键出错", e);
			}
		}
	}
	
	private void initOrders(Order[] orders) {
		if(orders != null) {
			for(int i = 0; i < orders.length; i++) {
				if(orders[i] != null) {
					String propertyName = orders[i].getProperty();
					if(StringUtils.hasText(propertyName)) {
						ResultMapping resultMapping = checkAndGetResultMapping(propertyName);
						orders[i].setColumn(resultMapping.getColumn());
					}
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
