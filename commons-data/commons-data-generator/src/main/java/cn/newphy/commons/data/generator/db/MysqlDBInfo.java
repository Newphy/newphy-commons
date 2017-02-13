package cn.newphy.commons.data.generator.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.newphy.commons.data.generator.model.ds.Column;
import cn.newphy.commons.data.generator.model.ds.Ds;
import cn.newphy.commons.data.generator.model.ds.Schema;
import cn.newphy.commons.data.generator.model.ds.Table;

public class MysqlDBInfo extends DBInfo {
	
	public MysqlDBInfo(Ds ds) {
		super(ds);
	}

	@Override
	public List<Schema> getSchemaList() {
		String sql = "SELECT DISTINCT table_schema " 
				+ "FROM information_schema.tables "
				+ "WHERE table_type = 'BASE TABLE' "
				+ "AND table_schema NOT IN('performance_schema', 'mysql', 'phpmyadmin', 'information_schema')";
		try {
			List<Schema> schemas = query(sql, new ResultHandler<Schema>() {
				@Override
				public Schema doInvoke(ResultSet rs) throws SQLException {
					Schema schema = new Schema();
					schema.setName(rs.getString("table_schema"));
					return schema;
				}
			});
			return schemas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public Schema getSchema(String schemaName) {
		String sql = "SELECT table_name, table_comment FROM information_schema.tables WHERE table_schema = '" + schemaName + "'";
		try {
			Schema schema = new Schema();
			schema.setName(schemaName);
			final Map<String, Table> tableMap = new HashMap<>();
			List<Table> tables = query(sql, new ResultHandler<Table>() {
				@Override
				public Table doInvoke(ResultSet rs) throws SQLException {
					Table table = new Table();
					 table.setName(rs.getString("table_name"));
					 table.setComment(rs.getString("table_comment"));
					 tableMap.put(table.getName(), table);
					return  table;
				}
			});
		
			
			Connection conn = getConnection();
			ResultSet rs = conn.getMetaData().getColumns(schemaName, null, null, null);
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
	            String name = rs.getString("COLUMN_NAME");
				int type = rs.getInt("DATA_TYPE");
				String comment = rs.getString("REMARKS");
				int scale = rs.getInt("DECIMAL_DIGITS");
				int length = rs.getInt("COLUMN_SIZE");
				boolean nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
				String typeName = rs.getString("TYPE_NAME");
				String columnDef = rs.getString("COLUMN_DEF");
				boolean autoIncrement = "YES".equalsIgnoreCase(rs.getString("IS_AUTOINCREMENT"));
				
				Column column = new Column();
				column.setAutoIncrement(autoIncrement);
				column.setName(name);
				column.setType(type);
				column.setTypeName(typeName);
				column.setColumnDef(columnDef);
				column.setComment(comment);
				column.setScale(scale);
				column.setLength(length);
				column.setNullable(nullable);
				if(tableMap.containsKey(tableName)) {
					tableMap.get(tableName).getColumns().add(column);
				}
	        }
			
			schema.setTables(tables);
			return schema;
		} catch (Exception e) {
			throw new IllegalStateException("连接失败", e);
		}
	}



}
