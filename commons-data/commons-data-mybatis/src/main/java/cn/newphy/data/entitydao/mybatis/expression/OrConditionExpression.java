package cn.newphy.data.entitydao.mybatis.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.newphy.data.entitydao.SqlBuilder;
import cn.newphy.data.entitydao.mybatis.builder.MybatisSqlBuilder;
import cn.newphy.data.entitydao.ConditionExpression;

public class OrConditionExpression extends MybatisConditionExpression {
	private List<ConditionExpression> queryExpressions = new ArrayList<ConditionExpression>();
	
	public OrConditionExpression(ConditionExpression...expressions) {
		if(expressions == null || expressions.length < 2) {
			throw new IllegalArgumentException("OR表达式成员数量不能少于2");
		}
		queryExpressions.addAll(Arrays.asList(expressions));
	}


	@Override
	public CharSequence toSql(SqlBuilder dialect) {
		MybatisSqlBuilder mybatisDialect = (MybatisSqlBuilder)dialect;
		List<CharSequence> fragments = new ArrayList<CharSequence>();
		for (ConditionExpression expression : queryExpressions) {
			fragments.add(expression.toSql(dialect));
		}
		return mybatisDialect.getOrString(fragments);
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
