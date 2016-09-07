package cn.newphy.commons.consistency.mq;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

import cn.newphy.commons.consistency.ConfirmLevel;
import cn.newphy.commons.consistency.ConfirmStatus;
import cn.newphy.commons.consistency.ConsistencyHandler;
import cn.newphy.commons.consistency.ConsistencyHandlerSupport;
import cn.newphy.commons.consistency.ConsistencyInfo;
import cn.newphy.commons.consistency.ConsistencyReplyMessage;
import cn.newphy.commons.consistency.RetryStatus;
import cn.newphy.commons.consistency.TransactionSynchronizationExecutor;
import cn.newphy.commons.consistency.util.JsonHelper;

public class MQConsistencyHandler extends ConsistencyHandlerSupport implements InitializingBean, ConsistencyHandler {
	private Logger logger = LoggerFactory.getLogger(MQConsistencyHandler.class);

	private JmsTemplate jmsTemplate;

	private ConnectionFactory connectionFactory;

	private JsonHelper jsonHelper = JsonHelper.getWithType();

	private Map<String, DefaultMessageListenerContainer> replyMessageListeners = new HashMap<>();

	private Object replyMonitor = new Object();

	@Override
	public void send(String target, Object obj) {
		ConsistencyInfo message = createConsistencyMessage(target, obj, ConfirmLevel.SENT, DEFAULT_RETRY_INTERVAL);
		send(message);
	}

	@Override
	public void send(final ConsistencyInfo cinfo) {
		// 保存消息
		cinfo.setConfirmStatus(ConfirmStatus.NEW);
		cinfo.setRetryStatus(RetryStatus.YES);
		cinfo.setRetryTime(DateUtils.addSeconds(new Date(), cinfo.getRetryInterval()));
		consistencyDAO.addConsistency(cinfo);
		// 发送消息
		TransactionSynchronizationExecutor.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				sendMessage(cinfo);
			}
		});
	}

	@Override
	public int compensate(int maxCount) {
		List<ConsistencyInfo> messages = consistencyDAO.queryRetryList(maxCount);
		int success = 0;
		for (ConsistencyInfo message : messages) {
			ConsistencyInfo detail = consistencyDAO.getDetail(message.getId());
			success += sendMessage(detail) ? 1 : 0;
		}
		return success;
	}

	private ConsistencyInfo createConsistencyMessage(String target, Object obj, ConfirmLevel level, int retryInterval) {
		ConsistencyInfo message = new ConsistencyInfo();
		message.setDestination(target);
		message.setConfirmLevel(level);
		message.setRetryInterval(retryInterval);
		message.setContent(jsonHelper.toJson(obj));

		return message;
	}

	private boolean sendMessage(final ConsistencyInfo cinfo) {
		logger.info("~~~ 发送一致性消息, txId={}, cinfo={} ~~~", cinfo.getTxId(), cinfo);
		Date syncTime = new Date();
		cinfo.setSyncTime(syncTime);
		cinfo.setRetryTime(DateUtils.addSeconds(syncTime, cinfo.getRetryInterval()));
		try {
			MessageCreator messageCreator = new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					TextMessage textMessage = session.createTextMessage();
					textMessage.setText(cinfo.getContent());
					textMessage.setStringProperty("source", getSource());
					// 判断是否需要回复
					if (cinfo.getConfirmLevel() == ConfirmLevel.EXECUTED) {
						String replyDestination = "reply-" + cinfo.getDestination();
						Destination destination = jmsTemplate.getDestinationResolver().resolveDestinationName(session,
								replyDestination, false);
						textMessage.setJMSReplyTo(destination);
						if (!replyMessageListeners.containsKey(replyDestination)) {
							createReplyMessageListener(replyDestination);
						}
					}
					return textMessage;
				}
			};

			// Queue
			jmsTemplate.send(cinfo.getDestination(), messageCreator);

			// 更新确认消息
			cinfo.setSentTime(new Date());
			if (cinfo.getConfirmStatus() == ConfirmStatus.NEW) {
				cinfo.setConfirmStatus(ConfirmStatus.SENT);
			}

			// 判断是否需要重新发送
			cinfo.setRetryStatus((cinfo.getConfirmLevel() == ConfirmLevel.SENT) ? RetryStatus.NO : RetryStatus.YES);
			cinfo.setFailCause(null);
			consistencyDAO.updateConsistency(cinfo);
			logger.info("~~~ 发送一致性消息成功, txId={}, message={} ~~~", cinfo.getTxId(), cinfo);
			return true;
		} catch (Exception e) {
			logger.error("~~~ 发送一致性消息出错, txId={}, message={} ~~~", cinfo.getTxId(), cinfo, e);
			Throwable t = ExceptionUtils.getRootCause(e);
			String error = t == null ? e.getMessage() : t.getMessage();
			cinfo.setFailCause(StringUtils.substring(error, 0, 100));
			consistencyDAO.updateConsistency(cinfo);
			return false;
		}
	}

	private void createReplyMessageListener(String replyDestination) {
		synchronized (replyMonitor) {
			if (!replyMessageListeners.containsKey(replyDestination)) {
				DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
				messageListenerContainer.setConnectionFactory(getConnectionFactory());
				messageListenerContainer.setAutoStartup(false);
				messageListenerContainer.setDestinationName(replyDestination);
				messageListenerContainer.setMessageListener(new MessageListener() {
					@Override
					public void onMessage(Message message) {
						try {
							TextMessage txtMessage = (TextMessage) message;
							String replyJson = txtMessage.getText();
							ConsistencyReplyMessage crm = jsonHelper.toObject(replyJson, ConsistencyReplyMessage.class);
							String txId = crm.getTxId();
							ConsistencyInfo cinfo = consistencyDAO.getConsistencyByTxId(txId);
							cinfo.setConfirmStatus(ConfirmStatus.EXECUTED);
							cinfo.setExecuteTime(crm.getExecuteTime());
							cinfo.setRetryStatus(RetryStatus.NO);
							consistencyDAO.updateConsistency(cinfo);
						} catch (JMSException e) {
							logger.warn("~~~ 收到一致性回复消息出错 ~~~", e);
						}
					}
				});
				messageListenerContainer.afterPropertiesSet();
				messageListenerContainer.start();
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		if (jmsTemplate == null && connectionFactory == null) {
			throw new IllegalStateException("未设置MessageQueue");
		}
		this.jmsTemplate = new JmsTemplate(this.connectionFactory);
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
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
