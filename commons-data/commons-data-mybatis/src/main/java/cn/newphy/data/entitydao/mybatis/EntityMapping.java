package cn.newphy.data.entitydao.mybatis;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import cn.newphy.data.entitydao.mybatis.util.CamelCaseUtils;
import cn.newphy.data.entitydao.mybatis.util.FreemarkUtils;

public class EntityMapping {
	private Logger logger = LoggerFactory.getLogger(EntityMapping.class);
	
	// Mybatis配置类
	private final Configuration configuration;
	// 源ResultMap
	private final ResultMap sourceResultMap;
	// 表名
	private String tableName;
	// resultMap
	private ResultMap resultMap;
	// 字段名map
	private Map<String, ResultMapping> columnMap = new HashMap<>();
	// 属性名map
	private Map<String, ResultMapping> propertyMap = new HashMap<>();


	public EntityMapping(Configuration configuration, ResultMap resultMap) {
		this.configuration = configuration;
		this.sourceResultMap = resultMap;
		initialize();
	}
	
	private void initialize() {
		Class<?> entityClass = sourceResultMap.getType();
		this.tableName = getTableNameByType(entityClass);
		// 注册新的ResultMap
		registerEntityDaoResultMap();
		for (ResultMapping resultMapping : resultMap.getResultMappings()) {
			columnMap.put(resultMapping.getColumn(), resultMapping);
			propertyMap.put(resultMapping.getProperty(), resultMapping);
		}
		// build mapper文件
		buildXmlMapper();
	}
	
	/**
	 * 注册EntityDao为命名空间的ResultMap
	 * 
	 * @param sid
	 * @param resultMap
	 * @return
	 */
	private void registerEntityDaoResultMap() {
		String id = getNamespace() + ".ResultMap";
		logger.debug("注册ResultMap[" + id + "]");
		this.resultMap = new ResultMap.Builder(configuration, id, sourceResultMap.getType(),
				sourceResultMap.getResultMappings(), sourceResultMap.getAutoMapping()).build();
		configuration.addResultMap(resultMap);
	}
	

	/**
	 * build ResultMap相关的Mapper操作
	 */
	private void buildXmlMapper() {
		String xml = FreemarkUtils.parse("template_mysql", this);
		String resource = "/entitydao/mapping/" + sourceResultMap.getId() + "Mapping.xml";
		XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(new ByteArrayInputStream(xml.getBytes()),
				configuration, resource, configuration.getSqlFragments());
		xmlMapperBuilder.parse();
	}

	/**
	 * 获得名字空间
	 * @return
	 */
	public String getNamespace() {
		return "EntityDao[" + getSourceId() + "]";
	}
	
	/**
	 * 获得映射列表
	 * 
	 * @return
	 */
	public List<ResultMapping> getResultMappings() {
		return resultMap.getResultMappings();
	}
	
	
	
	
	/**
	 * 根据字段名获得ResultMapping
	 * @param column
	 * @return
	 */
	public ResultMapping getResultMappingByColumn(String column) {
		return columnMap.get(column);
	}
	
	/**
	 * 根据属性名获得ResultMapping
	 * @param property
	 * @return
	 */
	public ResultMapping getResultMappingByProperty(String property) {
		return propertyMap.get(property);
	}
	

	/**
	 * 获得Id映射字段
	 * 
	 * @return
	 */
	public ResultMapping getIdMapping() {
		List<ResultMapping> idMappings = resultMap.getIdResultMappings();
		return idMappings.get(0);
	}

	/**
	 * 获得编号
	 * 
	 * @return
	 */
	public String getSourceId() {
		return sourceResultMap.getId();
	}
	
	/**
	 * 获得短编号
	 * @return
	 */
	public String getSourceShortId() {
		String id = getSourceId();
		return id.indexOf('.') > 0 ? id.substring(id.lastIndexOf('.')+1) : id;
	}

	/**
	 * 获得实体类型
	 * 
	 * @return
	 */
	public Class<?> getEntityType() {
		return resultMap.getType();
	}
	

	@SuppressWarnings("unchecked")
	private String getTableNameByType(Class<?> entityClass) {
		String tableName = null;
		if (entityClass.isAnnotationPresent(Table.class)) {
			Table table = entityClass.getAnnotation(Table.class);
			tableName = table.name();
			return tableName;
		}

		Class<? extends Annotation> tableClass;
		try {
			tableClass = (Class<? extends Annotation>) Class.forName("javax.persistence.Table");
			if (entityClass.isAnnotationPresent(tableClass)) {
				Object tableAnnotation = entityClass.getAnnotation(tableClass);
				Method method = tableClass.getMethod("name");
				tableName = (String) ReflectionUtils.invokeMethod(method, tableAnnotation);
				return tableName;
			}
		} catch (Exception e) {
		}

		if (tableName == null) {
			String className = entityClass.getSimpleName();
			tableName = CamelCaseUtils.camelCase2Underline(className);
		}
		return tableName;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the resultMap
	 */
	public ResultMap getResultMap() {
		return resultMap;
	}

	/**
	 * @param resultMap
	 *            the resultMap to set
	 */
	public void setResultMap(ResultMap resultMap) {
		this.resultMap = resultMap;
	}

}
