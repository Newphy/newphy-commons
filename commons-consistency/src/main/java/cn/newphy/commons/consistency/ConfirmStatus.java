package cn.newphy.commons.consistency;

import cn.newphy.commons.consistency.util.IEnum;

public enum ConfirmStatus implements IEnum {

	NEW(0, "新建"), SENT(1, "发送成功"), EXECUTED(2, "执行成功");

	private int status;
	private String description;

	private ConfirmStatus(int status, String description) {
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
