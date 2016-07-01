package cn.newphy.commons.rest;

import java.util.Map;

import org.springframework.util.MultiValueMap;

public interface RestEnclosingRequest<T extends RestEnclosingRequest<T>> extends RestRequest<T> {

	/**
	 * 设置表单参数
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	T bodyParam(String name, Object value);

	/**
	 * 设置表单参数
	 * 
	 * @param paramMap
	 * @return
	 */
	T bodyParam(Map<String, Object> paramMap);
	
	/**
	 * 设置表单参数
	 * @param bean
	 * @return
	 */
	T bodyParam(Object bean);

	/**
	 * 设置表单参数
	 * 
	 * @param paramMap
	 * @return
	 */
	T bodyParam(MultiValueMap<String, Object> paramMap);


	/**
	 * 设置Body JSON
	 * 
	 * @param bodyObject
	 * @return
	 */
	T bodyJSON(Object bodyObject);

	/**
	 * 设置body text
	 * 
	 * @param text
	 * @return
	 */
	T bodyText(String text);

}
