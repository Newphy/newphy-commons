package cn.newphy.data.mybatis.impl;



import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

import cn.newphy.commons.lang.ReflectionUtils;
import cn.newphy.data.Page;
import cn.newphy.data.mybatis.EntityMapper;
import cn.newphy.data.mybatis.EntityQuery;
import cn.newphy.data.mybatis.Table;
import cn.newphy.data.mybatis.util.CamelCaseUtils;

public class MybatisEntityMapper<T> implements EntityMapper<T> {
	
	private final String tableName;
	private final Class<T> entityClass;
	private final ResultMap resultMap;
	private List<ResultMapping> resultMappings;
	
	
	public MybatisEntityMapper(Configuration configuration, String resultMapId) {
		this.entityClass = ReflectionUtils.getSuperClassGenericType(getClass());
		this.resultMap = configuration.getResultMap(resultMapId);
		this.resultMappings = filterMappingProperty(entityClass, resultMap);
		this.tableName = resolveTableName(entityClass);
	}
	
	
	private List<ResultMapping> filterMappingProperty(Class<?> entityClass, ResultMap resultMap) {
		Set<String> propertyNames = new HashSet<String>();
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(entityClass);
		for (PropertyDescriptor pd : pds) {
			propertyNames.add(pd.getName());
		}
		List<ResultMapping> resultMappings = resultMap.getPropertyResultMappings();
		List<ResultMapping> result = new ArrayList<ResultMapping>();
		for (ResultMapping resultMapping : resultMappings) {
			if(propertyNames.contains(resultMapping.getProperty())) {
				result.add(resultMapping);
			}
		}
		return result;
	}
	
	private String resolveTableName(Class<?> entityClass) {
		String tableName = null;
		if(entityClass.isAnnotationPresent(Table.class))	{
			Table table = entityClass.getAnnotation(Table.class);
			tableName = table.name();
			return tableName;
		}
		
		Class<? extends Annotation> tableClass;
		try {
			tableClass = (Class<? extends Annotation>)Class.forName("javax.persistence.Table");
			if(entityClass.isAnnotationPresent(tableClass)) {
				Object tableAnnotation = entityClass.getAnnotation(tableClass);
				Method method = tableClass.getMethod("name");
				tableName = (String)org.springframework.util.ReflectionUtils.invokeMethod(method, tableAnnotation);
				return tableName;
			}
		} catch (Exception e) {
		}
		
		if(tableName == null) {
			String className = entityClass.getSimpleName();
			tableName = CamelCaseUtils.camelCase2Underline(className);			
		}
		return tableName;
	}

	@Override
	public EntityQuery<T> query() {
		return null;
	}

	@Override
	public void save(T entity) {
		
	}

	@Override
	public void batchSave(Collection<T> entities) {
		
	}

	@Override
	public void batchSave(T[] entities) {
		
	}

	@Override
	public void update(T entity) {
		
	}

	@Override
	public void batchUpdate(Collection<T> entities) {
		
	}

	@Override
	public void batchUpdate(T[] entities) {
		
	}

	@Override
	public void delete(T entity) {
		
	}

	@Override
	public void deleteAll() {
		
	}

	@Override
	public void batchDelete(Collection<T> entities) {
		
	}

	@Override
	public void batchDelete(T[] entities) {
		
	}

	@Override
	public void deleteById(Serializable id) {
		
	}

	@Override
	public T get(Serializable id) {
		return null;
	}

	@Override
	public List<T> getAll(String... orders) {
		return null;
	}

	@Override
	public List<T> getBy(String propName, Object value, String... orders) {
		return null;
	}

	@Override
	public List<T> getBy(String[] propNames, Object[] values, String... orders) {
		return null;
	}

	@Override
	public T getAnyBy(String propName, Object value) {
		return null;
	}

	@Override
	public T getAnyBy(String[] propNames, Object[] values) {
		return null;
	}

	@Override
	public Page<T> getPage(Page<T> page, String... orders) {
		return null;
	}

	@Override
	public Page<T> getPageBy(Page<T> page, String[] propNames, Object[] values, String... orders) {
		return null;
	}

	@Override
	public int count() {
		return 0;
	}

	@Override
	public int count(String[] propNames, Object[] values) {
		return 0;
	}

}
