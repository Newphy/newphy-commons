package cn.newphy.commons.rest;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

abstract class RestRequestBase<T extends RestRequestBase<T>> implements RestRequest<T> {

	protected final HttpMethod httpMethod;
	protected HttpHeaders headers = new HttpHeaders();
	protected MultiValueMap<String, Object> urlParams = new LinkedMultiValueMap<>();
	protected Map<String, Object> urlVariableMap = new LinkedHashMap<>();
	protected String uri;
	protected String url;
	
	
	public RestRequestBase(HttpMethod method) {
		this.httpMethod = method;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T header(String key, String value) {
		headers.add(key, value);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T header(Map<String, String> headerMap) {
		if (headerMap != null) {
			for (String key : headerMap.keySet()) {
				headers.add(key, headerMap.get(key));
			}
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T header(MultiValueMap<String, String> headerMap) {
		if (headerMap != null) {
			headers.putAll(headerMap);
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T uri(String uri) {
		this.uri = uri;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T url(String url) {
		this.url = url;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T urlParam(String name, Object value) {
		urlParams.add(name, value);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T urlParam(Map<String, Object> paramMap) {
		if (paramMap != null) {
			for (String key : paramMap.keySet()) {
				urlParams.add(key, paramMap.get(key));
			}
		}
		return (T) this;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public T urlParam(Object bean) {
		if(bean != null) {
			try {
				Map<String, Object> beanMap = PropertyUtils.describe(bean);
				urlParam(beanMap);
			} catch (Exception e) {
				throw new IllegalArgumentException("设置链接参数异常", e);
			}
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T urlParam(MultiValueMap<String, Object> paramMap) {
		if (paramMap != null) {
			urlParams.putAll(paramMap);
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T urlVariable(String name, Object value) {
		this.urlVariableMap.put(name, value);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T urlVariable(Map<String, ?> urlVariableMap) {
		this.urlVariableMap.putAll(urlVariableMap);
		return (T) this;
	}

	/**
	 * @return the headers
	 */
	HttpHeaders getHeaders() {
		return headers;
	}

	/**
	 * @return the urlParams
	 */
	MultiValueMap<String, Object> getUrlParams() {
		return urlParams;
	}

	/**
	 * @return the urlVariableMap
	 */
	Map<String, ?> getUrlVariableMap() {
		return urlVariableMap;
	}

	/**
	 * @return the uri
	 */
	String getUri() {
		return uri;
	}

	/**
	 * @return the url
	 */
	String getUrl() {
		return url;
	}

	/**
	 * @return the httpMethod
	 */
	HttpMethod getHttpMethod() {
		return httpMethod;
	}
	


}
