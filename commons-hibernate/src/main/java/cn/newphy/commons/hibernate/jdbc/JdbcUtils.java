package cn.newphy.commons.hibernate.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

public class JdbcUtils {

	public static int javaType2SqlType(Class<?> javaType) {
		if (javaType.equals(String.class)) {
			return Types.VARCHAR;
		}
		if (javaType.equals(int.class) || javaType.equals(Integer.class)) {
			return Types.INTEGER;
		}
		if (javaType.equals(long.class) || javaType.equals(Long.class)) {
			return Types.BIGINT;
		}
		if (javaType.equals(double.class) || javaType.equals(Double.class)) {
			return Types.DOUBLE;
		}
		if (javaType.equals(Date.class) || javaType.equals(Timestamp.class)) {
			return Types.TIMESTAMP;
		}
		if (javaType.equals(byte.class) || javaType.equals(Byte.class)) {
			return Types.TINYINT;
		}
		if (javaType.equals(short.class) || javaType.equals(Short.class)) {
			return Types.SMALLINT;
		}
		if (javaType.equals(float.class) || javaType.equals(Float.class)) {
			return Types.FLOAT;
		}
		if (javaType.equals(BigDecimal.class)) {
			return Types.DECIMAL;
		}
		if (javaType.equals(Time.class)) {
			return Types.TIME;
		}
		if (javaType.equals(byte[].class) || javaType.equals(Byte[].class)) {
			return Types.BINARY;
		}
		return Types.OTHER;
	}

	/**
	 * 关闭连接
	 * 
	 * @param conn
	 */
	public static void closeQuietly(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
			conn = null;
		}
	}

	/**
	 * 关闭ResultSet
	 * 
	 * @param rs
	 */
	public static void closeQuietly(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
			rs = null;
		}
	}

	/**
	 * 关闭ResultSet, Statement
	 * 
	 * @param rs
	 * @param stmt
	 */
	public static void closeQuietly(ResultSet rs, Statement stmt) {
		closeQuietly(rs);

		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
			}
			stmt = null;
		}
	}

	/**
	 * 关闭ResultSet, Statement, Connection
	 * 
	 * @param rs
	 * @param stmt
	 * @param conn
	 */
	public static void closeQuietly(ResultSet rs, Statement stmt, Connection conn) {
		closeQuietly(rs, stmt);
		closeQuietly(conn);
	}

}
