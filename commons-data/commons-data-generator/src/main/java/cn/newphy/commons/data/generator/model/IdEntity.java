package cn.newphy.commons.data.generator.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class IdEntity implements Serializable {

	private String id;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
}
