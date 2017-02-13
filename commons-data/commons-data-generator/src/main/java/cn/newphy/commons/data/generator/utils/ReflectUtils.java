package cn.newphy.commons.data.generator.utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectUtils {
	private static final Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

	public static boolean isPrimitive(Class<?> type) {
		return type.isPrimitive() || type == String.class
				|| type == Character.class || type == Boolean.class
				|| type == Byte.class || type == Short.class
				|| type == Integer.class || type == Long.class
				|| type == Float.class || type == Double.class
				|| type == Object.class;
	}

	public static Method getGetMethod(Object obj,String fileName){
		if(fileName==null||obj==null){
			return null;
		}
		Method method = null;
		try{
			Class<?> clazz = obj.getClass();
			String getMethodName =  "get"+fileName.substring(0,1).toUpperCase()+fileName.substring(1);
			method =clazz.getDeclaredMethod(getMethodName, new Class<?>[]{});
			if(method==null){
				getMethodName =  "is"+fileName.substring(0,1).toUpperCase()+fileName.substring(1);
				 method = clazz.getDeclaredMethod(getMethodName, new Class<?>[]{});
			}
		}catch(Exception e){
			logger.error("find "+fileName+" get method error!",e);
		}
		return method;
	}
	
    /**
     * 获得类的泛型定义.
     *
     * <p>
     *     ex. public class List<String> {}
     *     return String.class
     * </p>
     *
     * @param clazz
     * @param index
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static Class getSuperClassGenericType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
             return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 获得类的泛型定义.
     *
     * <p>
     * ex. public class List<String> {} return String.class
     * </p>
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperClassGenericType(final Class<?> clazz) {
        return getSuperClassGenericType(clazz, 0);
    }
	
}
