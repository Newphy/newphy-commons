package cn.newphy.data.entitydao.mybatis.builder;

import cn.newphy.data.entitydao.DialectType;

public class MysqlSqlBuilder extends MybatisSqlBuilder {
	
	private static final String LIMIT = " LIMIT ";

	public MysqlSqlBuilder() {
		super(DialectType.MYSQL);
	}
	

	@Override
	public CharSequence getLimitString(int offset, int limit) {
		StringBuffer sql = new StringBuffer(LIMIT);
		if(offset > 0) {
			sql.append(offset).append(" ,");
		}
		sql.append(limit);
		return sql;
	}


	@Override
	public boolean supportsLimit() {
		return true;
	}


	@Override
	public boolean supportsLimitOffset() {
		return true;
	}



	@Override
	public CharSequence getLimitString(String offsetPlaceholder, String limitPlaceholder) {
		StringBuffer sql = new StringBuffer(LIMIT);
		if(offsetPlaceholder != null) {
			sql.append(offsetPlaceholder).append(" ,");
		}
		sql.append(limitPlaceholder);
		return sql;	
	}
	
	
	
}
