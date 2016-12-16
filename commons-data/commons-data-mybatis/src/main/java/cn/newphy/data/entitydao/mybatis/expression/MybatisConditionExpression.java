package cn.newphy.data.entitydao.mybatis.expression;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.newphy.data.entitydao.ConditionExpression;

public abstract class MybatisConditionExpression implements ConditionExpression {

	
	protected Map<String, Object> getParameterMap(String key, Object value) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(key, value);
		return map;
	}
	
	protected Map<String, Object> getParameterMap(String[] keys, Object[] values) {
		Map<String, Object> map = new LinkedHashMap<>();
		for(int i = 0; i < keys.length; i++) {
			map.put(keys[i], values[i]);
		}
		return map;
	}
}
