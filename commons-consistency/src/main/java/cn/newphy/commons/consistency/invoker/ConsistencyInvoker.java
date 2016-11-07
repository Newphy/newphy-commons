package cn.newphy.commons.consistency.invoker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 一致性触发接口
 * 
 * @author xn044401
 *
 */
public abstract class ConsistencyInvoker<T> {
	private final Type type;

	public ConsistencyInvoker() {
		Type superClass = getClass().getGenericSuperclass();
		type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
	}

	public abstract void invoke(T obj) throws Exception;

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

}
