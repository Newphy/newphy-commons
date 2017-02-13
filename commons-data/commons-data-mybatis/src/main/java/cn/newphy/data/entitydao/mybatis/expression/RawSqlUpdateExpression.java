package cn.newphy.data.entitydao.mybatis.expression;

import cn.newphy.data.entitydao.SqlBuilder;
import cn.newphy.data.entitydao.UpdateExpression;

public class RawSqlUpdateExpression implements UpdateExpression {
	
	private final String sql;

	public RawSqlUpdateExpression(String sql) {
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
