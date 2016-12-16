package cn.newphy.data.domain;

import java.io.Serializable;

import org.springframework.util.StringUtils;

public class Order implements Serializable {
	private static final long serialVersionUID = 1522511010900108987L;

	public static final Direction DEFAULT_DIRECTION = Direction.ASC;

	private final Direction direction;
	private final String property;
	private String column;

	
	public static Order asc(String property) {
		return new Order(Direction.ASC, property);
	}
	
	public static Order desc(String property) {
		return new Order(Direction.DESC, property);
	}
	
	public Order(String property) {
		this(DEFAULT_DIRECTION, property);
	}

	public Order(Direction direction, String property) {
		if (!StringUtils.hasText(property)) {
			throw new IllegalArgumentException("Property must not null or empty!");
		}

		this.direction = direction == null ? DEFAULT_DIRECTION : direction;
		this.property = property;
		this.column = property;
	}

	public Direction getDirection() {
		return direction;
	}

	public String getProperty() {
		return property;
	}

	public boolean isAscending() {
		return this.direction.isAscending();
	}

	public boolean isDescending() {
		return this.direction.isDescending();
	}

	/**
	 * @return the column
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * @param column
	 *            the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	@Override
	public String toString() {
		String result = String.format("%s: %s", property, direction);
		return result;
	}
}
