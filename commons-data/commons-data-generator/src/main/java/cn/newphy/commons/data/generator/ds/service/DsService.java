package cn.newphy.commons.data.generator.ds.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.newphy.commons.data.generator.db.DBInfo;
import cn.newphy.commons.data.generator.db.DBInfoFactory;
import cn.newphy.commons.data.generator.ds.dao.DsDao;
import cn.newphy.commons.data.generator.model.ds.Column;
import cn.newphy.commons.data.generator.model.ds.Ds;
import cn.newphy.commons.data.generator.model.ds.Entity;
import cn.newphy.commons.data.generator.model.ds.Property;
import cn.newphy.commons.data.generator.model.ds.Schema;
import cn.newphy.commons.data.generator.model.ds.Table;
import cn.newphy.commons.data.generator.utils.CamelCaseUtils;
import cn.newphy.commons.data.generator.utils.TypeUtils;

@Service
public class DsService {

	@Autowired
	private DsDao dsDao;
	
	/**
	 * 获得DS
	 * @param dsId
	 * @return
	 */
	public Ds getDs(String dsId) {
		return dsDao.get(dsId);
	}
	
	/**
	 * 获得DS
	 * @param dsId
	 * @return
	 */
	public void deleteDs(String id) {
		dsDao.delete(id);
	}
	
	/**
	 * 查询所有Ds
	 * @return
	 */
	public List<Ds> queryDs() {
		return dsDao.query();
	}
	
	/**
	 * 增加Ds
	 * @param ds
	 */
	public void addDs(Ds ds) {
		dsDao.save(ds);
	}
	
	
	/**
	 * 修改Ds
	 * @param ds
	 */
	public void editDs(Ds ds) {
		dsDao.update(ds);
	}

	/**
	 * 检查数据源
	 * 
	 * @param ds
	 * @return
	 */
	public boolean checkDs(Ds ds) {
		DBInfo dbInfo = DBInfoFactory.getDBInfo(ds);
		Connection conn = null;
		try {
			conn = dbInfo.getConnection();
			if(conn != null) {
				return true;
			}
			return false;
		} catch(Exception e) {
			return false;
		}
		finally {
			dbInfo.close(conn, null, null);
		}
	}
	
	/**
	 * 获得Schema列表
	 * @param ds
	 * @return
	 */
	public List<Schema> getSchemas(Ds ds) {
		DBInfo dbInfo = DBInfoFactory.getDBInfo(ds);
		List<Schema> schemas = dbInfo.getSchemaList();
		return schemas;
	}
	
	/**
	 * 获得Schema
	 * @param dsId
	 * @param schemaName
	 * @return
	 */
	public Schema getSchema(String dsId, String schemaName) {
		if(dsId == null || dsId.length() == 0 || schemaName == null || schemaName.length() == 0) {
			return null;
		}
		Ds ds = dsDao.get(dsId);
		DBInfo dbInfo = DBInfoFactory.getDBInfo(ds);
		Schema schema = dbInfo.getSchema(schemaName);
		return schema;
	}
	
	/**
	 * 获得Table
	 * @param dsId
	 * @param schemaName
	 * @param tableName
	 * @return
	 */
	public Table getTable(String dsId, String schemaName, String tableName) {
		Schema schema = getSchema(dsId, schemaName);
		if(schema == null) {
			return null;
		}
		List<Table> tables = schema.getTables();
		if(tables != null) {
			for (Table table : tables) {
				if(table.getName().equals(tableName)) {
					return table;
				}
			}
		}
		return null;
	}
	
	public Entity getEntity(Table table) {
		Entity entity = new Entity();
		entity.setTable(table);
		entity.setComment(table.getComment());
		entity.setName(CamelCaseUtils.underlineToCamelUpper(table.getName()));
		List<Property> properties = new ArrayList<Property>();
		for (Column column : table.getColumns()) {
			properties.add(getProperty(column));
		}
		entity.setProperties(properties);
		return entity;
	}
	
	private Property getProperty(Column column) {
		Property property = new Property();
		property.setColumn(column);
		property.setName(CamelCaseUtils.underlineToCamel(column.getName()));
		property.setType(TypeUtils.jdbcType2JavaType(column.getType(), column.getScale(), column.getLength(), false));
		property.setComment(column.getComment());
		return property;
	}
}
