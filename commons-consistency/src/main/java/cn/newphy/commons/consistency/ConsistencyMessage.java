package cn.newphy.commons.consistency;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConsistencyMessage implements Serializable {
	private static final long serialVersionUID = -6407558119865999640L;

	// 事物编号
	private String txId;

	// 确认级别
	private int confirmLevel;

	// 参数
	private Map<String, Object> attributes = new LinkedHashMap<>();

	/**
	 * 增加参数
	 * 
	 * @param key
	 * @param value
	 */
	public void addAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	/**
	 * @return the confirmLevel
	 */
	public int getConfirmLevel() {
		return confirmLevel;
	}

	/**
	 * @param confirmLevel
	 *            the confirmLevel to set
	 */
	public void setConfirmLevel(int confirmLevel) {
		this.confirmLevel = confirmLevel;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
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

}
