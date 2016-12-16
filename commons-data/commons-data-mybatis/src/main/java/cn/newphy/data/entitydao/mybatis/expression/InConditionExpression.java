package cn.newphy.data.entitydao.mybatis.expression;

import org.apache.ibatis.mapping.ResultMapping;

import cn.newphy.data.entitydao.SqlBuilder;

public class InConditionExpression extends MybatisConditionExpression {
	private final ResultMapping columnMapping;
	private final Object[] values;

	
	public InConditionExpression(ResultMapping columnMapping, Object[] values) {
		super();
		this.columnMapping = columnMapping;
		this.values = values;
	}


	@Override
	public CharSequence toSql(SqlBuilder dialect) {
		if(values.length > 0) {
			StringBuffer fragment = new StringBuffer();
			fragment.append(columnMapping.getColumn()).append(" IN (");
			for(int i = 0; i < values.length; i++) {
				fragment.append("#{").append(columnMapping.getProperty() + i).append("}");
				if(i != values.length-1) {
					fragment.append(", ");
				}
			}
			fragment.append(")");
			return fragment;
		}
		return "";
	}
	
	@Override
	public String getPropertyName() {
		return columnMapping.getProperty();
	}

	@Override
	public Object getValue() {
		String[] paramNames = new String[values.length];
		for(int i = 0; i < values.length; i++) {
			paramNames[i] = columnMapping.getProperty() + i;
		}
		return getParameterMap(paramNames, values);
	}

}
