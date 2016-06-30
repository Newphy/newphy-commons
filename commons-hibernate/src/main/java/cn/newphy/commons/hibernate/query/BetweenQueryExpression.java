package cn.newphy.commons.hibernate.query;

public class BetweenQueryExpression extends QueryExpression {
	private final String propertyName;
	private final Object lo;
	private final Object hi;

	protected BetweenQueryExpression(String propertyName, Object lo, Object hi) {
		this.propertyName = propertyName;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	public CharSequence toSql(QueryLanguage language) {
		StringBuffer fragment = new StringBuffer();
		fragment.append(propertyName).append(" BETWEEN ? AND ?");
		return fragment;
	}

	@Override
	public Object[] getValues() {
		return new Object[] { lo, hi };
	}

}
