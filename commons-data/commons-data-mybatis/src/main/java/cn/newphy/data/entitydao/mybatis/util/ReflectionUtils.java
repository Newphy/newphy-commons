package cn.newphy.data.entitydao.mybatis.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectionUtils {

	
	public static <T extends Annotation> T getAnnotationFromProperty(Class<?> beanClass, String propertyName, Class<T> annotationType) {
		if(beanClass == null || propertyName == null || propertyName.length() == 0) {
			return null;
		}
		try {
			Field field = beanClass.getDeclaredField(propertyName);
			T annotation = field.getAnnotation(annotationType);
			return annotation;
		} catch (Exception e) {
		}
		
		try {
			PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass);
			T annotation = pd.getReadMethod().getAnnotation(annotationType);
			return annotation;
		} catch (IntrospectionException e) {
			return null;
		}
	}
}
