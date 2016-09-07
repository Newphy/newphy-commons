package cn.newphy.commons.spring.transaction;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

public class TransactionListener extends TransactionSynchronizationAdapter {
	private Logger logger = LoggerFactory.getLogger(TransactionListener.class);

	enum TransactionPhase {
		BEFORE_COMMIT, BEFORE_COMPLETION, AFTER_COMMIT, AFTER_COMPLETION;
	}

	private TransactionActionContext beforeCommitContext = new TransactionActionContext();
	private TransactionActionContext beforeCompletionContext = new TransactionActionContext();
	private TransactionActionContext afterCommitContext = new TransactionActionContext();
	private TransactionActionContext afterCompletionContext = new TransactionActionContext();

	public void addTransactionAction(TransactionPhase phase, TransactionAction action) {
		switch (phase) {
		case BEFORE_COMMIT: {
			beforeCommitContext.addAction(action);
			break;
		}
		case BEFORE_COMPLETION: {
			beforeCompletionContext.addAction(action);
			break;
		}
		case AFTER_COMMIT: {
			afterCommitContext.addAction(action);
			break;
		}
		case AFTER_COMPLETION: {
			afterCompletionContext.addAction(action);
			break;
		}
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void beforeCommit(boolean readOnly) {
		List<TransactionAction> actions = beforeCommitContext.actionsTL.get();
		executeTransactionActions(actions, TransactionSynchronization.STATUS_UNKNOWN);
	}

	@Override
	public void beforeCompletion() {
		List<TransactionAction> actions = beforeCompletionContext.actionsTL.get();
		executeTransactionActions(actions, TransactionSynchronization.STATUS_UNKNOWN);
	}

	@Override
	public void afterCommit() {
		List<TransactionAction> actions = afterCommitContext.actionsTL.get();
		executeTransactionActions(actions, TransactionSynchronization.STATUS_COMMITTED);
	}

	@Override
	public void afterCompletion(int status) {
		List<TransactionAction> actions = afterCompletionContext.actionsTL.get();
		executeTransactionActions(actions, status);
		// 清空
		beforeCommitContext.clear();
		beforeCompletionContext.clear();
		afterCommitContext.clear();
		afterCompletionContext.clear();
	}
	
	
	private void executeTransactionActions(List<TransactionAction> actions, int status) {
		if(actions != null) {
			for (TransactionAction action : actions) {
				try {
					action.execute(status);
				} catch (Exception e) {
					logger.error("execute transaction action error", e);
				}
			}
		}
	}

	private class TransactionActionContext {
		private ThreadLocal<List<TransactionAction>> actionsTL = new ThreadLocal<List<TransactionAction>>();

		/**
		 * 增加Action
		 * 
		 * @param action
		 */
		public void addAction(TransactionAction action) {
			if (actionsTL.get() == null) {
				actionsTL.set(new ArrayList<TransactionAction>());
			}
			actionsTL.get().add(action);
		}

		/**
		 * 清空
		 */
		public void clear() {
			actionsTL.set(null);
		}

	}

}
