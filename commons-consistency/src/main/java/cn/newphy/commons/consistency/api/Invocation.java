package cn.newphy.commons.consistency.api;

import java.io.Serializable;
import java.lang.reflect.Method;

public class Invocation implements Serializable {
	private static final long serialVersionUID = -2537074089870907843L;

	// 接口名称
	private String interfaceName;

	// 方法名
	private String methodName;

	// 类型参数名
	private String[] parameterTypeNames;

	// 参数对象
	private Object[] args;

	public Invocation() {

	}

	public Invocation(Method method, Object[] args) {
		this.interfaceName = method.getDeclaringClass().getName();
		this.methodName = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		this.parameterTypeNames = new String[method.getParameterTypes().length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypeNames[i] = parameterTypes[i].getName();
		}
		this.args = args;
	}

	/**
	 * @return the interfaceName
	 */
	public String getInterfaceName() {
		return interfaceName;
	}

	/**
	 * @param interfaceName
	 *            the interfaceName to set
	 */
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName
	 *            the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the parameterTypeNames
	 */
	public String[] getParameterTypeNames() {
		return parameterTypeNames;
	}

	/**
	 * @param parameterTypeNames
	 *            the parameterTypeNames to set
	 */
	public void setParameterTypeNames(String[] parameterTypeNames) {
		this.parameterTypeNames = parameterTypeNames;
	}

	/**
	 * @return the args
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * @param args
	 *            the args to set
	 */
	public void setArgs(Object[] args) {
		this.args = args;
	}

}
