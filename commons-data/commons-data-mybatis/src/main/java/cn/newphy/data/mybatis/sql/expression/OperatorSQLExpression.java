package cn.newphy.data.mybatis.sql.expression;

import org.apache.ibatis.mapping.ResultMapping;

public class OperatorSQLExpression extends SQLExpression {
	private final ResultMapping columnMapping;
	private final Object value;
	private final String op;

	protected OperatorSQLExpression(ResultMapping columnMapping, Object value, String op) {
		this.columnMapping = columnMapping;
		this.value = value;
		this.op = op;
	}

	@Override
	public CharSequence toSql() {
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
	public Object[] getValues() {
		return new Object[] { value };
	}

	protected final String getOp() {
		return op;
	}

}
