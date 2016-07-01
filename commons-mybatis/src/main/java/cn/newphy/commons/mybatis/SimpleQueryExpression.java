package cn.newphy.commons.mybatis;

public class SimpleQueryExpression extends QueryExpression {

	private final String propertyName;
	private final String expression;

	protected SimpleQueryExpression(String propertyName, String expression) {
		this.propertyName = propertyName;
		this.expression = expression;
	}

	@Override
	public CharSequence toSql(QueryLanguage language) {
		return new StringBuffer().append(propertyName).append(expression);
	}

	@Override
	public Object[] getValues() {
		return new Object[0];
	}

}
