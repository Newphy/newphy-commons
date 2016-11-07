package cn.newphy.commons.consistency.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;

public class ConsistencyApiScanner extends ClassPathBeanDefinitionScanner {

	private String consistencyHandlerName;

	public ConsistencyApiScanner(BeanDefinitionRegistry registry) {
		super(registry, false);
	}

	public void registerFilters() {
		addIncludeFilter(new AssignableTypeFilter(ConsistencyApi.class));
		// exclude package-info.java
		addExcludeFilter(new TypeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
					throws IOException {
				String className = metadataReader.getClassMetadata().getClassName();
				return className.endsWith("package-info");
			}
		});
	}


	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (beanDefinitions.isEmpty()) {
			logger.warn("No consistency interface was found in '" + Arrays.toString(basePackages) + "' package. ");
		} else {
			processBeanDefinitions(beanDefinitions);
		}
		return beanDefinitions;
	}

	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		GenericBeanDefinition definition;
		for (BeanDefinitionHolder holder : beanDefinitions) {
			definition = (GenericBeanDefinition) holder.getBeanDefinition();
			if (logger.isDebugEnabled()) {
				logger.debug("Creating ConsistencyMappingFactoryBean with name '" + holder.getBeanName() + "' and '"
						+ definition.getBeanClassName() + "' consistencyInterface");
			}
			definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName()); // issue
			definition.setBeanClass(ConsistencyApiFactoryBean.class);
			definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
			if (StringUtils.hasText(this.consistencyHandlerName)) {
				definition.getPropertyValues().add("consistencyHandler",
						new RuntimeBeanReference(this.consistencyHandlerName));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
		if (super.checkCandidate(beanName, beanDefinition)) {
			return true;
		} else {
			logger.warn("Skipping ConsistencyFactoryBean with name '" + beanName + "' and '"
					+ beanDefinition.getBeanClassName() + "' consistencyInterface"
					+ ". Bean already defined with the same name!");
			return false;
		}
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
