package cn.newphy.data.entitydao.mybatis.expression;

import cn.newphy.data.entitydao.ConditionExpression;
import cn.newphy.data.entitydao.SqlBuilder;

public class RawSqlExpression implements ConditionExpression {
	
	private final String sql;
	
	public RawSqlExpression(String sql) {
		this.sql = sql;
	}

	@Override
	public CharSequence toSql(SqlBuilder dialect) {
		return sql;
	}

	@Override
	public String getPropertyName() {
		return null;
	}

	@Override
	public Object getValue() {
		return null;
	}

}
