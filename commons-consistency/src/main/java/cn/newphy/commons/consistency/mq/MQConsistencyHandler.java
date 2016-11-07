package cn.newphy.commons.consistency.mq;

import java.util.Date;
import java.util.List;

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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;

import cn.newphy.commons.consistency.ConfirmLevel;
import cn.newphy.commons.consistency.ConfirmMessage;
import cn.newphy.commons.consistency.ConfirmStatus;
import cn.newphy.commons.consistency.ConsistencyInfo;
import cn.newphy.commons.consistency.RetryStatus;
import cn.newphy.commons.consistency.handler.ConsistencyHandlerSupport;
import cn.newphy.commons.consistency.handler.ConsistencyObject;
import cn.newphy.commons.consistency.support.transaction.TransactionSynchronizationUtil;

public class MQConsistencyHandler extends ConsistencyHandlerSupport {
	private Logger logger = LoggerFactory.getLogger(MQConsistencyHandler.class);

	private static final int DEFAULT_RETRY_INTERVAL = 10 * 60;

	private JmsTemplate jmsTemplate;

	private ConnectionFactory connectionFactory;

	private String confirmDestination;
	
	private DefaultMessageListenerContainer confirmMessageListenerContainer;

	@Override
	public void handle(String destination, ConsistencyObject cobj) {
		this.handle(destination, cobj, ConfirmLevel.SENT);
	}
	
	

	@Override
	public void handle(String destination, ConsistencyObject cobj, ConfirmLevel confirmLevel) {
		if(cobj.getObject() ==  null) {
			throw new NullPointerException("一致性对象cobj为空");
		}
		ConsistencyInfo cinfo = createConsistencyInfo(destination, cobj);
		if(confirmLevel != null) {
			cinfo.setConfirmLevel(confirmLevel);
		}
		handle(cinfo);
	}

	

	@Override
	public void handle(String destination, Object obj) {
		this.handle(destination, obj, ConfirmLevel.SENT);
	}


	@Override
	public void handle(String destination, Object obj, ConfirmLevel confirmLevel) {
		if(obj == null) {
			throw new NullPointerException("一致性对象obj为空");
		}
		this.handle(destination, new IdConsistencyObject(obj), confirmLevel);		
	}



