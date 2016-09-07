package cn.newphy.commons.consistency.invoker;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.StringUtils;

import cn.newphy.commons.consistency.api.ConsistencyApi;
import cn.newphy.commons.consistency.util.PathUtils;

public class ConsistencyInvokerConfigurer implements BeanPostProcessor, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
	private Logger logger = LoggerFactory.getLogger(ConsistencyInvokerConfigurer.class);

	private ConsistencyInvokerRegistrar invokerRegistrar;
	private ApplicationContext applicationContext;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
		final Class<?> targetClass = AopUtils.getTargetClass(bean);
		if (!targetClass.isAnnotationPresent(Consistency.class)) {
			return bean;
		}

		logger.debug("~~~ register consistency invoker in class[{}] ~~~", targetClass.getName());
		Consistency consistency = targetClass.getAnnotation(Consistency.class);
		final String rootPath = PathUtils.regularPath(consistency.path());
		ReflectionUtils.doWithMethods(targetClass, new MethodCallback() {
			public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
				Consistency annotation = AnnotationUtils.getAnnotation(method, Consistency.class);
				if (annotation == null) {
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
										"@Consistency method '%s' found on bean target class '%s', "
												+ "but not found in any interface(s) for bean JDK proxy. Either "
												+ "pull the method up to an interface or switch to subclass (CGLIB) "
												+ "proxies by setting proxy-target-class/proxyTargetClass "
												+ "attribute to 'true'",
										method.getName(), targetClass.getSimpleName()));
					}
				}

				String path = annotation.path();
				if (StringUtils.hasText(path)) {
					path = rootPath + PathUtils.regularPath(path);
					invokerRegistrar.registerInvoker(path, new ConsistencyMethodInvoker(bean, method));
				} else {
					Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
					for (Class<?> interfaceType : interfaces) {
						if (interfaceType.isAnnotationPresent(ConsistencyApi.class)) {
							try {
								Method apiMethod = interfaceType.getMethod(method.getName(),
										method.getParameterTypes());
								if (apiMethod != null && apiMethod.isAnnotationPresent(ConsistencyApi.class)) {
									path = PathUtils.getConsistencyApiPath(apiMethod);
									invokerRegistrar.registerInvoker(path, new ConsistencyMethodInvoker(bean, method));
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
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext() == applicationContext) {
			invokerRegistrar.start();
		}
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
