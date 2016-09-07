package cn.newphy.commons.consistency.invoker;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConsistencyModel extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = -724326704538171038L;
	
	public ConsistencyModel(Map<String, Object> map) {
		super(map);
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		Object obj = super.get(key);
		return (T)obj;
	}
	
	
}
