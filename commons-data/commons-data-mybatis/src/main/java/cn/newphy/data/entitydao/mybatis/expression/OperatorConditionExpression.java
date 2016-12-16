package cn.newphy.data.entitydao.mybatis.expression;

import org.apache.ibatis.mapping.ResultMapping;

import cn.newphy.data.entitydao.SqlBuilder;

public class OperatorConditionExpression extends MybatisConditionExpression {
	private final ResultMapping columnMapping;
	private final Object value;
	private final String op;

	public OperatorConditionExpression(ResultMapping columnMapping, Object value, String op) {
		this.columnMapping = columnMapping;
		this.value = value;
		this.op = op;
	}

	@Override
	public CharSequence toSql(SqlBuilder dialect) {
		StringBuffer fragment = new StringBuffer();
		fragment.append(columnMapping.getColumn()).append(" ")
			.append(op).append(" ")
			.append("#{" + columnMapping.getProperty() + "}");
		return fragment;
	}

	@Override
	public String toString() {
		return columnMapping.getColumn() + " " + getOp() + " " + value;
	}
	
	@Override
	public String getPropertyName() {
		return columnMapping.getProperty();
	}
	
	@Override
	public Object getValue() {
		return getParameterMap(columnMapping.getProperty(), value);
	}

	protected final String getOp() {
		return op;
	}

}
