package cn.newphy.commons.consistency.support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;

public class IDFetcher {

	/**
	 * 获得编号
	 * @param obj
	 * @return
	 */
	public static String getId(Object obj) {
		if(obj == null) {
			return null;
		}
		try {
			PropertyDescriptor idProperty = BeanUtils.getPropertyDescriptor(obj.getClass(), "id");
			if(idProperty != null) {
				Method readMethod = idProperty.getReadMethod();
				Object id = readMethod.invoke(obj, new Object[0]);
				return id.toString();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
