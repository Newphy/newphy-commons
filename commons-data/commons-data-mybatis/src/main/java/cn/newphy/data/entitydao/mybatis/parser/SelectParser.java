package cn.newphy.data.entitydao.mybatis.parser;

import cn.newphy.data.domain.Pageable;
import cn.newphy.data.entitydao.SqlBuilder;

public abstract class SelectParser implements SqlBuilder {

	protected final String sql;
	
	public SelectParser(String sql) {
		this.sql = sql;
	}
	
	
	public abstract String getPageSql(Pageable pageable, String offsetPlaceholder, String limitPlaceholder);
	
	
	public abstract String getCountSql();
}
