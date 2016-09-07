package cn.newphy.commons.consistency.invoker;

public interface ConsistencyInvokerRegistrar {

	/**
	 * 注册一致性Invoker
	 * 
	 * @param path
	 * @param invoker
	 */
	void registerInvoker(String path, ConsistencyInvoker invoker);
	
	/**
	 * 启动
	 */
	void start();
}
