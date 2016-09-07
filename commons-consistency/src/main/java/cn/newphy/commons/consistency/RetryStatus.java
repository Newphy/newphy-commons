package cn.newphy.commons.consistency;

import cn.newphy.commons.consistency.util.IEnum;

public enum RetryStatus implements IEnum {

	NO(0, "新建"), YES(1, "发送成功");

	private int status;
	private String description;

	private RetryStatus(int status, String description) {
		this.status = status;
		this.description = description;
	}

	/**
	 * @return the level
	 */
	public int getValue() {
		return status;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
