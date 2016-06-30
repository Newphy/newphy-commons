package cn.newphy.commons.rest;

import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

abstract class RestEnclosingRequestBase<T extends RestEnclosingRequestBase<T>> extends RestRequestBase<T>
		implements RestEnclosingRequest<T> {

	protected MultiValueMap<String, Object> bodyParams = new LinkedMultiValueMap<>();

	protected String bodyJsonText;

	protected Object bodyJsonObject;

	protected String bodyText;


	public RestEnclosingRequestBase(HttpMethod method) {
		super(method);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T bodyParam(String name, Object value) {
		bodyParams.add(name, value);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T bodyParam(Map<String, Object> paramMap) {
		if(paramMap != null) {
			for (String key : paramMap.keySet()) {
				bodyParams.add(key, paramMap.get(key));
			}
		}
		return (T) this;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public T bodyParam(Object bean) {
		if(bean != null) {
			try {
				Map<String, Object> beanMap = PropertyUtils.describe(bean);
				bodyParam(beanMap);
			} catch (Exception e) {
				throw new IllegalArgumentException("设置表单参数异常", e);
			}
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T bodyParam(MultiValueMap<String, Object> paramMap) {
		if(paramMap != null) {
			bodyParams.putAll(paramMap);
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T bodyJSON(Object bodyObject) {
		this.bodyJsonObject = bodyObject;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T bodyText(String text) {
		this.bodyText = text;
		return (T) this;
	}

	/**
	 * @return the formParams
	 */
	MultiValueMap<String, Object> getBodyParams() {
		return bodyParams;
	}

	/**
	 * @return the bodyJsonText
	 */
	String getBodyJsonText() {
		return bodyJsonText;
	}

	/**
	 * @return the bodyJsonObject
	 */
	Object getBodyJsonObject() {
		return bodyJsonObject;
	}

	/**
	 * @return the bodyText
	 */
	String getBodyText() {
		return bodyText;
	}

}
