package cn.newphy.commons.rest;

import java.util.Map;

import org.springframework.util.MultiValueMap;

public interface RestRequest<T extends RestRequest<T>> {

	/**
	 * 设置头部信息
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	T header(String key, String value);

	/**
	 * 设置头部信息
	 * 
	 * @param headerMap
	 * @return
	 */
	T header(Map<String, String> headerMap);

	/**
	 * 设置头部信息
	 * 
	 * @param headerMap
	 * @return
	 */
	T header(MultiValueMap<String, String> headerMap);

	/**
	 * 设置请求URI
	 * 
	 * @param uri
	 * @return
	 */
	T uri(String uri);

	/**
	 * 设置请求URL
	 * 
	 * @param url
	 * @return
	 */
	T url(String url);

	/**
	 * 设置链接参数
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	T urlParam(String name, Object value);
	
	/**
	 * 设置链接参数
	 * 
	 * @param paramMap
	 * @return
	 */
	T urlParam(Map<String, Object> paramMap);
	
	
	/**
	 * 设置链接参数
	 * @param bean
	 * @return
	 */
	T urlParam(Object bean);

	/**
	 * 设置链接参数
	 * 
	 * @param paramMap
	 * @return
	 */
	T urlParam(MultiValueMap<String, Object> paramMap);

	/**
	 * 设置链接变量
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	T urlVariable(String name, Object value);

	/**
	 * 设置链接变量
	 * 
	 * @param variableMap
	 * @return
	 */
	T urlVariable(Map<String, ?> urlVariableMap);

	/**
	 * 请求
	 * 
	 * @param responseType
	 * @return
	 */
	<R> R request(Class<R> responseType);
}
