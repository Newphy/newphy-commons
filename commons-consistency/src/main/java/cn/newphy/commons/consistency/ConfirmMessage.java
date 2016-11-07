package cn.newphy.commons.consistency;

import java.io.Serializable;
import java.util.Date;

public class ConfirmMessage implements Serializable {
	private static final long serialVersionUID = -6407558119865999640L;
	// 事物编号
	private String txId;
	// 执行服务器信息
	private String executeHost;
	// 执行结果
	private boolean success;
	// 执行时间
	private Date executeTime;

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
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
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
	 * @return the executeHost
	 */
	public String getExecuteHost() {
		return executeHost;
	}

	/**
	 * @param executeHost
	 *            the executeHost to set
	 */
	public void setExecuteHost(String executeHost) {
		this.executeHost = executeHost;
	}

}
