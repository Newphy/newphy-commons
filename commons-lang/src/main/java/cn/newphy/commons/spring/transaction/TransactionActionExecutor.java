package cn.newphy.commons.spring.transaction;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

import cn.newphy.commons.spring.transaction.TransactionListener.TransactionPhase;

public class TransactionActionExecutor implements BeanDefinitionRegistryPostProcessor {
	
	@Autowired
	private TransactionListener transactionListener;

	/**
	 * 在事务提交之前增加操作
	 * <p>这个操作在beforeCompletion之前
	 * @param action
	 */
	public void beforeCommit(TransactionAction action) {
		transactionListener.addTransactionAction(TransactionPhase.BEFORE_COMMIT, action);
	}

	/**
	 * 在事务提交/回滚之前增加操作
	 * @param action
	 */
	public void beforeCompletion(TransactionAction action) {
		transactionListener.addTransactionAction(TransactionPhase.BEFORE_COMPLETION, action);
	}

	
	/**
	 * 在事务提交后增加操作
	 * <p>这个操作在afterCompletion之前发生
	 * @param action
	 */
	public void afterCommit(TransactionAction action) {
		transactionListener.addTransactionAction(TransactionPhase.AFTER_COMMIT, action);
	}


	/**
	 * 事务提交/回滚后增加操作
	 * @param action
	 */
	public void afterCompletion(TransactionAction action) {
		transactionListener.addTransactionAction(TransactionPhase.AFTER_COMPLETION, action);
	}


	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	    // left intentionally blank
	}


	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		BeanDefinition definition = new RootBeanDefinition(TransactionListener.class);
		registry.registerBeanDefinition("transactionSynchronizationListener", definition);
	}

}
