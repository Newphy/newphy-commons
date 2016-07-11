package cn.newphy.data.mybatis.sql.expression;

import org.apache.ibatis.mapping.ResultMapping;

public class SimpleSQLExpression extends SQLExpression {

	private final ResultMapping columnMapping;
	private final String expression;

	protected SimpleSQLExpression(ResultMapping columnMapping, String expression) {
		this.columnMapping = columnMapping;
		this.expression = expression;
	}

	@Override
	public CharSequence toSql() {
		return new StringBuffer().append(columnMapping.getColumn()).append(expression);
	}

	@Override
	public Object[] getValues() {
		return new Object[0];
	}

}
