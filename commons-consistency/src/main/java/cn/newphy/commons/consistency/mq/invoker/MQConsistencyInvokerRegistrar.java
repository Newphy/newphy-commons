package cn.newphy.commons.consistency.mq.invoker;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.Assert;

import cn.newphy.commons.consistency.invoker.ConsistencyInvoker;
import cn.newphy.commons.consistency.invoker.ConsistencyInvokerRegistrar;
import cn.newphy.commons.consistency.util.PathUtils;

public class MQConsistencyInvokerRegistrar implements ConsistencyInvokerRegistrar, InitializingBean {
	private Logger logger = LoggerFactory.getLogger(MQConsistencyInvokerRegistrar.class);

	private ConnectionFactory connectionFactory;

	private Map<String, DefaultMessageListenerContainer> messageListenerContainers = new LinkedHashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(connectionFactory, "没有设置connectionFactory");
	}

	@Override
	public void registerInvoker(String path, ConsistencyInvoker invoker) {
		logger.debug("~~~ register invoker(path={}, invoker={}) ~~~", path, invoker);
		String consistencyPath = PathUtils.getConsistencyPath(path);
		if (messageListenerContainers.containsKey(consistencyPath)) {
			DefaultMessageListenerContainer exist = messageListenerContainers.get(consistencyPath);
			throw new IllegalStateException("exist duplicated ConsistencyInvoker for path[" + path + "], there are: "
					+ invoker.getClass().getName() + " and " + exist.getMessageListener());
		}
		DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
		messageListenerContainer.setConnectionFactory(this.connectionFactory);
		messageListenerContainer.setAutoStartup(false);
		messageListenerContainer.setDestinationName(consistencyPath);
		messageListenerContainer.setMessageListener(new ConsistencyInvokerMessageListener(invoker));
		messageListenerContainer.afterPropertiesSet();
		messageListenerContainers.put(consistencyPath, messageListenerContainer);
	}

	@Override
	public void start() {
		for (DefaultMessageListenerContainer messageListenerContainer : messageListenerContainers.values()) {
			messageListenerContainer.start();
		}
	}

	/**
	 * @return the connectionFactory
	 */
	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	/**
	 * @param connectionFactory
	 *            the connectionFactory to set
	 */
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

}
