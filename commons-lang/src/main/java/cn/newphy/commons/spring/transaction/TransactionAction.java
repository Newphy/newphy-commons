package cn.newphy.commons.spring.transaction;

public interface TransactionAction {

	/**
	 * 执行操作
	 * 
	 * @param status
	 *            事务状态, 查看{@code TransactionSynchronization.STATUS_*}, 只有在afterCompletion操作中，这个参数才有意义
	 */
	void execute(int status) throws Exception;
}
