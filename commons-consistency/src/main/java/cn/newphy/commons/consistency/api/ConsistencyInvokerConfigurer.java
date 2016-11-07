package cn.newphy.commons.consistency.api;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.StringUtils;

import cn.newphy.commons.consistency.invoker.ConsistencyInvokerRegistrar;

public class ConsistencyInvokerConfigurer implements BeanPostProcessor, InitializingBean {
	private Logger logger = LoggerFactory.getLogger(ConsistencyInvokerConfigurer.class);

	private ConsistencyInvokerRegistrar invokerRegistrar;


	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(invokerRegistrar, "invokerRegistrar属性为空");
	}

	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
		final Class<?> targetClass = AopUtils.getTargetClass(bean);
		if (!targetClass.isAnnotationPresent(ConsistencyComponent.class)) {
			return bean;
		}

		logger.debug("~~~ register consistency invoker in class[{}] ~~~", targetClass.getName());
		ReflectionUtils.doWithMethods(targetClass, new MethodCallback() {
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				if(method.isAnnotationPresent(ConsistencyIgnore.class)) {
					return;
				}
				if (AopUtils.isJdkDynamicProxy(bean)) {
					try {
						// found a @ConsistencyExecutor method on the
						// target class for this JDK proxy -> is it
						// also present on the proxy itself?
						method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
					} catch (SecurityException ex) {
						ReflectionUtils.handleReflectionException(ex);
					} catch (NoSuchMethodException ex) {
						throw new IllegalStateException(
								String.format(
										"@ConsistencyComponent method '%s' found on bean target class '%s', "
												+ "but not found in any interface(s) for bean JDK proxy. Either "
												+ "pull the method up to an interface or switch to subclass (CGLIB) "
												+ "proxies by setting proxy-target-class/proxyTargetClass "
												+ "attribute to 'true'",
										method.getName(), targetClass.getSimpleName()));
					}
				}

				String destination = null;
				ConsistencyMapping consistencyMapping = AnnotationUtils.getAnnotation(method, ConsistencyMapping.class);
				if(consistencyMapping != null) {
					destination = consistencyMapping.value();
					invokerRegistrar.registerInvoker(destination, new ConsistencyMethodInvoker(bean, method));
				}
				else {
					Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
					for (Class<?> interfaceType : interfaces) {
						if (ConsistencyApi.class.isAssignableFrom(interfaceType)) {
							try {
								Method apiMethod = interfaceType.getMethod(method.getName(),
										method.getParameterTypes());
								if (apiMethod != null && apiMethod.isAnnotationPresent(Consistency.class)) {
									Consistency consistency = apiMethod.getAnnotation(Consistency.class);
									destination = consistency.value();
									if(StringUtils.isEmpty(destination)) {
										continue;
									}
									invokerRegistrar.registerInvoker(destination, new ConsistencyMethodInvoker(bean, method));
								}
							} catch (NoSuchMethodException | SecurityException e) {
								continue;
							}

						}
					}
				}
			}
		});
		return bean;
	}

	/**
	 * @return the invokerRegistrar
	 */
	public ConsistencyInvokerRegistrar getInvokerRegistrar() {
		return invokerRegistrar;
	}

	/**
	 * @param invokerRegistrar
	 *            the invokerRegistrar to set
	 */
	public void setInvokerRegistrar(ConsistencyInvokerRegistrar invokerRegistrar) {
		this.invokerRegistrar = invokerRegistrar;
	}

}
