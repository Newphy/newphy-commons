package cn.newphy.commons.consistency.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import org.springframework.util.ReflectionUtils;

public class ConsistencyMethodInvoker implements ConsistencyInvoker {

	private final Object target;
	private final Method method;

	public ConsistencyMethodInvoker(Object target, Method method) {
		this.target = target;
		this.method = method;
	}

	public ConsistencyMethodInvoker(Object target, String methodName,  Object[] args) throws NoSuchMethodException {
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
	public void invoke(ConsistencyModel model) throws Exception {
		try {
			ReflectionUtils.makeAccessible(this.method);
			Object[] args = model.values().toArray();
			this.method.invoke(this.target, args);
		} catch (InvocationTargetException ex) {
			ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
		} catch (IllegalAccessException ex) {
			throw new UndeclaredThrowableException(ex);
		}
	}

}
