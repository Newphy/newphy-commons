package cn.newphy.data.hibernate;

import java.lang.reflect.Field;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

public class AutowiredEntityDaoAnnotationProcessor implements
		BeanPostProcessor, BeanFactoryAware {

	private EntityDaoFactory entityDaoFactory;
	@SuppressWarnings("unused")
	private BeanFactory beanFactory;

	@Override
	public Object postProcessBeforeInitialization(final Object bean,
			String beanName) throws BeansException {
		Class<?> clazz = getRealClass(bean);
		if (!clazz.isAnnotationPresent(Service.class)) {
			return bean;
		}

		if (entityDaoFactory == null) {
			throw new BeanInitializationException(
					"entityDaoFactory is not specified");
		}

		ReflectionUtils.doWithFields(clazz, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				Class<?> entityClass = cn.newphy.commons.lang.ReflectionUtils
						.getFieldGenericType(field, 0);
				if (entityClass == null) {
					throw new BeanInitializationException(fieldInfo(field)
							+ "is an EntityDao, but not specify generic type");
				}

				ReflectionUtils.makeAccessible(field);
				if (ReflectionUtils.getField(field, bean) == null) {
					EntityDao<?> entityDao = EntityDaoFactory.create(entityClass);
					ReflectionUtils.setField(field, bean, entityDao);
				}
			}
		}, new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				return field.isAnnotationPresent(Dao.class)
						&& EntityDao.class.equals(field.getType());
			}
		});
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean,
			String beanName) throws BeansException {
		return bean;
	}

	/**
	 * @param entityDaoFactory
	 *            the entityDaoFactory to set
	 */
	public void setEntityDaoFactory(EntityDaoFactory entityDaoFactory) {
		this.entityDaoFactory = entityDaoFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org
	 * .springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;

	}

	private String fieldInfo(Field field) {
		return field.getDeclaringClass().getCanonicalName() + "."
				+ field.getName();
	}

	private Class<?> getRealClass(Object bean) {
		return AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean
				.getClass();
	}

}