	@Override
	public void handle(final ConsistencyInfo cinfo) {
		// 保存消息
		Date firstTime = new Date();
		cinfo.setFirstSentTime(firstTime);
		cinfo.setRetryCount(0);
		cinfo.setRetryStatus(RetryStatus.YES);
		cinfo.setRetryTime(DateUtils.addSeconds(new Date(), cinfo.getRetryInterval()));
		cinfo.setConfirmDestination(confirmDestination);		
		cinfo.setConfirmStatus(ConfirmStatus.INTIAL);
		consistencyDao.addConsistency(cinfo);
		// 发送消息
		TransactionSynchronizationUtil.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				sendMessage(cinfo);
			}
		});
	}

	@Override
	public int compensate(int maxCount) {
		List<ConsistencyInfo> messages = consistencyDao.queryRetryList(maxCount);
		int success = 0;
		for (ConsistencyInfo message : messages) {
			ConsistencyInfo detail = consistencyDao.getDetail(message.getId());
			detail.setRetryCount(detail.getRetryCount() + 1);
			success += sendMessage(detail) ? 1 : 0;
		}
		return success;
	}
	

	private ConsistencyInfo createConsistencyInfo(String destination, ConsistencyObject cobj) {
		ConsistencyInfo cinfo = new ConsistencyInfo();
		cinfo.setDestination(destination);
		cinfo.setConfirmLevel(ConfirmLevel.SENT);
		cinfo.setRetryInterval(DEFAULT_RETRY_INTERVAL);
		cinfo.setBizId(cobj.getObjectId());
		cinfo.setConfirmDestination(this.confirmDestination);
		
		Object bizObj = cobj.getObject();
		cinfo.setContent(JSON.toJSONString(bizObj));
		return cinfo;
	}

	private boolean sendMessage(final ConsistencyInfo cinfo) {
		logger.info("~~~ 发送一致性消息, txId={}, cinfo={} ~~~", cinfo.getTxId(), cinfo);
		cinfo.setRetryTime(DateUtils.addSeconds(new Date(), cinfo.getRetryInterval()));
		try {
			MessageCreator messageCreator = new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					TextMessage textMessage = session.createTextMessage();
					textMessage.setText(cinfo.getContent());
					textMessage.setStringProperty(MQKeys.TX_ID, cinfo.getTxId());
					textMessage.setIntProperty(MQKeys.CONFIRM_LEVEL, cinfo.getConfirmLevel().ordinal());
					// 判断是否需要回复
					if (cinfo.getConfirmLevel() == ConfirmLevel.EXECUTED) {
						if(StringUtils.isBlank(confirmDestination)) {
							throw new IllegalStateException("没有设置确认地址confirmDestination");
						}
						Destination destination = jmsTemplate.getDestinationResolver().resolveDestinationName(session,
								confirmDestination, false);
						textMessage.setJMSReplyTo(destination);
					}
					return textMessage;
				}
			};
			// Queue
			jmsTemplate.send(cinfo.getDestination(), messageCreator);

			// 更新确认发送消息
			cinfo.setConfirmSentTime(new Date());
			if (cinfo.getConfirmStatus() == ConfirmStatus.INTIAL) {
				cinfo.setConfirmStatus(ConfirmStatus.SENT);
			}

			// 判断是否需要重新发送
			cinfo.setRetryStatus((cinfo.getConfirmLevel() == ConfirmLevel.SENT) ? RetryStatus.NO : RetryStatus.YES);
			cinfo.setFailCause(null);
			consistencyDao.updateConsistency(cinfo);
			logger.info("~~~ 发送一致性消息成功, txId={}, message={} ~~~", cinfo.getTxId(), cinfo);
			return true;
		} catch (Exception e) {
			logger.error("~~~ 发送一致性消息出错, txId={}, message={} ~~~", cinfo.getTxId(), cinfo, e);
			Throwable t = ExceptionUtils.getRootCause(e);
			String error = t == null ? e.getMessage() : t.getMessage();
			cinfo.setFailCause(StringUtils.substring(error, 0, 100));
			consistencyDao.updateConsistency(cinfo);
			return false;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.isTrue(StringUtils.isNotBlank(confirmDestination), "没有设置确认队列confirmDestination");

		if (jmsTemplate == null && connectionFactory == null) {
			throw new IllegalStateException("没有设置消息队列ConnectionFactory");
		}
		if (jmsTemplate == null) {
			this.jmsTemplate = new JmsTemplate(this.connectionFactory);
		}
		if(StringUtils.isNotBlank(confirmDestination)) {
			createConfirmMessageListener();
		}
	}

	private void createConfirmMessageListener() {
		if(confirmMessageListenerContainer != null) {
			return;
		}
		confirmMessageListenerContainer = new DefaultMessageListenerContainer();
		confirmMessageListenerContainer.setConnectionFactory(getConnectionFactory());
		confirmMessageListenerContainer.setAutoStartup(false);
		confirmMessageListenerContainer.setDestinationName(confirmDestination);
		confirmMessageListenerContainer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				try {
					TextMessage txtMessage = (TextMessage) message;
					String replyJson = txtMessage.getText();
					logger.info("~~~ 收到一致性确认消息, confirmJson={}", replyJson);
					ConfirmMessage crm = JSON.parseObject(replyJson, ConfirmMessage.class);
					if(crm.isSuccess()) {
						String txId = crm.getTxId();
						ConsistencyInfo cinfo = consistencyDao.getConsistencyByTxId(txId);
						cinfo.setConfirmStatus(ConfirmStatus.EXECUTED);
						cinfo.setConfirmExecuteTime(crm.getExecuteTime());
						cinfo.setExecuteHost(crm.getExecuteHost());
						cinfo.setRetryStatus(RetryStatus.NO);
						consistencyDao.updateConsistency(cinfo);
					}
				} catch (JMSException e) {
					logger.warn("~~~ 收到一致性确认消息出错 ~~~", e);
				}
			}
		});
		confirmMessageListenerContainer.afterPropertiesSet();
		confirmMessageListenerContainer.start();
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

	/**
	 * @return the confirmDestination
	 */
	public String getConfirmDestination() {
		return confirmDestination;
	}

	/**
	 * @param confirmDestination
	 *            the confirmDestination to set
	 */
	public void setConfirmDestination(String confirmDestination) {
		this.confirmDestination = confirmDestination;
	}

	

}
