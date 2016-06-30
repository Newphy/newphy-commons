package cn.newphy.commons.rest;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class RestClient implements InitializingBean {

	private String rootPath;
	private RestTemplate restTemplate;

	/**
	 * post请求
	 * 
	 * @param uri
	 * @return
	 */
	public RestEnclosingRequest<?> postUri(String uri) {
		return new RestPost().uri(uri);
	}

	/**
	 * post请求
	 * 
	 * @param uri
	 * @return
	 */
	public RestEnclosingRequest<?> postUrl(String url) {
		return new RestPost().url(url);
	}

	/**
	 * get请求
	 * 
	 * @param uri
	 * @return
	 */
	public RestRequest<?> getUri(String uri) {
		return new RestGet().uri(uri);
	}

	/**
	 * get请求
	 * 
	 * @param Url
	 * @return
	 */
	public RestRequest<?> getUrl(String url) {
		return new RestGet().url(url);
	}

	/**
	 * put请求
	 * 
	 * @param uri
	 * @return
	 */
	public RestEnclosingRequest<?> putUri(String uri) {
		return new RestPut().uri(uri);
	}

	/**
	 * put请求
	 * 
	 * @param Url
	 * @return
	 */
	public RestEnclosingRequest<?> putUrl(String url) {
		return new RestPut().url(url);
	}

	/**
	 * delete请求
	 * 
	 * @param uri
	 * @return
	 */
	public RestRequest<?> deleteUri(String uri) {
		return new RestDelete().uri(uri);
	}

	/**
	 * delete请求
	 * 
	 * @param Url
	 * @return
	 */
	public RestRequest<?> deleteUrl(String url) {
		return new RestDelete().url(url);
	}

	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (restTemplate == null) {
			restTemplate = new RestTemplate();
		}
	}

	private String uri2Url(String uri) {
		return rootPath + (StringUtils.startsWith(uri, "/") ? "" : "/") + uri;
	}

	/**
	 * @return the restTemplate
	 */
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	/**
	 * @param restTemplate
	 *            the restTemplate to set
	 */
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * @return the rootPath
	 */
	public String getRootPath() {
		return rootPath;
	}

	/**
	 * @param rootPath
	 *            the rootPath to set
	 */
	public void setRootPath(String rootPath) {
		this.rootPath = StringUtils.removeEnd(rootPath, "/");
	}

	private class RestPost extends RestEnclosingRequestBase<RestPost> {
		public RestPost() {
			super(HttpMethod.POST);
		}

		@Override
		public <R> R request(Class<R> responseType) {
			return exchange(RestPost.this, responseType);
		}
	}

	private class RestGet extends RestRequestBase<RestGet> {
		public RestGet() {
			super(HttpMethod.GET);
		}

		@Override
		public <R> R request(Class<R> responseType) {
			return exchange(RestGet.this, responseType);
		}
	}

	private class RestPut extends RestEnclosingRequestBase<RestPut> {
		public RestPut() {
			super(HttpMethod.PUT);
		}

		@Override
		public <R> R request(Class<R> responseType) {
			return exchange(RestPut.this, responseType);
		}
	}

	private class RestDelete extends RestRequestBase<RestDelete> {
		public RestDelete() {
			super(HttpMethod.DELETE);
		}

		@Override
		public <R> R request(Class<R> responseType) {
			return exchange(RestDelete.this, responseType);
		}
	}

	private String parseUrl(RestRequestBase<?> restRequest, String url) {
		MultiValueMap<String, Object> urlParams = restRequest.getUrlParams();
		if (urlParams != null && urlParams.size() > 0) {
			int i = 0;
			for (Entry<String, List<Object>> entry : urlParams.entrySet()) {
				url += ((i == 0 && !url.contains("?")) ? "?" : "&") + entry.getKey() + "=" + ConvertUtils.convert(entry.getValue());
				i++;
			}
		}
		return url;
	}

	private <R> R exchange(RestRequestBase<?> restRequest, Class<R> responseType) {
		HttpMethod method = restRequest.getHttpMethod();
		String url = restRequest.getUrl();
		if (StringUtils.isBlank(url)) {
			url = uri2Url(restRequest.getUri());
		}
		if (url == null) {
			throw new IllegalArgumentException("链接参数为空");
		}
		url = parseUrl(restRequest, url);
		BodyHandler[] bodyHandlers = { new TextBodyHandler(), new FormBodyHandler(), new JSONBodyHandler() };

		Object requestObject = null;
		for (BodyHandler bodyHandler : bodyHandlers) {
			if (bodyHandler.canHandle(restRequest)) {
				requestObject = bodyHandler.handleBody(restRequest);
				break;
			}
		}
		HttpHeaders headers = restRequest.getHeaders();
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestObject, headers);
		Map<String, ?> urlVariables = restRequest.getUrlVariableMap();
		ResponseEntity<R> response = restTemplate.exchange(url, method, requestEntity, responseType, urlVariables);
		return response.getBody();
	}

	interface BodyHandler {
		/**
		 * 是否能处理
		 * 
		 * @return
		 */
		boolean canHandle(RestRequestBase<?> restRequestBase);

		/**
		 * 处理Body内容
		 * 
		 * @param restEnclosingRequestBase
		 * @return
		 */
		Object handleBody(RestRequestBase<?> restRequestBase);
	}

	// 文本处理
	private class TextBodyHandler implements BodyHandler {
		@Override
		public boolean canHandle(RestRequestBase<?> restRequestBase) {
			return restRequestBase instanceof RestEnclosingRequestBase
					&& ((RestEnclosingRequestBase<?>) restRequestBase).getBodyText() != null;
		}

		@Override
		public Object handleBody(RestRequestBase<?> restRequestBase) {
			if (!(restRequestBase instanceof RestEnclosingRequestBase)) {
				return null;
			}
			RestEnclosingRequestBase<?> restEnclosingRequestBase = (RestEnclosingRequestBase<?>) restRequestBase;
			MediaType mediaType = restEnclosingRequestBase.getHeaders().getContentType();
			if (mediaType == null) {
				restEnclosingRequestBase.getHeaders().setContentType(MediaType.TEXT_PLAIN);
			}
			return restEnclosingRequestBase.getBodyText();
		}
	}

	// 表单提交
	private class FormBodyHandler implements BodyHandler {
		@Override
		public boolean canHandle(RestRequestBase<?> restRequestBase) {
			if (restRequestBase instanceof RestEnclosingRequestBase) {
				return !((RestEnclosingRequestBase<?>) restRequestBase).getBodyParams().isEmpty();
			}
			return false;
		}

		@Override
		public Object handleBody(RestRequestBase<?> restRequestBase) {
			if (!(restRequestBase instanceof RestEnclosingRequestBase)) {
				return null;
			}
			RestEnclosingRequestBase<?> restEnclosingRequestBase = (RestEnclosingRequestBase<?>) restRequestBase;
			MultiValueMap<String, ?> params = restEnclosingRequestBase.getBodyParams();
			MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
			for (String key : params.keySet()) {
				List<?> values = params.get(key);
				for (Object value : values) {
					Map<String, String> resultMap = handleParameterBean(value);
					for (String rkey : resultMap.keySet()) {
						result.add(key + rkey, resultMap.get(rkey));
					}
				}
			}
			// contentType 为空默认为表单提交
			return result;
		}
	}

	// JSON
	private class JSONBodyHandler implements BodyHandler {
		@Override
		public boolean canHandle(RestRequestBase<?> restRequestBase) {
			return restRequestBase instanceof RestEnclosingRequestBase
					&& ((RestEnclosingRequestBase<?>) restRequestBase).getBodyJsonObject() != null;
		}

		@Override
		public Object handleBody(RestRequestBase<?> restRequestBase) {
			if (!(restRequestBase instanceof RestEnclosingRequestBase)) {
				return null;
			}
			RestEnclosingRequestBase<?> restEnclosingRequestBase = (RestEnclosingRequestBase<?>) restRequestBase;
			MediaType mediaType = restEnclosingRequestBase.getHeaders().getContentType();
			if (mediaType == null) {
				restEnclosingRequestBase.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			}
			return restEnclosingRequestBase.getBodyJsonObject();
		}
	}
	

	@SuppressWarnings("unchecked")
	private static Map<String, String> handleParameterBean(Object object) {
		Map<String, String> result = new LinkedHashMap<>();
		if(object == null) {
			return result;
		}
		Class<?> dataType = object.getClass();
		if(ClassUtils.isPrimitiveOrWrapper(dataType)
				|| Number.class.isAssignableFrom(dataType)
				|| CharSequence.class.isAssignableFrom(dataType)
				|| Date.class.isAssignableFrom(dataType)) {
			if(Date.class.equals(dataType)) {
				result.put("", DateFormatUtils.format((Date)object, "yyyy-MM-dd HH:mm:ss"));
				return result;
			}
			result.put("", ConvertUtils.convert(object));
			return result;
		}
		if(Map.class.isAssignableFrom(dataType)) {
			Map<String, ?> map = (Map<String, ?>)object;
			for (String key : map.keySet()) {
				Object temp = map.get(key);
				Map<String, String> resultMap = handleParameterBean(temp);
				for (String rkey	 : resultMap.keySet()) {
					result.put("['" + key + "']"+ rkey, resultMap.get(rkey));
				}
			}
		}
		else if(Collection.class.isAssignableFrom(dataType)) {
			Collection<?> collection = (Collection<?>)object;
			int i = 0;
			for (Object temp : collection) {
				Map<String, String> resultMap = handleParameterBean(temp);
				for (String rkey	 : resultMap.keySet()) {
					result.put("[" + i + "]"+ rkey, resultMap.get(rkey));
				}
				i++;
			}
		}
		else {
			try {
				PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(dataType);
				for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
					if(!"class".equals(propertyDescriptor.getName())) {
						Object propValue = PropertyUtils.getNestedProperty(object, propertyDescriptor.getName());
						Map<String, String> resultMap = handleParameterBean(propValue);
						for (String rkey	 : resultMap.keySet()) {
							result.put("." + propertyDescriptor.getName() + rkey, resultMap.get(rkey));
						}
					}
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("读取属性出错", e);
			}
		}
		
		return result;
	}

	
	public static void main(String[] args) {
		Map<String, Integer> scores = new HashMap<>();
		scores.put("math", 98);
		scores.put("geo", 67);
		scores.put("english", 97);
		
		Student john = new Student();
		john.setName("John");
		john.setAge(12);
		john.setAmount(BigDecimal.valueOf(12.22D));
		john.setSubjectScore(scores);
		john.setClassmates(new ArrayList<Student>());
		
		Student smith = new Student();
		smith.setName("Smith");
		smith.setAge(13);
		smith.setAmount(BigDecimal.valueOf(23.22D));
		smith.setSubjectScore(scores);
		smith.setClassmates(new ArrayList<Student>());
		
		Student jake = new Student();
		jake.setName("Jake");
		jake.setAge(15);
		jake.setAmount(BigDecimal.valueOf(123.22D));
		jake.setSubjectScore(scores);
		jake.setClassmates(new ArrayList<Student>());
		
		john.getClassmates().add(smith);
		john.getClassmates().add(jake);
		
		john.getPartners().put("partner1", smith);
		john.getPartners().put("partner2", jake);
		
		System.out.println(handleParameterBean(john));
	}
}

