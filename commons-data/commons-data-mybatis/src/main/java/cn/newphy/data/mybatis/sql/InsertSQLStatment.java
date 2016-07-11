package cn.newphy.data.mybatis.sql;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultMapping;

public class InsertSQLStatment implements SQLStatement {
	// 表名
	private String tableName;
	// 列映射
	private Set<ResultMapping> insertColumns = new LinkedHashSet<ResultMapping>();
	
	public InsertSQLStatment(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * 增加列映射
	 * @param columnMapping
	 */
	public void addInsertColumnMapping(ResultMapping columnMapping) {
		insertColumns.add(columnMapping);
	}

	@Override
	public String generateSQL() {
		if(StringUtils.isBlank(tableName)) {
			throw new IllegalStateException("表名为空");
		}
		if(CollectionUtils.isEmpty(insertColumns)) {
			throw new IllegalStateException("数据表列为空");
		}
		StringBuilder preSQL = new StringBuilder(INSERT).append(tableName).append("(");
		StringBuilder postSQL = new StringBuilder(VALUES).append("(");
		int i = 0;
		for (ResultMapping columnMapping : insertColumns) {
			preSQL.append(i == 0 ? "" : ", ").append(columnMapping.getColumn());
			postSQL.append(i == 0 ? "" : ", ").append("#{" + columnMapping.getProperty() + "}");
			i++;
		}
		preSQL.append(")");
		postSQL.append(")");
		return preSQL.append(postSQL).toString();
	}

	
	
}
