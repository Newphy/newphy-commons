package cn.newphy.commons.consistency.support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

import cn.newphy.commons.consistency.BizId;

public class IDFetcher {

	/**
	 * 获得编号
	 * @param obj
	 * @return
	 */
	public static String getId(final Object obj) {
		if(obj == null) {
			return null;
		}
		final Set<Object> set = new HashSet<Object>();
		Class<?> clazz = obj.getClass();
		ReflectionUtils.doWithFields(clazz, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				try {
					if(PropertyUtils.isReadable(obj, field.getName())) {
						Object value = PropertyUtils.getProperty(obj, field.getName());
						set.add(value == null ? "" : value.toString());						
					}
				} catch (Exception e) {
					set.add("");
				}
			}
		}, new FieldFilter(){
			@Override
			public boolean matches(Field field) {
				return field.isAnnotationPresent(BizId.class);
			}}
		);
		
		Object value = set.isEmpty() ? null : set.iterator().next();
		if(value == null) {
			PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
			for (int i = 0; i < pds.length; i++) {
				if(pds[i].getReadMethod() != null && pds[i].getReadMethod().isAnnotationPresent(BizId.class)) {
					try {
						value = PropertyUtils.getProperty(obj, pds[i].getName());
					} catch (Exception e) {
						value = "";
					}
					break;
				}
			}
		}
		
		if(value == null) {
			try {
				value = PropertyUtils.getProperty(obj, "id");
			} catch (Exception e) {
				value = null;
			}
		}
		return value == null ? "" : value.toString();
	}
}
