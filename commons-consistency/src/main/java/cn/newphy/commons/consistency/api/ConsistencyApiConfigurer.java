package cn.newphy.commons.consistency.api;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

public class ConsistencyApiConfigurer implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

	private ApplicationContext applicationContext;
	private String basePackage;
	private String consistencyHandlerName;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// ignore
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		if (StringUtils.isEmpty(basePackage)) {
			return;
		}
		ConsistencyApiScanner scanner = new ConsistencyApiScanner(registry);
		scanner.setConsistencyHandlerName(this.consistencyHandlerName);
		scanner.setResourceLoader(this.applicationContext);
		scanner.registerFilters();
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}

	/**
	 * @return the basePackage
	 */
	public String getBasePackage() {
		return basePackage;
	}

	/**
	 * @param basePackage
	 *            the basePackage to set
	 */
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	/**
	 * @return the consistencyHandlerName
	 */
	public String getConsistencyHandlerName() {
		return consistencyHandlerName;
	}

	/**
	 * @param consistencyHandlerName
	 *            the consistencyHandlerName to set
	 */
	public void setConsistencyHandlerName(String consistencyHandlerName) {
		this.consistencyHandlerName = consistencyHandlerName;
	}

}
