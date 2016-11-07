package cn.newphy.commons.consistency.invoker;

public interface ConsistencyInvokerRegistrar {

	/**
	 * 注册一致性Invoker
	 * 
	 * @param path
	 * @param invoker
	 */
	<T> void registerInvoker(String destination, ConsistencyInvoker<T> invoker);

}
