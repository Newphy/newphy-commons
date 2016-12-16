package cn.newphy.data.entitydao.mybatis.parser;

import cn.newphy.data.entitydao.DialectType;
import cn.newphy.data.entitydao.mybatis.parser.druid.MysqlDruidSelectParserFactory;

public abstract class SqlParserFactory {

	private static SelectParserFactory selectParserFactory = null;
	
	/**
	 * 获取SelectParser
	 * @param dialectType
	 * @return
	 */
	public static SelectParser getSelectParser(String sql, DialectType dialectType) {
		if(selectParserFactory == null) {
			synchronized (SqlParserFactory.class) {
				if(selectParserFactory == null) {
					if(dialectType == DialectType.MYSQL) {
						if(existClass("com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser")) {
							selectParserFactory = new MysqlDruidSelectParserFactory();
						}
					}
				}
			}
		}
		return selectParserFactory.createSqlParser(sql);
	}

	private static boolean existClass(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	
	public static interface SelectParserFactory {
		SelectParser createSqlParser(String sql);

	}
	
}
