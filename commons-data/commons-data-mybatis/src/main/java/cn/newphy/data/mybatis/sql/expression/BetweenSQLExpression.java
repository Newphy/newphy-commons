package cn.newphy.data.mybatis.sql.expression;

import org.apache.ibatis.mapping.ResultMapping;

public class BetweenSQLExpression extends SQLExpression {
	private final ResultMapping columnMapping;
	private final Object lo;
	private final Object hi;

	protected BetweenSQLExpression(ResultMapping columnMapping, Object lo, Object hi) {
		this.columnMapping = columnMapping;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	public CharSequence toSql() {
		StringBuffer fragment = new StringBuffer();
		fragment.append(columnMapping.getColumn())
			.append(" BETWEEN #{" + columnMapping.getProperty() + "Lo}")
			.append(" AND #{" + columnMapping.getProperty() + "Hi}");
		return fragment;
	}

	@Override
	public Object[] getValues() {
		return new Object[] { lo, hi };
	}

}
