package cn.newphy.commons.consistency.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonHelper {

	private SerializerFeature[] features = new SerializerFeature[0];
	
	public static JsonHelper getDefault() {
		JsonHelper jsonHelper = new JsonHelper();
		return jsonHelper;
	}
	
	public static JsonHelper getWithType() {
		JsonHelper jsonHelper = new JsonHelper();
		jsonHelper.features = new SerializerFeature[]{
				SerializerFeature.WriteClassName
		};
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
		return JSON.toJSONString(obj, features);
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
		return JSON.parseObject(json, type);
	}
}
