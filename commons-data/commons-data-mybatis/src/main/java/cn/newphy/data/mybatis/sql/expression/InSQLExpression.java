package cn.newphy.data.mybatis.sql.expression;

public class InSQLExpression extends SQLExpression {
	private final String propertyName;
	private final Object[] values;

	
	public InSQLExpression(String propertyName, Object[] values) {
		super();
		this.propertyName = propertyName;
		this.values = values;
	}


	@Override
	public CharSequence toSql() {
		StringBuffer fragment = new StringBuffer();
		fragment.append(propertyName).append(" IN ?");
		return fragment;
	}


	public Object[] getValues() {
		return values;
	}

	
	
	
	
	
}
