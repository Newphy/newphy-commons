package cn.newphy.commons.consistency.mq.invoker;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.SessionAwareMessageListener;

import cn.newphy.commons.consistency.ConfirmLevel;
import cn.newphy.commons.consistency.ConsistencyMessage;
import cn.newphy.commons.consistency.ConsistencyReplyMessage;
import cn.newphy.commons.consistency.invoker.ConsistencyInvoker;
import cn.newphy.commons.consistency.invoker.ConsistencyModel;
import cn.newphy.commons.consistency.util.JsonHelper;

public class ConsistencyInvokerMessageListener implements SessionAwareMessageListener<Message> {
	private Logger logger = LoggerFactory.getLogger(ConsistencyInvokerMessageListener.class);

	private static JsonHelper jsonHelper = JsonHelper.getWithType();
	private ConsistencyInvoker consistencyInvoker;

	public ConsistencyInvokerMessageListener(ConsistencyInvoker consistencyInvoker) {
		this.consistencyInvoker = consistencyInvoker;
	}

	@Override
	public void onMessage(Message message, Session session) throws JMSException {
		if (!(message instanceof TextMessage)) {
			logger.error("consistency only support TextMessage type, message={}", message);
			throw new IllegalStateException("unsupported message type for message type");
		}
		TextMessage textMessage = (TextMessage) message;
		String content = textMessage.getText();
		ConsistencyMessage cm = jsonHelper.toObject(content, ConsistencyMessage.class);
		boolean success = false;
		try {
			ConsistencyModel model = new ConsistencyModel(cm.getAttributes());
			consistencyInvoker.invoke(model);
			success = true;
		} catch (Exception e) {
			logger.warn("execute consistency message error", e);
			success = false;
		}
		if(cm.getConfirmLevel() == ConfirmLevel.EXECUTED.getValue()) {
			TextMessage replyMessage = session.createTextMessage();
			ConsistencyReplyMessage crm = new ConsistencyReplyMessage();
			crm.setTxId(cm.getTxId());
			crm.setSuccess(success);
			crm.setExecuteTime(new Date());
			String replyJson = jsonHelper.toJson(crm);
			replyMessage.setText(replyJson);
			
            MessageProducer producer = session.createProducer(message.getJMSReplyTo());
            producer.send(replyMessage);  
		}
	}
}
