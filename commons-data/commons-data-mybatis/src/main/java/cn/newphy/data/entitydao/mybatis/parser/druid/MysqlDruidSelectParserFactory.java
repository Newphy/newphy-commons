package cn.newphy.data.entitydao.mybatis.parser.druid;

import cn.newphy.data.entitydao.mybatis.parser.SelectParser;
import cn.newphy.data.entitydao.mybatis.parser.SqlParserFactory.SelectParserFactory;

public class MysqlDruidSelectParserFactory implements SelectParserFactory {

	@Override
	public SelectParser createSqlParser(String sql) {
		return new MysqlDruidSelectParser(sql);
	}

}
