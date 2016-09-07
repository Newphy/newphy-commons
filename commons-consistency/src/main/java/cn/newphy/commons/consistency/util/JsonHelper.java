package cn.newphy.commons.consistency.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

	private ObjectMapper objectMapper = new ObjectMapper();
	
	public static JsonHelper getDefault() {
		JsonHelper jsonHelper = new JsonHelper();
		return jsonHelper;
	}
	
	public static JsonHelper getWithType() {
		JsonHelper jsonHelper = new JsonHelper();
		jsonHelper.objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		return jsonHelper;		
	}
	
	/**
	 * toJson
	 * @param obj
	 * @return
	 */
	public String toJson(Object obj) {
		if(obj == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 转为Object
	 * @param json
	 * @param type
	 * @return
	 */
	public <T> T toObject(String json, Class<T> type) {
		if(json == null) {
			return null;
		}
		try {
			return objectMapper.readValue(json, type);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
