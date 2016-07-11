package cn.newphy.data.hibernate.query;

public class OperatorQueryExpression extends QueryExpression {
	private final String propertyName;
	private final Object value;
	private final String op;

	protected OperatorQueryExpression(String propertyName, Object value, String op) {
		this.propertyName = propertyName;
		this.value = value;
		this.op = op;
	}

	@Override
	public CharSequence toSql(QueryLanguage language) {
		StringBuffer fragment = new StringBuffer();
		fragment.append(propertyName).append(" ").append(op).append(" ").append("?");
		return fragment;
	}

	@Override
	public String toString() {
		return propertyName + " " + getOp() + " " + value;
	}

	@Override
	public Object[] getValues() {
		return new Object[] { value };
	}

	protected final String getOp() {
		return op;
	}

}
