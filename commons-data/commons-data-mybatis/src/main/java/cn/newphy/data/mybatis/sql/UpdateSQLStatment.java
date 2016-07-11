package cn.newphy.data.mybatis.sql;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultMapping;
import org.springframework.util.Assert;

import cn.newphy.data.mybatis.sql.expression.SQLExpression;

public class UpdateSQLStatment implements SQLStatement {
	// 表名
	private String tableName;
	// 列映射
	private Set<ResultMapping> updateColumns = new LinkedHashSet<ResultMapping>();
	// where条件
	private Set<SQLExpression> conditionExpressions = new LinkedHashSet<SQLExpression>();
	
	
	public UpdateSQLStatment(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * 增加列映射
	 * @param columnMapping
	 */
	public void addUpdateColumn(ResultMapping columnMapping) {
		updateColumns.add(columnMapping);
	}
	
	/**
	 * 增加条件表达式
	 * @param conditionExpression
	 */
	public void addCondition(SQLExpression conditionExpression) {
		conditionExpressions.add(conditionExpression);
	}

	@Override
	public String generateSQL() {
		Assert.isTrue(StringUtils.isNotBlank(tableName), "table name is empty");
		Assert.isTrue(CollectionUtils.isEmpty(updateColumns), "update columns is empty");

		StringBuilder updateSQL = new StringBuilder(UPDATE).append(tableName).append(" ");
		StringBuilder setSQL = new StringBuilder(SET);		
		StringBuilder whereSQL = new StringBuilder();
		int i = 0;
		for (ResultMapping columnMapping : updateColumns) {
			setSQL.append((i == 0) ? "" : ", ").append(columnMapping.getColumn()).append(" = ").append("#{" + columnMapping.getProperty() + "}");
			i++;
		}
		if(CollectionUtils.isNotEmpty(conditionExpressions)) {
			whereSQL.append(WHERE);
			for (SQLExpression sqlExpression : conditionExpressions) {
				
			}
		}
		return updateSQL.append(setSQL).append(whereSQL).toString();
	}

	public static void main(String[] args) {
		
	}
	
}
