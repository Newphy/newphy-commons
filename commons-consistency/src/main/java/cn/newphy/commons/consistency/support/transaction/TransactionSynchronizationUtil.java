package cn.newphy.commons.consistency.support.transaction;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 事务同步管理工具
 * 
 * @author liuhui18
 * @time 2016年10月31日
 * @Copyright (c) 深圳市牛鼎丰科技有限公司-版权所有.
 */
public class TransactionSynchronizationUtil {

	/**
	 * 注册事务同步器
	 * 
	 * @param synchronization
	 */
	public static void registerSynchronization(TransactionSynchronization synchronization) {
		if (synchronization != null) {
			// 有事务管理，注册事务同步器
			if (TransactionSynchronizationManager.isSynchronizationActive()) {
				TransactionSynchronizationManager.registerSynchronization(synchronization);
			} else {
				// 如果没有事务管理，马上执行
				synchronization.beforeCommit(false);
				synchronization.beforeCompletion();
				synchronization.afterCommit();
				synchronization.afterCompletion(TransactionSynchronization.STATUS_UNKNOWN);
			}
		}
	}

}
