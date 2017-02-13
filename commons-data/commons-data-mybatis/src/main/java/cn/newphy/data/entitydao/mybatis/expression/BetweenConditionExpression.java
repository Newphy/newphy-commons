package cn.newphy.data.entitydao.mybatis.expression;

import org.apache.ibatis.mapping.ResultMapping;

import cn.newphy.data.entitydao.SqlBuilder;

public class BetweenConditionExpression extends MybatisConditionExpression {
	private final ResultMapping columnMapping;
	private final Object lo;
	private final Object hi;

	public BetweenConditionExpression(ResultMapping columnMapping, Object lo, Object hi) {
		this.columnMapping = columnMapping;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	public CharSequence toSql(SqlBuilder sqlBuilder) {
		StringBuffer fragment = new StringBuffer();
		fragment.append(columnMapping.getColumn())
			.append(" BETWEEN #{" + columnMapping.getProperty() + "Lo}")
			.append(" AND #{" + columnMapping.getProperty() + "Hi}");
		return fragment;
	}


	@Override
	public String getPropertyName() {
		return columnMapping.getProperty();
	}

	@Override
	public Object getValue() {
		return getParameterMap(new String[]{columnMapping.getProperty() + "Lo", columnMapping.getProperty() + "Hi" }, new Object[]{lo, hi});
	}

}
