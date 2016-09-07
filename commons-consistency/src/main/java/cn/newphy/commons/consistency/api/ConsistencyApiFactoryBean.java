package cn.newphy.commons.consistency.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.newphy.commons.consistency.ConfirmLevel;
import cn.newphy.commons.consistency.ConsistencyHandler;
import cn.newphy.commons.consistency.ConsistencyInfo;
import cn.newphy.commons.consistency.ConsistencyMessage;
import cn.newphy.commons.consistency.util.JsonHelper;
import cn.newphy.commons.consistency.util.PathUtils;

public class ConsistencyApiFactoryBean<T> implements FactoryBean<T> {
	private Logger logger = LoggerFactory.getLogger(ConsistencyApiFactoryBean.class);

	// 接口类
	private Class<T> consistencyInterface;

	private ConsistencyHandler consistencyHandler;

	private JsonHelper jsonHelper = JsonHelper.getWithType();

	public ConsistencyApiFactoryBean() {
	}

	public ConsistencyApiFactoryBean(Class<T> consistencyInterface) {
		this.consistencyInterface = consistencyInterface;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { consistencyInterface },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						Class<?> clazz = method.getDeclaringClass();
						if (Object.class.equals(clazz)) {
							try {
								return method.invoke(this, args);
							} catch (Throwable t) {
								throw t;
							}
						}
						return invokeConsistency(method, args);
					}
				});
	}

	private Object invokeConsistency(Method method, Object[] args) throws JsonProcessingException {
		ConsistencyInfo cinfo = getInterfaceConsistencyInfo();
		ConsistencyApi consistencyApi = method.getAnnotation(ConsistencyApi.class);
		if (consistencyApi == null) {
			logger.warn("{}.{}() not present annotation ConsistentApi!", consistencyInterface.getCanonicalName(),
					method.getName());
			return null;
		}

		initConsistencyInfo(cinfo, consistencyApi);
		// 发送message
		ConsistencyMessage message = new ConsistencyMessage();
		message.setTxId(cinfo.getTxId());
		message.setConfirmLevel(cinfo.getConfirmLevel().getValue());
		for (int i = 0; i < args.length; i++) {
			Object object = args[i];
			message.addAttribute("args-" + i, object);
		}
		cinfo.setContent(jsonHelper.toJson(message));
		consistencyHandler.send(cinfo);
		return null;
	}

	private ConsistencyInfo getInterfaceConsistencyInfo() {
		ConsistencyApi consistencyApi = consistencyInterface.getAnnotation(ConsistencyApi.class);
		ConsistencyInfo cinfo = new ConsistencyInfo();
		cinfo.setConfirmLevel(consistencyApi.confirmLevel());
		cinfo.setRetryInterval(consistencyApi.retryInterval());
		cinfo.setDestination(PathUtils.regularPath(consistencyApi.path()));
		return cinfo;
	}

	private void initConsistencyInfo(ConsistencyInfo cinfo, ConsistencyApi consistencyApi) {
		if (consistencyApi.confirmLevel() != ConfirmLevel.DEFAULT) {
			cinfo.setConfirmLevel(consistencyApi.confirmLevel());
		}
		if (cinfo.getConfirmLevel() == ConfirmLevel.DEFAULT) {
			cinfo.setConfirmLevel(ConfirmLevel.SENT);
		}

		if (consistencyApi.retryInterval() != 0) {
			cinfo.setRetryInterval(consistencyApi.retryInterval());
		}
		if (consistencyApi.retryInterval() == 0) {
			cinfo.setRetryInterval(60);
		}
		String path = consistencyApi.path();
		path = cinfo.getDestination() + PathUtils.regularPath(path);
		cinfo.setDestination(PathUtils.getConsistencyPath(path));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<T> getObjectType() {
		return this.consistencyInterface;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * @return the consistencyInterface
	 */
	public Class<T> getConsistencyInterface() {
		return consistencyInterface;
	}

	/**
	 * @param consistencyInterface
	 *            the consistencyInterface to set
	 */
	public void setConsistencyInterface(Class<T> consistencyInterface) {
		this.consistencyInterface = consistencyInterface;
	}

	/**
	 * @param consistencyHandler
	 *            the consistencyHandler to set
	 */
	public void setConsistencyHandler(ConsistencyHandler consistencyHandler) {
		this.consistencyHandler = consistencyHandler;
	}

}
