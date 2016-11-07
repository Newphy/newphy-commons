package cn.newphy.commons.consistency.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.newphy.commons.consistency.invoker.ConsistencyInvoker;

public class ConsistencyMethodInvoker extends ConsistencyInvoker<Invocation> {

	private final Object target;
	private final Method method;

	public ConsistencyMethodInvoker(Object target, Method method) {
		this.target = target;
		this.method = method;
	}

	public ConsistencyMethodInvoker(Object target, String methodName, Object[] args) throws NoSuchMethodException {
		this.target = target;
		this.method = target.getClass().getMethod(methodName);
	}

	public Object getTarget() {
		return this.target;
	}

	public Method getMethod() {
		return this.method;
	}

	@Override
	public void invoke(Invocation invocation) throws Exception {
		Object[] args = invocation.getArgs();
		String[] parameterTypeNames = invocation.getParameterTypeNames();
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof JSONObject) {
				JSONObject jobj = (JSONObject) args[i];
				Class<?> parameterType = Class.forName(parameterTypeNames[i]);
				args[i] = JSON.toJavaObject(jobj, parameterType);
			}
		}
		try {
			ReflectionUtils.makeAccessible(this.method);
			this.method.invoke(this.target, args);
		} catch (InvocationTargetException ex) {
			ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
		} catch (IllegalAccessException ex) {
			throw new UndeclaredThrowableException(ex);
		}
	}

}
