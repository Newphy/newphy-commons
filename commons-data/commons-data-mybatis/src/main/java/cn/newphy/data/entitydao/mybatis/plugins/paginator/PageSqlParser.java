package cn.newphy.data.entitydao.mybatis.plugins.paginator;

import cn.newphy.data.domain.Pageable;
import cn.newphy.data.entitydao.DialectType;
import cn.newphy.data.entitydao.mybatis.parser.SelectParser;
import cn.newphy.data.entitydao.mybatis.parser.SqlParserFactory;

public class PageSqlParser {

	private SelectParser selectParser;
	
	public PageSqlParser(String sql, DialectType dialectType) {
		this.selectParser = SqlParserFactory.getSelectParser(sql, dialectType);
	}
	
	public String getPageSql(Pageable pageable) {
		String sql = selectParser.getPageSql(pageable, "?", "?");
		return sql;
	}
	
	public String getCountSql() {
		return selectParser.getCountSql();
	}
}
