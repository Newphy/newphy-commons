package cn.newphy.commons.consistency;

import cn.newphy.commons.consistency.util.IEnum;

public enum ConfirmLevel implements IEnum {
	DEFAULT(0, "缺省"),
	SENT(1, "发送保证一致性"), 
	EXECUTED(2, "最终执行保证一致性");

	private int level;
	private String description;

	private ConfirmLevel(int level, String description) {
		this.level = level;
		this.description = description;
	}

	/**
	 * @return the level
	 */
	public int getValue() {
		return level;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	
	
}
