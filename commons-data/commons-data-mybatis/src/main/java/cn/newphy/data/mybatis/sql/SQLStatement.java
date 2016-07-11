package cn.newphy.data.mybatis.sql;

public interface SQLStatement {
	
	public static final String INSERT = "INSERT INTO ";
	public static final String VALUES = " VALUES ";
	public static final String UPDATE = "UPDATE ";
	public static final String SET = "SET ";
	public static final String WHERE = "WHERE ";
	public static final String SELECT = "SELECT ";
	public static final String AND = " AND ";
	public static final String OR = " OR ";
	public static final String ORDER_BY = "ORDER BY ";
	/**
	 * 生成SQL
	 * @return
	 */
	String generateSQL();
}
