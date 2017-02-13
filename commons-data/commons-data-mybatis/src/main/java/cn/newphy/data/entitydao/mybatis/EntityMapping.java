package cn.newphy.data.entitydao.mybatis;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.newphy.data.entitydao.mybatis.util.FreemarkUtils;
import cn.newphy.data.entitydao.mybatis.util.ReflectionUtils;

public class EntityMapping {
	private Logger logger = LoggerFactory.getLogger(EntityMapping.class);
	
	// Mybatis配置类
	private final Configuration configuration;
	// 全部配置
	private final GlobalConfig globalConfig;
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
	// 更新字段
	private List<ResultMapping> updateMappings = new ArrayList<>();
	// 插入字段
	private List<ResultMapping> insertMappings = new ArrayList<>();
	
	private ResultMapping versionMapping = null;
	// 主键生成器
	private IdGenerator idGenerator;


	public EntityMapping(Configuration configuration, GlobalConfig globalConfig, ResultMap resultMap) {
		this.configuration = configuration;
		this.globalConfig = globalConfig;
		this.sourceResultMap = resultMap;
		initialize();
	}
	
	private void initialize() {
		initTableName();
		// 注册新的ResultMap
		registerEntityDaoResultMap();
		// 初始化ResultMappings
		initResultMappings();
		// 初始化Id生成器
		initIdGenerator();
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
	
	
	private void initResultMappings() {
		Class<?> entityClass = sourceResultMap.getType();
		for (ResultMapping resultMapping : resultMap.getResultMappings()) {
			columnMap.put(resultMapping.getColumn(), resultMapping);
			propertyMap.put(resultMapping.getProperty(), resultMapping);
			Column column = ReflectionUtils.getAnnotationFromProperty(entityClass, resultMapping.getProperty(), Column.class);
			if(column == null || column.insertable()) {
				insertMappings.add(resultMapping);
			}
			if(column == null || column.updatable()) {
				updateMappings.add(resultMapping);
			}
			Version version = ReflectionUtils.getAnnotationFromProperty(entityClass, resultMapping.getProperty(), Version.class);
			if(version != null) {
				versionMapping = resultMapping;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initIdGenerator() {
		Class<?> entityClass = sourceResultMap.getType();
		ResultMapping idMapping = getIdMapping();
		String idName = idMapping.getProperty();
		GeneratedValue generatedValue = ReflectionUtils.getAnnotationFromProperty(entityClass, idName, GeneratedValue.class);
		if(generatedValue == null) {
			return;
		}
		if(generatedValue.strategy() == GenerationType.AUTO) {
			String generator = generatedValue.generator();
			if(generator != null && generator.length() > 0) {
				try {
					Class<? extends IdGenerator> generatorClass = (Class<? extends IdGenerator>)Class.forName(generator);
					this.idGenerator = generatorClass.newInstance();
				} catch (ClassNotFoundException e) {
					logger.warn("找不到Id生成器类[" + generator + "]");
				} catch (Exception e) {
					logger.warn("无法初始化生成器[" + generator + "]", e);
				}
			}
		}
	}
	

	/**
	 * build ResultMap相关的Mapper操作
	 */
	private void buildXmlMapper() {
		String xml = FreemarkUtils.parse("template_mysql", this);
		System.out.println(xml);
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
	 * @return the updateMappings
	 */
	public List<ResultMapping> getUpdateMappings() {
		return updateMappings;
	}

	/**
	 * @return the insertMappings
	 */
	public List<ResultMapping> getInsertMappings() {
		return insertMappings;
	}

	/**
	 * @return the versionMapping
	 */
	public ResultMapping getVersionMapping() {
		return versionMapping;
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
	 * @return the idGenerator
	 */
	public IdGenerator getIdGenerator() {
		return idGenerator;
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
	

	private void initTableName() {
		Class<?> entityClass = sourceResultMap.getType();
		if (entityClass.isAnnotationPresent(Table.class)) {
			Table table = entityClass.getAnnotation(Table.class);
			this.tableName = table.name();
		}
		else {
			this.tableName = globalConfig.getTableNameStrategy().getTableName(globalConfig, entityClass);
		}
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
