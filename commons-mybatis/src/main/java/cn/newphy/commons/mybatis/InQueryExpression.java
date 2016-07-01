package cn.newphy.commons.mybatis;

public class InQueryExpression extends QueryExpression {
	private final String propertyName;
	private final Object[] values;

	
	public InQueryExpression(String propertyName, Object[] values) {
		super();
		this.propertyName = propertyName;
		this.values = values;
	}


	@Override
	public CharSequence toSql(QueryLanguage language) {
		StringBuffer fragment = new StringBuffer();
		fragment.append(propertyName).append(" IN ?");
		return fragment;
	}


	public Object[] getValues() {
		return values;
	}

	
	
	
	
	
}
