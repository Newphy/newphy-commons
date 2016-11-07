package cn.newphy.commons.consistency.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import cn.newphy.commons.consistency.ConfirmLevel;
import cn.newphy.commons.consistency.ConsistencyConst;
import cn.newphy.commons.consistency.ConsistencyInfo;
import cn.newphy.commons.consistency.handler.ConsistencyHandler;
import cn.newphy.commons.consistency.support.IDFetcher;

public class ConsistencyApiFactoryBean<T> implements FactoryBean<T> {
	private Logger logger = LoggerFactory.getLogger(ConsistencyApiFactoryBean.class);

	// 接口类
	private Class<T> consistencyInterface;

	private ConsistencyHandler consistencyHandler;

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

	private Object invokeConsistency(Method method, Object[] args) {
		Consistency consistency = method.getAnnotation(Consistency.class);
		if (consistency == null) {
			logger.warn("{}.{}() not present annotation ConsistencyMapping!", consistencyInterface.getName(),
					method.getName());
			return null;
		}
		ConsistencyInfo cinfo = getConsistencyInfo(consistency, method, args);
		Invocation invocation = new Invocation(method, args);
		cinfo.setContent(JSON.toJSONString(invocation));
		consistencyHandler.handle(cinfo);
		return null;
	}

	private ConsistencyInfo getConsistencyInfo(Consistency consistency, Method method, Object[] args) {
		ConsistencyInfo cinfo = new ConsistencyInfo();
		cinfo.setConfirmLevel(consistency.confirmLevel());
		if (cinfo.getConfirmLevel() == ConfirmLevel.DEFAULT) {
			cinfo.setConfirmLevel(ConfirmLevel.SENT);
		}
		cinfo.setRetryInterval(consistency.retryInterval());
		if (consistency.retryInterval() == 0) {
			cinfo.setRetryInterval(ConsistencyConst.DEFAULT_RETRY_INTERVAL);
		}
		String destination = consistency.value();
		if(StringUtils.isEmpty(destination)) {
			throw new IllegalArgumentException("ConsistencyMapping's value not allowed to be empty at "
					+ method.getDeclaringClass().getName() + "." + method.getName() + "()");
		}
		cinfo.setDestination(destination);
		
		// 获取业务编号
		if(args.length > 0) {
			cinfo.setBizId(IDFetcher.getId(args[0]));
		}
		return cinfo;
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
