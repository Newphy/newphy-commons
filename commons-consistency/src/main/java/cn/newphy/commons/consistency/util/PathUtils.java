package cn.newphy.commons.consistency.util;

import java.lang.reflect.Method;

import org.springframework.util.StringUtils;

import cn.newphy.commons.consistency.api.ConsistencyApi;

public class PathUtils {
	
	public static String getConsistencyPath(String path) {
		return "consistency[" + path + "]";
	}

	public static String regularPath(String path) {
		if (StringUtils.isEmpty(path)) {
			return path;
		}
		path = StringUtils.trimAllWhitespace(path);
		path = StringUtils.trimLeadingCharacter(path, '/');
		path = StringUtils.trimTrailingCharacter(path, '/');
		return "/" + path;
	}

	public static String getConsistencyApiPath(Method method) {
		Class<?> type = method.getDeclaringClass();
		if (!type.isAnnotationPresent(ConsistencyApi.class) || !method.isAnnotationPresent(ConsistencyApi.class)) {
			return null;
		}
		return PathUtils.regularPath(type.getAnnotation(ConsistencyApi.class).path())
				+ PathUtils.regularPath(method.getAnnotation(ConsistencyApi.class).path());
	}
}
