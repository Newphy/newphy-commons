package cn.newphy.commons.consistency;

public interface ConsistencyHandler {
	// 缺省重试间隔（300s）
	public final static int DEFAULT_RETRY_INTERVAL = 5 * 60;

	/**
	 * 设置源地址
	 * 
	 * @param source
	 */
	void setSource(String source);

	/**
	 * 一致性处理
	 * 
	 * @param target
	 * @param obj
	 */
	void send(String target, Object obj);

	/**
	 * 一致性处理
	 * 
	 * @param message
	 */
	void send(ConsistencyInfo message);

	/**
	 * 补偿操作
	 * 
	 * @param maxCount
	 * @return 补偿数量
	 */
	int compensate(int maxCount);

	
	
}
