package cn.newphy.commons.data.generator.utils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Types;

public class TypeUtils {

	
	
	public static Class<?> jdbcType2JavaType(int jdbcType, int scale, int length, boolean decimalful) {
		switch(jdbcType) {
		case Types.BIT:
		case Types.BOOLEAN:
			return Boolean.class;
		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.INTEGER:
			return Integer.class;
		case Types.BIGINT:
			return Long.class;
		case Types.FLOAT:
		case Types.REAL:
		case Types.DOUBLE:
		case Types.NUMERIC:
		case Types.DECIMAL:
			return decimalful ? BigDecimal.class : Double.class;
		case Types.CHAR:
		case Types.NCHAR:
		case Types.VARCHAR:
		case Types.NVARCHAR:
		case Types.LONGVARCHAR:
		case Types.LONGNVARCHAR:
		case Types.SQLXML:
		case Types.CLOB:
		case Types.NCLOB:
			return String.class;
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return Date.class;
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
		case Types.BLOB:
			return byte[].class;
		default: 
			return Object.class;
		}
	}

}
