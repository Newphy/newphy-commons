package cn.newphy.commons.consistency;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import cn.newphy.commons.consistency.util.UUID;

public class ConsistencyInfo implements Serializable {
	private static final long serialVersionUID = -1160875210173788524L;
	
	// 编号
	private long id;
	// 事物编号
	private String txId;
	// 生成时间
	private Date createTime;
	// 确认级别(1:发送/2:执行)
	private ConfirmLevel confirmLevel;
	// 确认状态(0:新建/1:发送确认/2:执行确认)
	private ConfirmStatus confirmStatus;
	// 同步时间
	private Date syncTime;
	// 确认时间间隔(秒)
	private int retryInterval;
	// 重新同步时间
	private Date retryTime;
	// 重试状态(0:不需要/1:需要)
	private RetryStatus retryStatus;
	// 发送确认时间
	private Date sentTime;
	// 执行确认时间
	private Date executeTime;
	// 消息目标
	private String destination;
	// 消息内容
	private String content;
	// 失败原因
	private String failCause;
	
	public ConsistencyInfo() {
		this.txId = UUID.generate();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the txId
	 */
	public String getTxId() {
		return txId;
	}

	/**
	 * @param txId
	 *            the txId to set
	 */
	public void setTxId(String txId) {
		this.txId = txId;
	}

	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the confirmLevel
	 */
	public ConfirmLevel getConfirmLevel() {
		return confirmLevel;
	}

	/**
	 * @param confirmLevel
	 *            the confirmLevel to set
	 */
	public void setConfirmLevel(ConfirmLevel confirmLevel) {
		this.confirmLevel = confirmLevel;
	}

	/**
	 * @return the confirmStatus
	 */
	public ConfirmStatus getConfirmStatus() {
		return confirmStatus;
	}

	/**
	 * @param confirmStatus
	 *            the confirmStatus to set
	 */
	public void setConfirmStatus(ConfirmStatus confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	/**
	 * @return the syncTime
	 */
	public Date getSyncTime() {
		return syncTime;
	}

	/**
	 * @param syncTime
	 *            the syncTime to set
	 */
	public void setSyncTime(Date syncTime) {
		this.syncTime = syncTime;
	}

	/**
	 * @return the retryInterval
	 */
	public int getRetryInterval() {
		return retryInterval;
	}

	/**
	 * @param retryInterval
	 *            the retryInterval to set
	 */
	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	/**
	 * @return the retryTime
	 */
	public Date getRetryTime() {
		return retryTime;
	}

	/**
	 * @param retryTime
	 *            the retryTime to set
	 */
	public void setRetryTime(Date retryTime) {
		this.retryTime = retryTime;
	}

	/**
	 * @return the retryStatus
	 */
	public RetryStatus getRetryStatus() {
		return retryStatus;
	}

	/**
	 * @param retryStatus
	 *            the retryStatus to set
	 */
	public void setRetryStatus(RetryStatus retryStatus) {
		this.retryStatus = retryStatus;
	}

	/**
	 * @return the sentTime
	 */
	public Date getSentTime() {
		return sentTime;
	}

	/**
	 * @param sentTime
	 *            the sentTime to set
	 */
	public void setSentTime(Date sentTime) {
		this.sentTime = sentTime;
	}

	/**
	 * @return the executeTime
	 */
	public Date getExecuteTime() {
		return executeTime;
	}

	/**
	 * @param executeTime
	 *            the executeTime to set
	 */
	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the failCause
	 */
	public String getFailCause() {
		return failCause;
	}

	/**
	 * @param failCause
	 *            the failCause to set
	 */
	public void setFailCause(String failCause) {
		this.failCause = failCause;
	}

}
