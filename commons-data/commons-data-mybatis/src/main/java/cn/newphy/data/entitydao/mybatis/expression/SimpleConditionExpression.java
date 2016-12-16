package cn.newphy.data.entitydao.mybatis.expression;

import org.apache.ibatis.mapping.ResultMapping;

import cn.newphy.data.entitydao.SqlBuilder;
import cn.newphy.data.entitydao.ConditionExpression;
import cn.newphy.data.entitydao.mybatis.EmptyValue;

public class SimpleConditionExpression implements ConditionExpression {

	private final ResultMapping columnMapping;
	private final String expression;

	public SimpleConditionExpression(ResultMapping columnMapping, String expression) {
		this.columnMapping = columnMapping;
		this.expression = expression;
	}

	@Override
	public CharSequence toSql(SqlBuilder dialect) {
		return new StringBuffer().append(columnMapping.getColumn()).append(expression);
	}

	@Override
	public String getPropertyName() {
		return columnMapping.getProperty();
	}
	
	@Override
	public Object getValue() {
		return EmptyValue.VALUE;
	}

}
