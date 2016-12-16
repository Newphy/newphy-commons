package cn.newphy.data.entitydao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

public class EntityDaoAnnotationProcessor implements InitializingBean,
		BeanPostProcessor, BeanFactoryAware {

	private EntityDaoFactory entityDaoFactory;
	@SuppressWarnings("unused")
	private BeanFactory beanFactory;

	@Override
	public Object postProcessBeforeInitialization(final Object bean,
			String beanName) throws BeansException {
		Class<?> clazz = getRealClass(bean);
		ReflectionUtils.doWithFields(clazz, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				Class<?> entityClass = getFieldGenericType(field);
				if (entityClass == null) {
					throw new BeanInitializationException(fieldInfo(field)
							+ "is an EntityDao, but not specify generic type");
				}

				ReflectionUtils.makeAccessible(field);
				if (ReflectionUtils.getField(field, bean) == null) {
					EntityDao<?> entityDao = entityDaoFactory.createEntityDao(entityClass);
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
	public void afterPropertiesSet() throws Exception {
		if (entityDaoFactory == null) {
			throw new BeanInitializationException("entityDaoFactory is not specified");
		}
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

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;

	}
	
	private Class<?> getFieldGenericType(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
        	ParameterizedType parameterizedType = (ParameterizedType) type;
        	Type[] argumentTypes = parameterizedType.getActualTypeArguments();
        	return (Class<?>) argumentTypes[0];
        }
        return null;
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
