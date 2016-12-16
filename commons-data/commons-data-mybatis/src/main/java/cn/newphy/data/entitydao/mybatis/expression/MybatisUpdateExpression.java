package cn.newphy.data.entitydao.mybatis.expression;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.mapping.ResultMapping;

import cn.newphy.data.entitydao.SqlBuilder;
import cn.newphy.data.entitydao.UpdateExpression;

public class MybatisUpdateExpression implements UpdateExpression {

	private final ResultMapping columnMapping;
	private final Object value;
	
	public MybatisUpdateExpression(ResultMapping columnMapping, Object value) {
		this.columnMapping = columnMapping;
		this.value = value;
	}
	
	@Override
	public CharSequence toSql(SqlBuilder dialect) {
		return new StringBuilder(columnMapping.getColumn()).append(" = ").append("#{").append(getPlaceHolderName()).append("}");
	}

	@Override
	public String getPropertyName() {
		return columnMapping.getProperty();
	}

	@Override
	public Object getValue() {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put(getPlaceHolderName(), value);
		return paramMap;
	}
	
	private String getPlaceHolderName() {
		return "u_" + columnMapping.getProperty();
	}

}
