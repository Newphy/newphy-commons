package cn.newphy.commons.consistency.mq;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cn.newphy.commons.consistency.ConfirmLevel;
import cn.newphy.commons.consistency.ConfirmMessage;
import cn.newphy.commons.consistency.exception.ConsistencyException;
import cn.newphy.commons.consistency.invoker.ConsistencyInvoker;
import cn.newphy.commons.consistency.invoker.ConsistencyInvokerRegistrar;

public class MQConsistencyInvokerRegistrar implements ConsistencyInvokerRegistrar, InitializingBean {
	private Logger logger = LoggerFactory.getLogger(MQConsistencyInvokerRegistrar.class);

	private String hostInfo;
	
	private ConnectionFactory connectionFactory;

	private Map<String, DefaultMessageListenerContainer> messageListenerContainers = new LinkedHashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(connectionFactory, "没有设置connectionFactory");
		InetAddress address = InetAddress.getLocalHost();
		this.hostInfo = address.getHostName() + "[" + address.getHostAddress() + "]";
	}


	@Override
	public <T> void registerInvoker(String destination, ConsistencyInvoker<T> invoker) {
		logger.debug("~~~ register invoker(destination={}, invoker={}) ~~~", destination, invoker);
		if (messageListenerContainers.containsKey(destination)) {
			DefaultMessageListenerContainer exist = messageListenerContainers.get(destination);
			throw new IllegalStateException("exist duplicated ConsistencyInvoker for path[" + destination + "], there are: "
					+ invoker.getClass().getName() + " and " + exist.getMessageListener());
		}
		DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
		messageListenerContainer.setConnectionFactory(this.connectionFactory);
		messageListenerContainer.setAutoStartup(false);
		messageListenerContainer.setDestinationName(destination);
		messageListenerContainer.setSessionTransacted(true);
		messageListenerContainer.setMessageListener(new ConsistencyInvokerMessageListener<T>(invoker));
		messageListenerContainer.afterPropertiesSet();
		messageListenerContainers.put(destination, messageListenerContainer);
		messageListenerContainer.start();
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

	
	private class ConsistencyInvokerMessageListener<T> implements SessionAwareMessageListener<Message> {
		private ConsistencyInvoker<T> consistencyInvoker;
		
		public ConsistencyInvokerMessageListener(ConsistencyInvoker<T> consistencyInvoker) {
			this.consistencyInvoker = consistencyInvoker;
		}

		@Override
		public void onMessage(Message message, Session session) throws JMSException {
			if (!(message instanceof TextMessage)) {
				logger.error("一致性消息只支持TextMessage类型, message={}", message);
				throw new IllegalStateException("unsupported message type for message type");
			}
			
			TextMessage textMessage = (TextMessage) message;
			String txId = textMessage.getStringProperty(MQKeys.TX_ID);
			Integer confirmLevel = textMessage.getIntProperty(MQKeys.CONFIRM_LEVEL);
			boolean success = false;
			try {
				final Type type = consistencyInvoker.getType();
				String content = textMessage.getText();
				logger.info("收到一致性消息, txId={}, confirmLevel={}, content={}", txId, confirmLevel, content);
				T obj = JSON.parseObject(content, new TypeReference<T>(){
					@Override
					public Type getType() {
						return type;
					}
				});
				consistencyInvoker.invoke(obj);
				success = true;
			} catch (Exception e) {
				success = false;
				logger.warn("执行一致性消息出错", e);
				throw new ConsistencyException("执行一致性操作发生异常", e);
			}
			if(confirmLevel != null && confirmLevel == ConfirmLevel.EXECUTED.ordinal()) {
				Destination confirmDestination = message.getJMSReplyTo();
				if(confirmDestination == null) {
					throw new IllegalStateException("not specify the confirm destination");
				}
				TextMessage replyMessage = session.createTextMessage();
				ConfirmMessage confirmMessage = new ConfirmMessage();
				confirmMessage.setTxId(txId);
				confirmMessage.setSuccess(success);
				confirmMessage.setExecuteTime(new Date());
				confirmMessage.setExecuteHost(hostInfo);
				String confirmJson = JSON.toJSONString(confirmMessage);
				replyMessage.setText(confirmJson);
				
	            MessageProducer producer = session.createProducer(message.getJMSReplyTo());
	            producer.send(replyMessage);  
	            logger.info("发送一致性确认消息,txId={}", txId);
			}
		}
		
	}
}
