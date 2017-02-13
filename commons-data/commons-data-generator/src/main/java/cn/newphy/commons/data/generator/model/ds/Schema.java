package cn.newphy.commons.data.generator.model.ds;

import java.io.Serializable;
import java.util.List;

public class Schema implements Serializable {
	private static final long serialVersionUID = 9199409136614914131L;

	private String name;
	private List<Table> tables;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the tables
	 */
	public List<Table> getTables() {
		return tables;
	}

	/**
	 * @param tables
	 *            the tables to set
	 */
	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

}
