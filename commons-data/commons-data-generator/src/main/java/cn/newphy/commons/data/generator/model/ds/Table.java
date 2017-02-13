package cn.newphy.commons.data.generator.model.ds;

import java.util.ArrayList;
import java.util.List;

public class Table {
	private String name;
	private String comment;
	private List<Column> columns = new ArrayList<Column>();
	private Column primaryKey;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return the columns
	 */
	public List<Column> getColumns() {
		return columns;
	}
	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	/**
	 * @return the primaryKey
	 */
	public Column getPrimaryKey() {
		return primaryKey;
	}
	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(Column primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	
	
}
