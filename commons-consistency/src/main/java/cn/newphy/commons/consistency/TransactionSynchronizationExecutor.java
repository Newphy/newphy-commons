package cn.newphy.commons.consistency;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class TransactionSynchronizationExecutor {

	/**
	 * 注册事务同步器
	 * @param synchronization
	 */
	public static void registerSynchronization(TransactionSynchronization synchronization) {
		if(synchronization != null) {
			if(TransactionSynchronizationManager.isSynchronizationActive()) {
				TransactionSynchronizationManager.registerSynchronization(synchronization);
			}
			else {
				// 马上执行
				synchronization.beforeCommit(false);
				synchronization.beforeCompletion();
				synchronization.afterCommit();
				synchronization.afterCompletion(TransactionSynchronization.STATUS_UNKNOWN);
			}
		}
	}

}
